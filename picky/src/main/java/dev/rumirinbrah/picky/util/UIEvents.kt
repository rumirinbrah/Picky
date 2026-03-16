package dev.rumirinbrah.picky.util

import android.net.Uri

internal interface UIEvents {
    data object Success : UIEvents
    data class Error(val errorMsg :String?) : UIEvents
}

/**
 * One-time UI events for media manager
 * @author zyzz
*/
internal interface MediaManagerEvents{
    /**
     * one time event for single img selection
     * @author zyzz
    */
    data class OnImageSelect(val uri : Uri) : MediaManagerEvents
    /**
     * event for multi selection
     * @author zyzz
    */
    data class OnImagesSelect(val uri : List<Uri>) : MediaManagerEvents

    data class Error(val errorMsg : String) : MediaManagerEvents
}
