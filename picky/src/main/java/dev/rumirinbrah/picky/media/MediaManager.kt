package dev.rumirinbrah.picky.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MediaManager(
    private val scope: CoroutineScope ,
    private val context: Context
) {

    private val _state = MutableStateFlow(ImagePickerState())
    val state = _state.asStateFlow()

    private val allRecentImages = mutableListOf<GalleryImage>()
    private var recentImagesLoadingJob: Job? = null
    private var recentsPage = 0

    private var allSelectedAlbumImages = listOf<GalleryImage>()
    private var albumImagesPage = 0

    private val pageSize = 30
    private var endReached: Boolean = false
    private var albumEndReached : Boolean = false

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
            ImagePickerAction.LoadAlbumImagesNextPage ->{
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
            //--------SELECT IMG-------
            is ImagePickerAction.SelectImage -> {
                selectImage(action.image)
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
            val end = (start+pageSize).coerceAtMost(allRecentImages.size)
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
    private fun setAlbum(albumName : String){
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
            return
        }
        if(_state.value.loadingAlbumImages){
            return
        }
        log {
            "Loading album images..."
        }
        scope.launch {

            val start = albumImagesPage * pageSize
            if( start> allSelectedAlbumImages.size){
                log {
                    "loadAlbumImages : End reached"
                }
                albumEndReached = true
                return@launch
            }
            val end = (start+pageSize).coerceAtMost(allSelectedAlbumImages.size)
            _state.update {
                it.copy(loadingAlbumImages = true)
            }
            val images = allSelectedAlbumImages.subList(start,end)

            log {
                "loadAlbumImages : Page $albumImagesPage loaded"
            }
            _state.update {
                it.copy(
                    loadingAlbumImages = false ,
                    albumImages = images
                )
            }

        }
    }
    private fun clearAlbumImages(){
        scope.launch {
            log {
                "clearAlbumImages : Clearing..."
            }
            allSelectedAlbumImages = emptyList()
            _state.update {
                it.copy(
                    loadingAlbumImages = false ,
                    albumImages = emptyList() ,
                    selectedAlbum = "Unknown"
                )
            }
        }
    }

    private fun selectImage(imageUri : Uri){
        scope.launch {
            _state.update {
                it.copy(selectedImage = imageUri)
            }
        }
    }

    private fun triggerTabRow(visible : Boolean) {
        _state.update {
            it.copy(tabRowVisible = visible)
        }
    }

    private fun clearData(){
        //onCleared()
        log {
            "Clearing media manager..."
        }
        allRecentImages.clear()
        allSelectedAlbumImages = emptyList()
        recentsPage = 0
        albumImagesPage = 0
        recentImagesLoadingJob?.cancel()

        endReached = false
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