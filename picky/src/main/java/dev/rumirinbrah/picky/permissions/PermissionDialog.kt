package dev.rumirinbrah.picky.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @author zyzz
 * @param permission - Manifest permission name
 * @param isPermanentlyDeclined - Whether user has permanently declined the permission
 * @param onDismiss - When user doesn't want to grant permission
 * @param onOkClick - When user wishes to grant permission
 * @param onGoToSettings - Open settings
 */
@Composable
internal fun PermissionDialog(
    modifier: Modifier = Modifier,
    permission: String ,
    isPermanentlyDeclined: Boolean = false ,
    onDismiss: () -> Unit ,
    onOkClick: () -> Unit ,
    onGoToSettings: () -> Unit ,
) {

    val permissionName = remember { getPermissionName(permission) }

    AlertDialog(
        onDismissRequest = onDismiss ,
        title = {
            Text(
                "Can we have the permission please?",
                color = MaterialTheme.colorScheme.onBackground ,
                textAlign = TextAlign.Center
            )
        } ,
        text = {
            Text(
                text = buildAnnotatedString {
                    if (isPermanentlyDeclined) {
                        append("Uh oh! Looks like we've been turned down. You can grant the ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ){
                            append(permissionName)
                        }
                        append(" permission through settings if you change your mind!")
                    } else{
                        append("Almost there! We just need the ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ){
                            append(permissionName)
                        }
                        append(" permission to make your travels a little smoother.")
                    }
                }
                ,
                color = MaterialTheme.colorScheme.onBackground
            )
        } ,
        confirmButton = {
            Row(
                Modifier.fillMaxWidth() ,
                horizontalArrangement = Arrangement.spacedBy(8.dp) ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //settings
                if(isPermanentlyDeclined){
                    Button(
                        onClick = onGoToSettings,
                        modifier = Modifier.weight(1f) ,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) ,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Go to settings ->" , color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                else{
                    //NOUUUU
                    Button(
                        onClick = {
                            onDismiss()
                        } ,
                        modifier = Modifier.weight(1f) ,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ) ,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("No" , color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                    //YEEEE
                    Button(
                        onClick = {
                            onOkClick()
                        } ,
                        modifier = Modifier.weight(1f) ,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) ,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            "Okay" ,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                }

            }

        } ,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    )

}


/**
 * @author zyzz
 */
private fun getPermissionName(permission: String) : String{
    return when(permission){
        Manifest.permission.POST_NOTIFICATIONS->"Notification"
        Manifest.permission.READ_MEDIA_IMAGES -> "Image Media Access"
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED -> "Image Media Access"
        Manifest.permission.READ_EXTERNAL_STORAGE-> "Image Media Access"
        else->"Unknown"
    }
}

/**
 * @author zyzz
 */
internal fun Activity.openAppSettings(){
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package",packageName,null)
    ).also {
        startActivity(it)
    }
}
internal fun Context.hasPermission( permission: String):Boolean{
    return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}