package dev.rumirinbrah.picky.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * ### Picky image picker state
 *
 * @property launch Launch the image picker
 * @property dismiss Close the image picker
 *
 * @author zyzz
*/
class PickyState{
    var pickerVisible by mutableStateOf(false)
        private set

    fun launch(){
        pickerVisible = true
    }
    fun dismiss(){
        pickerVisible = false
    }
}
