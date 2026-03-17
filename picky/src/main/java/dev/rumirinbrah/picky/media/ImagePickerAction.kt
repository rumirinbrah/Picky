package dev.rumirinbrah.picky.media

import android.net.Uri
import dev.rumirinbrah.picky.api.PickyOption

internal sealed class ImagePickerAction {
    data class TriggerTabRow(val visible : Boolean) : ImagePickerAction()
    data class SetPickyOption<T>(val option: PickyOption <T>) : ImagePickerAction()

    data object Load : ImagePickerAction()
    data object LoadRecentsNextPage : ImagePickerAction()

    data class PreviewImage(val uri : Uri) : ImagePickerAction()
    data object ClearPreviewImage : ImagePickerAction()
    data class SelectImage(val image : Uri , val id : Long) : ImagePickerAction()
    data object CancelSelection : ImagePickerAction()

    data class LoadAlbumImages(val albumName : String) : ImagePickerAction()
    data object LoadAlbumImagesNextPage : ImagePickerAction()

    /**
     * Clear all selected images in mutli select
     * @author zyzz
    */
    data object ClearSelection : ImagePickerAction()
    /**
     * Confirm multi select
     * @author zyzz
    */
    data object ConfirmSelection : ImagePickerAction()
    data object ClearAlbumImages : ImagePickerAction()
}