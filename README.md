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
**⚠️Make sure to put the component inside a Box as it acts as an overlap over your UI**

## 🎨Picky Configurations
**Picky is designed to be flexible and adapt to your app’s design. You can customize appearance, layout, and behavior via parameters.**
### 1. Grid 🗃️
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
### 2. Shape of the sheet 📐
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
### 3. Tab Colors 🖌️

Tab colors can be used to customize the **Recents | Album** tab of Picky.

Class attributes
```kotlin
/**
 * This allows customization of the container, tab content, selection state,
 * and indicator colors used by the tab component.
 *
 * By default, the colors are derived from the current [MaterialTheme]
 * so the picker automatically adapts to the host application's theme.
 *
 * @param containerColor Background color of the tab row container.
 * @param contentColor Default color applied to tab content.
 * @param selectedTabColor Color used for the label of currently selected tab.
 * @param selectedTabContainerColor Color used for the container of selected tab
 * @param unselectedTabColor Color used for the label of tab that is not selected.
 * @param tabIndicatorColor Color of the indicator shown beneath the selected tab.
 *
 * Example:
 * ```
 * PickyDefaults.tabColors(
 *     containerColor = Color.Black,
 *     selectedTabColor = Color.White
 * )
 * ```
 */    
class PickyTabColors(
    val containerColor: Color ,
    val contentColor: Color ,
    val selectedTabColor: Color ,
    val selectedTabContainerColor: Color,
    val unselectedTabColor: Color ,
    val tabIndicatorColor: Color
)
```
Use it through sheet
```kotlin
    PickyImagePickerSheet(
        pickyState = picky,
        onResult = {uri->
            println("URI is $uri")
        },
        option = PickyOption.PickSingle,
        tabColors = PickyDefaults.tabColors(
            containerColor = Color.White
        )
    )
```
    
### 4. Selection Colors 🖌️

Used to configure the selected items in multi-select mode.

Class  attributes
```kotlin
/**
 * This includes the checkmark icon, its background, and the border indicator
 * drawn around selected images. By default, the colors are derived from the
 * current [MaterialTheme] so the picker integrates naturally with the host
 * application's theme.
 *
 * @param tickIconColor Color of the selection checkmark icon displayed on
 * selected images.
 * @param tickIconBackgroundColor Background color behind the checkmark icon.
 * (This is drawn as a circular container)
 * @param borderIndicatorColor Color of the border drawn around selected images.
 * Example:
 * ```
 * PickyDefaults.selectionColors(
 *     tickIconColor = Color.White,
 *     tickIconBackgroundColor = Color.Black,
 *     borderIndicatorColor = Color.White
 * )
 * ```
 */
class PickySelectionColors(
    val tickIconColor : Color,
    val tickIconBackgroundColor : Color,
    val borderIndicatorColor : Color
)
```
Use it through sheet
```kotlin
    PickyImagePickerSheet(
        pickyState = picky,
        onResult = {uri->
            println("URI is $uri")
        },
        option = PickyOption.PickSingle,
        selectionColors = PickyDefaults.selectionColors(
            tickIconColor = Color.Black
        )
    )
```

### 5. Sheet background color 🌄

Customize through the background parameter of PickyImagePickerSheet
```kotlin
    PickyImagePickerSheet(
        pickyState = picky,
        onResult = {uri->
            println("URI is $uri")
        },
        option = PickyOption.PickSingle,
        background = Color.Gray
    )
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

## 🤝 Contributing

Contributions are welcome! 🎉  
If you'd like to improve **Picky**, feel free to contribute in any of the following ways:

- 🐛 Report bugs & errors
- 💡 Suggest new features or improvements
- 🔧 Submit pull requests


Made with 💖

Happy coding fellas~

