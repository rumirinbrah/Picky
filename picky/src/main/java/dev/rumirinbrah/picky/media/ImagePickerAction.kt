package dev.rumirinbrah.picky.media

import android.net.Uri
import dev.rumirinbrah.picky.api.PickyOption

internal sealed class ImagePickerAction {
    data class TriggerTabRow(val visible : Boolean) : ImagePickerAction()
    data class SetPickyOption<T>(val option: PickyOption <T>) : ImagePickerAction()

    data object Load : ImagePickerAction()
    data object LoadRecentsNextPage : ImagePickerAction()

    data class SelectImage(val image : Uri , val id : Long) : ImagePickerAction()
    data object CancelSelection : ImagePickerAction()

    data class LoadAlbumImages(val albumName : String) : ImagePickerAction()
    data object LoadAlbumImagesNextPage : ImagePickerAction()

    data object ClearAlbumImages : ImagePickerAction()
}