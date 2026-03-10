package dev.rumirinbrah.picky.media

import android.net.Uri

internal sealed class ImagePickerAction {
    data class TriggerTabRow(val visible : Boolean) : ImagePickerAction()

    data object Load : ImagePickerAction()
    data object LoadRecentsNextPage : ImagePickerAction()

    data class SelectImage(val image : Uri) : ImagePickerAction()
    data object CancelSelection : ImagePickerAction()

    data class LoadAlbumImages(val albumName : String) : ImagePickerAction()
    data object LoadAlbumImagesNextPage : ImagePickerAction()

    data object ClearAlbumImages : ImagePickerAction()
}