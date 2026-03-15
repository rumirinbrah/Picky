package dev.rumirinbrah.picky.media

import android.net.Uri

/**
 * Used by media manager
 * @author zyzz
*/
internal data class ImagePickerState(
    val images : List<GalleryImage> = emptyList() ,
    val albums : List<GalleryAlbum> = emptyList() ,
    val albumImages : List<GalleryImage> = emptyList() ,
    val selectedImage : Uri? = null ,
    val selectedImages : List<GalleryImage> = emptyList() ,
    val selectedAlbum : String? = null ,
    val tabRowVisible : Boolean = true ,
    val loading : Boolean = false ,
    val loadingAlbumImages : Boolean = false ,
    val multiSelect : Boolean = false,
    val maxItems : Int = 0,
)
