package dev.rumirinbrah.picky.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build


/**
 * Checks storage permissions for various Android vers.
 */
internal fun Context.checkStoragePermissions(
    notGrantedBelowAndroid12 : ()->Unit ,
    notGrantedAboveAndroid13 : ()->Unit ,
    //notGrantedFor14 : ()->Unit,
    granted : ()->Unit
){
    when{
        /*
        Build.VERSION.SDK_INT >= 34 ->{
            if(
                !hasPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            ){
                notGrantedFor14()
            }else{
                granted()
            }
        }

         */
        Build.VERSION.SDK_INT >= 33 ->{
            if(
                !hasPermission(Manifest.permission.READ_MEDIA_IMAGES)
            ){
                notGrantedAboveAndroid13()
            }else{
                granted()
            }
        }
        else->{
            if(
                !hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            ){
                notGrantedBelowAndroid12()
            }else{
                granted()
            }
        }
    }
}

private fun Context.hasPermission( permission: String):Boolean{
    return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}