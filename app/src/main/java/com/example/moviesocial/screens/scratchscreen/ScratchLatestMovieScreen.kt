package com.example.moviesocial.screens.scratchscreen


import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.moviesocial.R
import com.example.moviesocial.model.Line
import com.google.gson.Gson
import org.koin.androidx.compose.koinViewModel



@Composable
fun ScratchLatestMovieScreen(navController : NavHostController){

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            var isCardScratched = remember { mutableStateOf(false) }


            Spacer(modifier = Modifier.height(40.dp))

            ScratchMovieComponent(
                navController,
                modifier = Modifier
                    .height(400.dp)
                    .fillMaxWidth(0.9f),
                onScratchComplete = {
                    isCardScratched.value = true
                },
                isScratched = isCardScratched.value,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Go ahead, scratch to find the perfect movie we’ve chosen for you!",
                fontSize = 22.sp,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center
            )

        }
    }
}

@Composable
fun ScratchMovieComponent(
    navController: NavHostController,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    scratchingThresholdPercentage : Float = 0.8f,
    scratchLineWidth : Dp = 32.dp,
    scratchLineCap : StrokeCap = StrokeCap.Round,
    isScratched: Boolean = false,
    onScratchComplete: () -> Unit = {},
    shape: Shape = RoundedCornerShape(12.dp)
) {

    val viewModel = koinViewModel<ScratchLatestMovieViewModel>()

    LaunchedEffect(Unit) {
        viewModel.getRandomMovie()
    }

    val latestMovie = viewModel.movie.value


    val baseUrl = "https://image.tmdb.org/t/p/w500"

    val fullImageUrl = baseUrl + (latestMovie?.poster_path ?: "")
    val painterImage = rememberImagePainter(
        data = fullImageUrl,
        builder = {
            error(R.drawable.movie_social)
            crossfade(1000)
        }
    )

    val baseImage =  painterImage
    val overlayImage =  ImageBitmap.imageResource(R.drawable.scratch)



    val scratchLines = remember {
        mutableStateListOf<Line>()
    }
    val totalScratchedArea = remember {
        mutableFloatStateOf(0f)
    }

    Box(
        modifier = modifier
            .clip(shape)
    ){
        Image(
            painter = baseImage,
            contentDescription = "Base image",
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .clip(shape),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = androidx.compose.ui.Modifier
                .graphicsLayer {
                    compositingStrategy =
                        CompositingStrategy.Offscreen
                }
        ) {
            Canvas(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .pointerInput(true)
                    {
                        detectTapGestures(
                            onTap = { position ->

                                val line = Line(
                                    start = position,
                                    end = position,
                                    strokeWidth = scratchLineWidth.toPx()
                                )
                                totalScratchedArea.floatValue += line.calculateArea()
                                scratchLines.add(line)
                                val movieJson = Uri.encode(Gson().toJson(latestMovie))
                                navController.navigate("movie_detail_screen/$movieJson")
                            }
                        )
                    }
                    .pointerInput(true) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()

                            val line = Line(
                                start = change.position - dragAmount,
                                end = change.position,
                                strokeWidth = scratchLineWidth.toPx()
                            )
                            //accumulate the total scratched area, including overlapping lines
                            totalScratchedArea.floatValue += line.calculateArea()

                            scratchLines.add(line)
                        }
                    }
            ) {
                val imageSize = IntSize(width = size.width.toInt(), height = size.height.toInt())
                val maxCanvasArea = this.size.width * this.size.height

                if(!isScratched && totalScratchedArea.floatValue/maxCanvasArea < scratchingThresholdPercentage) {
                    drawImage(image = overlayImage, dstSize = imageSize)
                    scratchLines.forEach { line ->
                        drawLine(
                            color = Color.Transparent,
                            start = line.start,
                            end = line.end,
                            strokeWidth = line.strokeWidth,
                            cap = scratchLineCap,
                            blendMode = BlendMode.Clear
                        )
                    }
                }else{
                    if(totalScratchedArea.floatValue>0) {
                        if(!isScratched){
                            onScratchComplete()
                        }
                        if(isScratched){
                            scratchLines.clear()
                            totalScratchedArea.floatValue = 0f

                        }
                    }
                }

            }
        }
    }
}


