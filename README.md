# Picky 📸✨

A modern, Jetpack Compose image picker that gives you **persistent URI access** — no more crashes when accessing images later.

## Usage/Set-up
### 1. Setup Gradle
### Gradle (kotlin)

In settings.gradle
```kotlin
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://www.jitpack.io") }
		}
	}
```
In build.gradle (app-level)
```kotlin
	dependencies {
            //ex version 1.0.0
	        implementation("com.github.rumirinbrah:picky:$version")
	}
```

### Gradle (groovy)

In settings.gradle
```kotlin
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            mavenCentral()
            //add this
            maven { url 'https://www.jitpack.io' }
        }
    }
```
In build.gradle (app-level)
```kotlin
    dependencies {
        //ex version 1.0.0
        implementation 'com.github.rumirinbrah:picky:$version'
    }
```
### 2. Use in code
```kotlin
    val picky = rememberPickyImagePicker()
    
    Box(
        Modifier.fillMaxSize()
    ){
        Button(
            onClick = {
                picky.launch()
            }
        ) {
            Text("Launch Picky")
        }
        
        PickyImagePickerSheet(
            pickyState = picky,
            onResult = {uri->
                println("URI is $uri")
            },
            option = PickyOption.PickSingle
        )
    }
```
## 🎨Picky Configurations
**Picky is designed to be flexible and adapt to your app’s design. You can customize appearance, layout, and behavior via parameters.**
### 1. Grid
You can customize the number of grid cells you want & spacing between items
```kotlin
/**
 * Configuration for controlling the layout of the image grid inside the Picky picker.
 *
 * @param horizontalSpacing Horizontal space between grid items.
 * @param verticalSpacing Vertical space between grid items.
 * @param gridCells Number of columns in the grid.
 * @sample sampleGridConfig
 */
data class PickyGridConfig(
    val horizontalSpacing : Dp = 2.dp,
    val verticalSpacing : Dp = 2.dp,
    val gridCells : Int = 3
)
```
### 2. Shape of the sheet
Define the type of shape you want.

Example
```kotlin
    PickyImagePickerSheet(
        pickyState = picky,
        sheetShape = RoundedCornerShape(25.dp) ,
        onResult = {uri->
            println("URI is $uri")
        },
        option = PickyOption.PickSingle
    )
```
Default
```kotlin
val sheetShape: Shape = RoundedCornerShape(topEnd = 40.dp , topStart = 40.dp)
```


## ✨ Features

- 🔐 **Persistent URI access**  
  Safely access selected images anytime — no more crashes due to lost permissions.💀

- 🛡 **Automatic permission handling (Upto Android 16)**  
  Handles media and storage permissions internally, so you don’t have to.

- 🖼 **Single & multiple image selection**  
  Flexible API supporting both single and multi-select use cases.

- 📁 **Albums + recents browsing**  
  Lets users pick images from albums or recent media, just like android's image picker.

- ⚡ **Compose-first API**  
  Built entirely with Jetpack Compose & Kotlin.

- 🎨 **Highly customizable UI**  
  Customize colors, grid layout, selection indicators, and more to match your app’s design.

- 🚀 **Simple and intuitive API**  
  Minimal setup required — open the picker and get results with just a few lines of code.
