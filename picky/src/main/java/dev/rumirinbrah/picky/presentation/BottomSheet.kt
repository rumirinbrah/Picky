package dev.rumirinbrah.picky.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
internal fun rememberPickySheetState(
    initialState: PickySheetState  = PickySheetState.CLOSED
): PickyBottomSheetState {

    val density = LocalDensity.current
    val localConfig = LocalConfiguration.current
    val scope = rememberCoroutineScope()

    val navBarHeight = WindowInsets.systemGestures.getBottom(density)

    val screenHeight = remember(density) {
        with(density) {
            localConfig.screenHeightDp.dp.toPx()
        }
    }

    //exp
    val anchors = remember {
        mapOf(
            PickySheetState.EXPANDED to 0f ,
            PickySheetState.HALF_EXPANDED to 0.3f * screenHeight ,
            PickySheetState.CLOSED to screenHeight + navBarHeight
        )
    }

    val decay = rememberSplineBasedDecay<Float>()
    //exp
    val translationY = remember {
        Animatable(
            initialValue = anchors.getValue(initialState)
        )
    }
    translationY.updateBounds(0f , screenHeight + navBarHeight)

    //exp
    val draggableState = rememberDraggableState { dragAmount ->
        scope.launch {
            translationY.snapTo(translationY.value + dragAmount)
        }
    }

    return remember {
        PickyBottomSheetState(
            anchors = anchors ,
            animatable = translationY ,
            decayAnimationSpec = decay ,
            draggableState = draggableState ,
            sheetState = initialState
        )
    }

}

/**
 * @param color Handle color
 * @param verticalPadding Padding around the handle
 */
@Composable
fun BottomSheetHandle(
    modifier: Modifier = Modifier ,
    sheetState: PickySheetState = PickySheetState.HALF_EXPANDED ,
    color: Color = MaterialTheme.colorScheme.onBackground.copy(0.7f) ,
    verticalPadding: Dp = 10.dp ,
    width: Dp = 30.dp ,
    height: Dp = 5.dp ,
) {
    Box(
        modifier
            .padding(vertical = verticalPadding)
            .clip(MaterialTheme.shapes.large)
            .width(width)
            .height(height)
            .background(color)
    )
}

@Composable
internal fun PickyBottomSheet(
    modifier: Modifier = Modifier ,
    state: PickyBottomSheetState ,
    onSheetClosed: () -> Unit = {} ,
    contentPadding : Dp = 16.dp,
    verticalSpacing: Dp = 8.dp ,
    sheetColor: Color = MaterialTheme.colorScheme.surfaceContainer ,
    sheetShape: Shape = RoundedCornerShape(0.dp),
    dismissTopContainer : Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val scope = rememberCoroutineScope()


    //-------- PARENT CONTAINER --------
    Box(
        Modifier
            .fillMaxSize()
            .background(
                color = sheetColor.copy(0.2f),
//                sheetShape
            )
    ) {
        //-------- OVERLAY AT THE TOP TO DISMISS SHEET ON CLICK --------
        if(dismissTopContainer){
            Box(
                Modifier.fillMaxSize()
                    .clickable(
                        onClick = {
                            onSheetClosed()
                            scope.launch {
                                state.dismiss()
                            }
                        },
                        onClickLabel = "Dismiss bottom sheet",
                        interactionSource = null,
                        indication = null
                    )
                    .align(Alignment.TopCenter)
            ){

            }
        }


        //-------- DRAGGABLE CONTAINER --------
        Box(
            Modifier
                .graphicsLayer {
                    //this.translationY = translationY.value
                    this.translationY = state.translationY.value
                }
        ) {
            Column(
                modifier
                    .draggable(
                        state = state.draggableState ,
                        orientation = Orientation.Vertical ,
                        onDragStopped = { velocity ->
                            val decayY = state.calculateDecayTarget(
                                state.translationY.value ,
                                velocity
                            )
                            val transY = state.translationY.value
                            val halfAnchor = state.getAnchorValue(PickySheetState.HALF_EXPANDED)
                            val fullAnchor = state.getAnchorValue(PickySheetState.EXPANDED)
                            val closedAnchor = state.getAnchorValue(PickySheetState.CLOSED)

                            scope.launch {
                                val targetState = when {
                                    //fling up
                                    decayY < (fullAnchor + halfAnchor) / 2 -> PickySheetState.EXPANDED
                                    /*
                                    fling down
                                    transY < (fullAnchor + halfAnchor)/2 -> SheetState.EXPANDED
                                    decayY > (halfAnchor + closedAnchor)/2 -> SheetState.CLOSED

                                     */
                                    //pull down
                                    transY > (halfAnchor + closedAnchor) / 2 -> PickySheetState.CLOSED
                                    else -> PickySheetState.HALF_EXPANDED
                                }
                                state.animateTo(targetState)
                                //HIDE SHEET
                                if (targetState == PickySheetState.CLOSED) {
                                    onSheetClosed()
                                }

                            }
                        }
                    )
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(verticalSpacing)
            ) {
                BottomSheetHandle(
                    Modifier.align(Alignment.CenterHorizontally) ,
                    verticalPadding = 10.dp ,
                    sheetState = state.sheetState
                )
                Spacer(Modifier.height(5.dp))
                content()
            }
        }
    }


}



