package dev.rumirinbrah.picky.api

import android.net.Uri

/**
 * Represents the selection configuration for the Picky image picker.
 *
 * This type determines both the **selection behavior** of the picker and the
 * **result type** returned through the picker callback.
 *
 * The generic type [T] corresponds to the type delivered in `onResult`
 * when using the picker composable.
 *
 * Example usage:
 *
 * ```
 * PickyImagePickerSheet(
 *     option = PickyOption.PickSingle
 * ) { uri ->
 *     // uri is a Uri
 * }
 * ```
 *
 * ```
 * PickyImagePickerSheet(
 *     option = PickyOption.PickMultiple(maxItems = 5)
 * ) { uris ->
 *     // uris is a List<Uri>
 * }
 * ```
 *
 * Implementations:
 * - [PickSingle] returns a single [Uri].
 * - [PickMultiple] returns a List of [Uri].
 *
 * This interface is sealed to ensure only the supported picker configurations
 * can be used.
 */
sealed interface PickyOption<T> {
    /**
     * Configures the picker to allow selecting **a single image only**.
     *
     * The result callback will receive a single [Uri].
     */
    data object PickSingle : PickyOption<Uri>

    /**
     * Configures the picker to allow selecting **multiple images**.
     *
     * @param maxItems The maximum number of images that can be selected.
     * Defaults to `20`.
     *
     * The result callback will receive a [List] of selected [Uri] objects.
     */
    data class PickMultiple(val maxItems: Int = 20) : PickyOption<List<Uri>>
}
