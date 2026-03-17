package dev.rumirinbrah.picky.permissions

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import dev.rumirinbrah.picky.util.UIEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class PermissionManager(
    private val scope : CoroutineScope
) {
    private val _permissionQueue = mutableStateListOf<String>()
    val permissionQueue : List<String> = _permissionQueue

    private val _events = Channel<UIEvents>()
    val events = _events.receiveAsFlow()

    fun onDismiss(){
        log {
            "onDismiss"
        }
        if(_permissionQueue.isNotEmpty())
        {
            _permissionQueue.removeAt(0)
        }
    }

    fun onPermissionResult(
        permission : String,
        granted : Boolean
    ){
        log {
            "onPermissionResult"
        }
        scope.launch {
            if(!granted){
                _permissionQueue.add(permission)
            }else{
                _events.send(UIEvents.Success)
            }
        }
    }

    //TODO(Remove in prod)
    private val loggingEnabled = false

    private fun log(msg: () -> String) {
        if (loggingEnabled) {
            Log.d("PermissionManager : " , msg())
        }
    }

}