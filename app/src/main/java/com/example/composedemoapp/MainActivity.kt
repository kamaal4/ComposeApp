package com.example.composedemoapp

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.twotone.East
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.example.composedemoapp.network.Movie
import com.example.composedemoapp.ui.theme.ComposeDemoAppTheme
import com.skydoves.landscapist.glide.GlideImage

class MainActivity : ComponentActivity() {

    val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDemoAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (!Splasher()) {
                        MyApp(movieList = mainViewModel.movieListResponse)
                        mainViewModel.getMovieList()
                    }
                }
            }
        }
    }
}


@Composable
private fun MyApp(movieList: List<Movie>) {

    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
    if (shouldShowOnboarding) {
        OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
    } else {
        Movies(movieList)
    }
}

@Composable
private fun Movies(movies: List<Movie>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Movies")
                },
                actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier
            .padding(innerPadding)
            .padding(vertical = 4.dp)) {
            items(items = movies) { movie ->
                MovieItem(movie = movie)
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        CardContent(movie = movie)
    }
}

@Composable
private fun CardContent(movie: Movie){
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Surface(
            modifier = Modifier.size(70.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {
            //Image goes here
            GlideImage(
                imageModel = movie.imageUrl,
                contentScale = ContentScale.Crop,
                placeHolder = ImageBitmap.imageResource(com.airbnb.lottie.R.drawable.abc_ic_star_half_black_16dp),
                contentDescription = movie.desc,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f)
            )

        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {


            Text(
                text = movie.name,
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (expanded) {
                Text(text = movie.desc)
            }
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }

            )
        }

        IconButton(onClick = { Toast.makeText(context, "Tested", Toast.LENGTH_SHORT).show() }) {
            Icon(
                imageVector = Icons.TwoTone.East,
                contentDescription = "Testing...."
            )

        }
    }

}
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    ComposeDemoAppTheme {
        val movie = Movie(
            "Coco",
            "https://howtodoandroid.com/images/coco.jpg",
            "Coco is a 2017 American 3D computer-animated musical fantasy adventure film produced by Pixar",
            "Latest"
        )
        MovieItem(movie = movie)
    }
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
    // TODO: This state should be hoisted

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ) {
                Text("Continue")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    ComposeDemoAppTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}


@Composable
fun Splasher():Boolean {
    val compositionResult : LottieCompositionResult = rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.splash_animation)
    )

    val progress by animateLottieCompositionAsState(
        composition = compositionResult.value,
        isPlaying = true,
        iterations = 1,
        speed = 1.0f,
    )
    LottieAnimation(
        composition = compositionResult.value,
        progress = progress
    )
    return progress != 1.0f

}

