package dev.rumirinbrah.picky.util

internal interface UIEvents {
    data object Success : UIEvents
    data class Error(val errorMsg :String?) : UIEvents
}

/**
 * One-time UI events for media manager
 * @author zyzz
*/
internal interface MediaManagerEvents{
    data object OnImageSelect : MediaManagerEvents
    data object OnImagesSelect : MediaManagerEvents
}
