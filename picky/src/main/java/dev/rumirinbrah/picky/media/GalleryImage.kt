package dev.rumirinbrah.picky.media

import android.net.Uri
/**
 * @param id For lazy containers
 * @author zyzz
*/
internal data class GalleryImage(
    val id : Long,
    val image : Uri,
    val albumName : String ="Unknown"
)

internal data class GalleryAlbum(
    val albumName : String,
    val coverImage : GalleryImage
)