internal class PickyBottomSheetState(
    private val anchors: Map<PickySheetState , Float> ,
    private val animatable: Animatable<Float , AnimationVector1D> ,
    val decayAnimationSpec: DecayAnimationSpec<Float> ,
    draggableState: DraggableState ,
    visible: Boolean = false ,
    sheetState: PickySheetState = PickySheetState.HALF_EXPANDED
)
{
    val translationY: State<Float> = animatable.asState()
    val draggableState = draggableState
    var visible by mutableStateOf(visible)
        private set
    var sheetState by mutableStateOf(sheetState)
        private set

    /**
     * To animate sheet between different states such as EXPANDED,COLLAPSED,etc.
     */
    suspend fun animateTo(
        sheetState: PickySheetState ,
        animationSpec: AnimationSpec<Float> = spring()
    ) {
        try {
            animatable.animateTo(
                anchors.getValue(sheetState) ,
                animationSpec = animationSpec
            )
            visible = when (sheetState) {
                PickySheetState.CLOSED -> {
                    false
                }
                else -> {
                    true
                }
            }
            this.sheetState = sheetState
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            e.printStackTrace()
        }

    }

    /**
     * Snap without animation
     */
    suspend fun snapTo(
        sheetState: PickySheetState ,
    ) {
        try {
            animatable.snapTo(anchors.getValue(sheetState))
            visible = when (sheetState) {
                PickySheetState.CLOSED -> {
                    false
                }

                else -> {
                    true
                }
            }
            this.sheetState = sheetState
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            e.printStackTrace()
        }
    }

    /**
     * Hide sheet
     */
    fun show() {
        visible = true
    }

    /**
     * Hide sheet
     */
    suspend fun dismiss() {
        try {
            animatable.snapTo(anchors.getValue(PickySheetState.CLOSED))
            visible = false
            sheetState = PickySheetState.CLOSED
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @return Decay value
     */
    fun calculateDecayTarget(
        initialValue: Float ,
        initialVelocity: Float
    ): Float {
        return decayAnimationSpec.calculateTargetValue(initialValue , initialVelocity)
    }

    /**
     * @param sheetState - State for the sheet for which assigned height is needed
     * @return Height for the sheet state in Float
     * @exception Exception If sheet state invalid, returns 0F
     */
    fun getAnchorValue(sheetState: PickySheetState): Float {
        return try {
            anchors.getValue(sheetState)
        } catch (e: Exception) {
            e.printStackTrace()
            0f
        }
    }


}

/**
 * Represents states for picky bottom sheet
 */
enum class PickySheetState {
    HALF_EXPANDED ,
    EXPANDED ,
    CLOSED
}


@Preview(showBackground = true)
@Composable
private fun BottomSheetPrev() {
//    PickyBottomSheet()
    Box(
        Modifier.fillMaxSize()
            .background(Color.Gray)
    ){
        Text("HELLO THERE")
    }
}


