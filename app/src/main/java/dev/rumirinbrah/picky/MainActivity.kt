package dev.rumirinbrah.picky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.rumirinbrah.picky.api.PickyDefaults
import dev.rumirinbrah.picky.api.PickyImagePickerSheet
import dev.rumirinbrah.picky.api.PickyOption
import dev.rumirinbrah.picky.api.rememberPickyImagePicker
import dev.rumirinbrah.picky.presentation.PickySheetState
import dev.rumirinbrah.picky.ui.theme.PickyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PickyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TestPicky(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TestPicky(
    modifier: Modifier = Modifier
) {
    val pickyState = rememberPickyImagePicker()

    Box(
        modifier.fillMaxSize()
    ){
        Column(
            Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    println("launch")
                    pickyState.launch()
                },
                enabled = true,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Launch Picky")
            }
        }
        if(pickyState.pickerVisible){
            PickyImagePickerSheet(
                Modifier.fillMaxSize(),
                pickyState = pickyState,
                onResult = {
                    println("URI is $it")
                },
                option = PickyOption.PickMultiple()
            )
        }
    }
}

@Composable
fun Greeting(name: String , modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!" ,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PickyTheme {
        val state = rememberPickyImagePicker()
        LaunchedEffect(Unit) {
            state.launch()
        }

//        PickyImagePickerSheet(
//            pickyState = state,
//            initialSheetState = PickySheetState.HALF_EXPANDED,
//            onResult = {}
//        )
    }
}