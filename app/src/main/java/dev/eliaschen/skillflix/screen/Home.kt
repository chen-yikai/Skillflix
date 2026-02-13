package dev.eliaschen.skillflix.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eliaschen.skillflix.LocalNavViewModel
import dev.eliaschen.skillflix.LocalNetworkViewModel
import dev.eliaschen.skillflix.R
import dev.eliaschen.skillflix.component.FeaturedCard
import dev.eliaschen.skillflix.component.NetworkImage
import dev.eliaschen.skillflix.red
import dev.eliaschen.skillflix.schema.Featured
import dev.eliaschen.skillflix.schema.VideoType
import dev.eliaschen.skillflix.viewmodel.Screen

@Composable
fun Home(modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Box {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 50.dp,
                    bottom = innerPadding.calculateBottomPadding() + 20.dp
                ),
                modifier = modifier.background(Color.White)
            ) {
                item {
                    PosterPreviewBox(VideoType.Movie)
                }
                item {
                    PosterPreviewBox(VideoType.TVSeries)
                }
                item {
                    PosterPreviewBox(isCollection = true)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            renderEffect = BlurEffect(
                                radiusX = 100f,
                                radiusY = 100f,
                                edgeTreatment = TileMode.Decal
                            )
                        }
                        .background(Color.White.copy(0.9f))
                        .statusBarsPadding()
                )
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(30.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun PosterPreviewBox(
    type: VideoType = VideoType.Movie,
    isCollection: Boolean = false,
    modifier: Modifier = Modifier
) {
    val api = LocalNetworkViewModel.current
    val nav = LocalNavViewModel.current
    val featuredList = remember { mutableStateListOf<Featured>() }

    LaunchedEffect(type, isCollection) {
        if (!isCollection) {
            val data = api.getFeatured(type = type)
            featuredList.clear()
            featuredList.addAll(data.take(3))
        } else {
            val data = api.getCollection()
            featuredList.clear()
            featuredList.addAll(data)
        }
    }

    if (featuredList.isNotEmpty()) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .height(50.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(red, RoundedCornerShape(100f))
                            .size(4.dp, 25.dp)
                    )
                    Text(
                        if (isCollection) "我的收藏" else "精選${type.label}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (!isCollection) {
                    IconButton(onClick = { nav.navigate(Screen.ActualList, videoType = type) }) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = red)
                    }
                }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                items(featuredList, { it.id }) { featured ->
                    FeaturedCard(featured)
                }
            }
        }
    }
}