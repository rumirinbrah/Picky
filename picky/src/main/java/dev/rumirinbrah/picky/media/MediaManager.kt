package dev.rumirinbrah.picky.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import dev.rumirinbrah.picky.api.PickyOption
import dev.rumirinbrah.picky.util.MediaManagerEvents
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class MediaManager(
    private val scope: CoroutineScope ,
    private val context: Context
) {

    private val _state = MutableStateFlow(ImagePickerState())
    val state = _state.asStateFlow()

    private val _events = Channel<MediaManagerEvents>()
    val events = _events.receiveAsFlow()

    private val allRecentImages = mutableListOf<GalleryImage>()
    private var recentImagesLoadingJob: Job? = null
    private var recentsPage = 0

    private var allSelectedAlbumImages = listOf<GalleryImage>()
    private var albumImagesPage = 0

    private val pageSize = 30
    private var endReached: Boolean = false
    private var albumEndReached: Boolean = false

    init {
        log {
            "init..."
        }
    }

    fun onAction(action: ImagePickerAction) {
        when (action) {
            //--------LOAD INIT-------
            ImagePickerAction.Load -> {
                loadInit()
            }
            //--------LOAD ALBUM IMAGES-------
            is ImagePickerAction.LoadAlbumImages -> {
                setAlbum(action.albumName)
            }
            //--------LOAD ALBUM NXT PG-------
            ImagePickerAction.LoadAlbumImagesNextPage -> {
                loadAlbumImagesNextPage()
            }

            //--------CLEAR ALBUM-------
            ImagePickerAction.ClearAlbumImages -> {
                clearAlbumImages()
            }
            //--------RECENTS NEXT PG-------
            ImagePickerAction.LoadRecentsNextPage -> {
                loadRecentsNextPage()
            }
            //--------TAB ROW-------
            is ImagePickerAction.TriggerTabRow -> {
                triggerTabRow(action.visible)
            }
            //--------PICKY OPT-------
            is ImagePickerAction.SetPickyOption<*> -> {
                setPickyOption(action.option)
            }

            //--------SELECT IMG-------
            is ImagePickerAction.SelectImage -> {
                selectImage(action.image , action.id)
            }
            //--------PREV-------
            is ImagePickerAction.PreviewImage ->{
                previewImage(action.uri)
            }
            ImagePickerAction.ClearPreviewImage ->{
                clearPreviewImage()
            }

            ImagePickerAction.ConfirmSelection ->{
                confirmImagesSelection()
            }
            ImagePickerAction.ClearSelection -> {
                clearSelectedImages()
            }
            //--------CANCEL-------
            ImagePickerAction.CancelSelection -> {
                clearData()
            }
        }
    }

    /**
     * load all recents
     * @author zyzz
     */
    private fun loadInit() {
        if (allRecentImages.isNotEmpty()) {
            log {
                "Already loaded recents, returning..."
            }
            return
        }
        log {
            "Loading images..."
        }
        recentImagesLoadingJob = scope.launch {
            //--------load all from media store-------
            withContext(Dispatchers.IO) {
                try {

                    val projection = arrayOf(
                        MediaStore.Images.Media._ID ,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME ,
                    )
                    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
                    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    ensureActive()

                    context.contentResolver.query(
                        uri ,
                        projection ,
                        null ,
                        null ,
                        sortOrder
                    )?.use { cursor ->

                        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                        val albumCol =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                        while (cursor.moveToNext()) {
                            ensureActive()
                            val id = cursor.getLong(idCol)
                            val albumName = cursor.getString(albumCol) ?: "Unknown"
                            val contentUri = ContentUris.withAppendedId(uri , id)
                            allRecentImages.add(
                                GalleryImage(id , contentUri , albumName)
                            )
                        }

                    }
                } catch (_: CancellationException) {
                    return@withContext
                } catch (e: Exception) {
                    log {
                        "loadCameraImages : Error"
                    }
                    e.printStackTrace()
                    ensureActive()
                }
            }
            log {
                "Recents loaded"
            }
            //Load first page and album names
            loadRecentsNextPage()
            readyAlbums()
        }
    }


    //--------NEXT PAGE-------
    private fun loadRecentsNextPage() {
        if (endReached) {
            log {
                "loadRecentsNextPage : End reached"
            }
            return
        }
        if (_state.value.loading) {
            return
        }
        scope.launch {

            val start = recentsPage * pageSize
            if (start > allRecentImages.size) {
                log {
                    "End reached for recents"
                }
                endReached = true
                return@launch
            }
            //to ensure it doesnt overshoot
            val end = (start + pageSize).coerceAtMost(allRecentImages.size)
            _state.update {
                it.copy(loading = true)
            }
            log {
                "Recents ; Loading more..."
            }

            val nextPage = allRecentImages.subList(start , end)
            _state.update {
                it.copy(
                    images = it.images + nextPage ,
                    loading = false
                )
            }

            recentsPage += 1
            log {
                "Current page is $recentsPage"
            }

        }

    }

    private fun readyAlbums() {
        when {
            allRecentImages.isEmpty() -> {
                log {
                    "No recent images for making albums"
                }
                return
            }

            _state.value.albums.isNotEmpty() -> {
                log {
                    "Albums already fetched!"
                }
                return
            }
        }

        scope.launch {
            val tempAlbums = allRecentImages.groupBy {
                it.albumName
            }.map { (album , images) ->

                val coverImage = images.maxByOrNull { image ->
                    image.id
                } ?: images.first()

                GalleryAlbum(
                    albumName = album ,
                    coverImage = coverImage
                )
            }

            _state.update {
                it.copy(albums = tempAlbums)
            }
            log {
                "Albums mapped!!"
            }
        }
    }

    private fun setAlbum(albumName: String) {
        //here we set the album
        //load all the album images
        //load first page
        scope.launch {
            allSelectedAlbumImages = allRecentImages.filter {
                it.albumName == albumName
            }
            loadAlbumImagesNextPage()
            _state.update {
                it.copy(selectedAlbum = albumName)
            }
        }
    }

    private fun loadAlbumImagesNextPage() {
        if (allSelectedAlbumImages.isEmpty() || albumEndReached) {
            log {
                "loadAlbumImagesNextPage : returning..."
            }
            return
        }
        if (_state.value.loadingAlbumImages) {
            log{
                "loadAlbumImagesNextPage : already loading, returning...."
            }
            return
        }
        log {
            "Loading album images..."
        }
        scope.launch {

            val start = albumImagesPage * pageSize
            if (start > allSelectedAlbumImages.size) {
                log {
                    "loadAlbumImages : End reached"
                }
                albumEndReached = true
                return@launch
            }
            val end = (start + pageSize).coerceAtMost(allSelectedAlbumImages.size)
            _state.update {
                it.copy(loadingAlbumImages = true)
            }
            val images = allSelectedAlbumImages.subList(start , end)

            log {
                "loadAlbumImages : Page $albumImagesPage loaded"
            }
            albumImagesPage += 1
            _state.update {
                it.copy(
                    loadingAlbumImages = false ,
                    albumImages = it.albumImages + images
                )
            }

        }
    }

    private fun clearAlbumImages() {
        scope.launch {
            log {
                "clearAlbumImages : Clearing..."
            }
            allSelectedAlbumImages = emptyList()
            albumImagesPage = 0
            albumEndReached = false
            _state.update {
                it.copy(
                    loadingAlbumImages = false ,
                    albumImages = emptyList() ,
                    selectedAlbum = "Unknown"
                )
            }
        }
    }

    private fun selectImage(imageUri: Uri , id: Long) {
        scope.launch {
            if (_state.value.multiSelect) {
                selectMultipleImage(imageUri, id)
            } else {
                selectSingleImage(imageUri)
            }
        }
    }
    private fun previewImage(uri: Uri){
        scope.launch {
            _state.update {
                it.copy(previewImage = uri)
            }
        }
    }
    private fun clearPreviewImage(){
        scope.launch {
            _state.update {
                it.copy(previewImage = null)
            }
        }
    }

    private fun selectSingleImage(imageUri: Uri) {
        log {
            "selectSingleImage : Select single image $imageUri"
        }
        scope.launch {
            _state.update {
                it.copy(selectedImage = imageUri)
            }
            _events.send(MediaManagerEvents.OnImageSelect(imageUri))
        }
    }

    private fun selectMultipleImage(imageUri: Uri  , id : Long ) {
        log {
            "Add to multi-images"
        }
        scope.launch {
            _state.update {
                val updated = it.selectedImages.updateList(
                    imageUri ,
                    id ,
                    it.maxItems ,
                    onFull = {
                        _events.send(MediaManagerEvents.Error("You can only select upto ${it.maxItems} images"))
                        return@launch
                    }
                )
                it.copy(
                    selectedImages = updated
                )
            }
        }
    }

    private fun triggerTabRow(visible: Boolean) {
        _state.update {
            it.copy(tabRowVisible = visible)
        }
    }

    private fun <T> setPickyOption(option: PickyOption<T>) {
        log {
            "setPickyOption : Option is $option"
        }
        scope.launch {
            when (option) {
                is PickyOption.PickMultiple -> {

                    _state.update {
                        it.copy(
                            multiSelect = true,
                            maxItems = option.maxItems
                        )
                    }
                }

                PickyOption.PickSingle -> {
                    _state.update {
                        it.copy(
                            multiSelect = false
                        )
                    }
                }
            }
        }
    }

    //--------MULTI-SELECT-------
    private fun clearSelectedImages(){
        scope.launch {
            _state.update {
                it.copy(selectedImages = emptyList())
            }
        }
    }
    private fun confirmImagesSelection(){
        scope.launch {
            val values = _state.value
            val data = values.selectedImages.toUriList()
            _events.send(MediaManagerEvents.OnImagesSelect(data))
        }
    }

    private fun clearData() {
        //onCleared()
        log {
            "Clearing media manager..."
        }
        allRecentImages.clear()
        recentsPage = 0
        recentImagesLoadingJob?.cancel()
        endReached = false

        allSelectedAlbumImages = emptyList()
        albumImagesPage = 0
        albumEndReached = false

        _state.update {
            ImagePickerState()
        }
    }


    //TODO(remove in prod)
    private val loggingEnabled = true

    private fun log(msg: () -> String) {
        if (loggingEnabled) {
            Log.d("MediaManager : " , msg())
        }
    }

}

private inline fun List<GalleryImage>.updateList(
    imageUri: Uri ,
    id: Long,
    maxItems : Int,
    onFull : ()->Unit
) : List<GalleryImage>{
    return if(this.containsId(id)){
        this.filter {
            it.id != id
        }
    }else{
        if(this.size>=maxItems){
            onFull()
        }
        this + GalleryImage(id,imageUri)
    }
}
