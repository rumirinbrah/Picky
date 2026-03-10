package dev.rumirinbrah.picky.util

internal interface UIEvents {
    data object Success : UIEvents
    data class Error(val errorMsg :String?) : UIEvents
}