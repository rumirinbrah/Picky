package dev.rumirinbrah.picky.presentation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.rumirinbrah.picky.media.GalleryAlbum
import dev.rumirinbrah.picky.media.GalleryImage
import dev.rumirinbrah.picky.media.ImagePickerAction
import dev.rumirinbrah.picky.media.ImagePickerState
import dev.rumirinbrah.picky.media.containsId

/**
 * Composable rep a list of albums and over pics. Has dedicated navigation internally
 * @param onDismiss User navigates up
 */
@Composable
internal fun AlbumsRoot(
    state: ImagePickerState ,
    onAction: (ImagePickerAction) -> Unit ,
    onDismiss: () -> Unit ,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    BackHandler {
        onDismiss()
    }

    NavHost(
        modifier = modifier,
        navController = navController ,
        startDestination = "all_albums_screen" ,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = {
                    it
                }
            )
        } ,
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = {
                    -it
                }
            )
        } ,
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = {
                    -it
                }
            )
        } ,
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = {
                    it
                }
            )
        }
    ) {
        composable(
            route = "all_albums_screen"
        ) {
            LaunchedEffect(Unit) {
                //delay(500)
                onAction(ImagePickerAction.TriggerTabRow(true))
            }
            AllAlbumsPage(
                state.albums ,
                onNavToAlbum = { albumName ->
                    navController.navigate("all_detail_screen/$albumName")
                    onAction(ImagePickerAction.TriggerTabRow(false))
                } ,
            )

        }
        composable(
            route = "all_detail_screen/{name}",
            arguments = listOf(
                navArgument(
                    name = "name"
                ){
                    NavType.StringType
                }
            )
        ) {backstack->
            val albumName = backstack.arguments?.getString("name") ?: run {
                throw Exception("An unknown error occurred")
            }
            LaunchedEffect(Unit) {
                onAction(ImagePickerAction.LoadAlbumImages(albumName))
                onAction(ImagePickerAction.TriggerTabRow(false))
            }

            AlbumImagesPage(
                images = state.albumImages ,
                selectedImages = state.selectedImages,
                albumName = state.selectedAlbum ,
                loading = state.loadingAlbumImages,
                onImageSelect = { uri ->
                    onAction(ImagePickerAction.SelectImage(uri,0))
                } ,
                multiSelect = state.multiSelect,
                onAction = {
                    onAction(it)
                },
                navigateUp = {
                    onAction(ImagePickerAction.ClearAlbumImages)
                    navController.navigateUp()
                }
            )

        }
    }

}

@Composable
internal fun AllAlbumsPage(
    albums: List<GalleryAlbum> ,
    onNavToAlbum: (albumName: String) -> Unit ,
    modifier: Modifier = Modifier ,
    gridCells: Int = 2 ,
) {


    Column(
        modifier.fillMaxSize()
    ) {
        VerticalSpace()

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize() ,
            columns = GridCells.Fixed(gridCells) ,
            verticalArrangement = Arrangement.spacedBy(2.dp) ,
            horizontalArrangement = Arrangement.spacedBy(2.dp) ,
        ) {
            items(
                albums
            ) { album ->
                AlbumItem(
                    album ,
                    onClick = onNavToAlbum
                )
            }
        }
    }
}

/**
 * Displays all the images in a particular album
 */
@Composable
private fun AlbumImagesPage(
    modifier: Modifier = Modifier ,
    images: List<GalleryImage> ,
    selectedImages : List<GalleryImage>,
    onAction: (ImagePickerAction) -> Unit,
    albumName: String? = null ,
    loading : Boolean = false ,
    multiSelect : Boolean = false,
    onImageSelect: (imageUri: Uri) -> Unit ,
    navigateUp: () -> Unit ,
    gridCells: Int = 3 ,
) {
    val context = LocalContext.current
    val listState = rememberLazyGridState()

    BackHandler {
        navigateUp()
    }
//    LaunchedEffect(listState) {
//        snapshotFlow {
//            listState.layoutInfo
//        }.debounce(400)
//            .collect{ layoutInfo->
//                val total = layoutInfo.totalItemsCount
//                val lastItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
//
//                if(total>=5 && lastItem >= total-2){
//                    //TODO load more
//                }
//            }
//    }

    Column(
        modifier.fillMaxSize()
    ) {
        VerticalSpace(10.dp)
        ActionButtonHeader(
            actionIcon = Icons.AutoMirrored.Filled.ArrowBack ,
            onAction = {
                navigateUp()
            } ,
            actionDescription = "Go back" ,
            title = albumName ?: "Unknown"
        )
        VerticalSpace(10.dp)
        when{
            loading->{
                CircularProgressIndicator(
                    modifier = Modifier.size(25.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize() ,
            columns = GridCells.Fixed(gridCells) ,
            verticalArrangement = Arrangement.spacedBy(2.dp) ,
            horizontalArrangement = Arrangement.spacedBy(2.dp) ,
            state = listState
        ) {
            items(
                images ,
                key = {
                    it.id
                }
            ) { image ->
//                Box(
//                    Modifier
//                        .animateItem()
//                        .weight(1f)
//                ) {
//                    AsyncImage(
//                        model = ImageRequest.Builder(context)
//                            .data(image.image)
//                            .crossfade(true)
//                            .build() ,
//                        contentDescription = "image" ,
//                        modifier = Modifier
//                            .aspectRatio(1f)
//                            .clickable {
//                                onImageSelect(image.image)
//                            },
//                        contentScale = ContentScale.Crop
//                    )
//                }
                ImageItem(
                    Modifier
                        .animateItem()
                        .weight(1f),
                    image = image,
                    onClick = {
                        onAction(ImagePickerAction.SelectImage(it.image , it.id))
                    },
                    selected = multiSelect && selectedImages.containsId(image.id)
                )

            }
            item {
                VerticalSpace()
            }
        }

    }
}

/**
 * Single album item in the grid
 */
@Composable
private fun AlbumItem(
    album: GalleryAlbum ,
    onClick: (albumName: String) -> Unit ,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier
            .padding(8.dp) ,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(album.coverImage.image)
                .crossfade(true)
                .build() ,
            contentDescription = album.albumName ,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .aspectRatio(1f)
                .clickable {
                    onClick(album.albumName)
                },
            contentScale = ContentScale.Crop
        )
        Text(
            album.albumName ,
            style = TextStyle(
                fontSize = 15.sp ,
                fontWeight = FontWeight.Bold
            ) ,
            maxLines = 1 ,
            overflow = TextOverflow.Ellipsis
        )
    }
}