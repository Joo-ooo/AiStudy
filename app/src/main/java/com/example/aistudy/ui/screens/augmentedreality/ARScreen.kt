package com.example.aistudy.ui.screens.augmentedreality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aistudy.R
import com.example.aistudy.components.CustomText
import com.example.aistudy.data.models.ARModel
import com.example.aistudy.ui.theme.BlackShade
import com.example.aistudy.ui.theme.ChineseSilver
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Action
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.getDescription
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView

    /**
     * The ARScreen composable function sets up an AR scene using a provided navigation controller and shared view model.
     * It utilizes several ARCore and Sceneform functionalities, such as plane detection, anchor creation, and 3D model rendering,
     * to allow users to place and interact with 3D models in a real-world environment through their device's camera.
     *
     * Key features include:
     * - Dynamically switching between a list of predefined 3D models (such as a polar bear, capybara, skeleton, and fox),
     *   each with a unique description and scale factor.
     * - Offering users interactive capabilities to place models in the environment and switch between them.
     * - Providing a description panel that can be toggled for additional information about the currently selected model.
     * - Handling user gestures for model placement and selection, with feedback on AR tracking state and instructions for interaction.
     *
     * The AR functionality is encapsulated within a Box composable that manages the AR scene and overlays UI components
     * for model selection and information display. It demonstrates a practical implementation of AR within a mobile application,
     * leveraging Kotlin, Jetpack Compose, and Google's ARCore for an immersive user experience.
     */
@Composable
fun ARScreen(
    navigateToListScreen: (Action) -> Unit,
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
        // The destroy calls are automatically made when their disposable effect leaves
        // the composition or its key changes.
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val materialLoader = rememberMaterialLoader(engine)
        val cameraNode = ARSceneView.createARCameraNode(engine)
        val childNodes = rememberNodes()
        val view = rememberView(engine)
        val collisionSystem = rememberCollisionSystem(view)

        var planeRenderer by remember { mutableStateOf(true) }

        val modelInstances = remember { mutableListOf<ModelInstance>() }
        var trackingFailureReason by remember {
            mutableStateOf<TrackingFailureReason?>(null)
        }
        var frame by remember { mutableStateOf<Frame?>(null) }

        var count by remember { mutableStateOf(0) }
        var isFullScreen by remember { mutableStateOf(true) }
        var newStart by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                collisionSystem = collisionSystem,
                sessionConfiguration = { session, config ->
                    config.depthMode =
                        when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                            true -> Config.DepthMode.AUTOMATIC
                            else -> Config.DepthMode.DISABLED
                        }
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode =
                        Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                cameraNode = cameraNode,
                planeRenderer = planeRenderer,
                onTrackingFailureChanged = {
                    trackingFailureReason = it
                },
                onSessionUpdated = { session, updatedFrame ->
                    frame = updatedFrame
                },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { motionEvent, node ->
                        if (node == null) {
                            val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                            hitResults?.firstOrNull {
                                it.isValid(
                                    depthPoint = false,
                                    point = false
                                )
                            }?.createAnchorOrNull()
                                ?.let { anchor ->
                                    planeRenderer = true
                                    // Remove the previous model if it exists
                                    if (childNodes.isNotEmpty()) {
                                        childNodes.removeAt(childNodes.lastIndex)
                                    }
                                    childNodes += sharedViewModel.createAnchorNode(
                                        engine = engine,
                                        modelLoader = modelLoader,
                                        materialLoader = materialLoader,
                                        modelInstances = modelInstances,
                                        anchor = anchor,
                                        ARModel = kARModelLists[count],
                                        MaxModelInstances = kMaxModelInstances
                                    )
                                }
                        }
                        if (newStart) {
                            newStart = false
                        }
                    }),

                )
            CustomText(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 32.dp, end = 32.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.White,
                text = trackingFailureReason?.let {
                    it.getDescription(LocalContext.current)
                } ?: if (childNodes.isEmpty() && newStart) {
                    "Point your phone down at an empty space, and move it around slowly"
                } else {
                    "Tap anywhere to add model"
                }
            )

            if (!newStart) {
                if (!isFullScreen) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(256.dp)
                            .background(BlackShade)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                IconButton(
                                    onClick = {
                                        isFullScreen = true
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ar_down_arrow),
                                        contentDescription = "Left Button",
                                        tint = ChineseSilver
                                    )
                                }

                                CustomText(
                                    text = kARModelLists[count].modelName,
                                    style = MaterialTheme.typography.h4,
                                    color = Color.White
                                )

                                IconButton(
                                    onClick = {
                                        if (count < kARModelLists.size - 1) {
                                            count++
                                        } else {
                                            count = 0
                                        }
                                        // Remove the previous model if it exists
                                        if (childNodes.isNotEmpty()) {
                                            childNodes.removeAt(childNodes.lastIndex)
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ar_right_arrow),
                                        contentDescription = "Left Button",
                                        tint = ChineseSilver
                                    )
                                }
                            }

                            Divider(color = Color.White)

                            DescrptionText(modelDesc = kARModelLists[count].modelDesc)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                isFullScreen = false
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .background(BlackShade)
                                .size(64.dp)

                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ar_up_arrow),
                                contentDescription = "Up Button",
                                tint = ChineseSilver
                           )
                        }
                    }
                }
            }
        }
}

@Composable
fun DescrptionText(modelDesc: String) {
    val scrollState = rememberScrollState()

    CustomText(
        text = modelDesc,
        fontSize = 18.sp,
        color = Color.White,
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(scrollState)
    )
}

private val kARModelLists = listOf(
    // Need to change Bear Model
    ARModel("Polar Bear","models/bear.glb", 1.25f,
        "The polar bear is a type of bear native to the Arctic Circle. It is a large carnivore, primarily preying on seals. Adapted to cold environments, it has a thick layer of fat and a water-repellent coat for warmth and buoyancy, with white fur for camouflage in the icy Arctic. "),

    ARModel("Capybara","models/capybara.glb", 1f,
        "The capybara is the largest rodent in the world, native to South America. It is a semi-aquatic mammal, known for its webbed feet and love for water. Capybaras have a social nature, often living in groups, and are herbivores, mainly feeding on grasses and aquatic plants. They are known for their calm temperament and are sometimes kept as pets."),

    ARModel("Skeleton","models/skeleton.glb", 1.5f,
        "The skeletal system is the framework of bones and cartilage that supports and protects the body, facilitates movement, and produces blood cells. It consists of 206 bones in adults, including the skull, spine, ribs, and limbs. The system also includes joints, which allow for mobility and flexibility."),

    ARModel("Fox","models/fox.glb",1f,
        "The fox is a small to medium-sized omnivorous mammal, known for its pointed ears, bushy tail, and agile nature. Foxes are found in various habitats worldwide, from forests to deserts. They are skilled hunters and known for their cunning behavior. In Minecraft, a secret fact is that foxes can carry items in their mouths, and if you give them a sweet berry, they might drop the item they're holding in exchange."),

    )

private const val kMaxModelInstances = 10
