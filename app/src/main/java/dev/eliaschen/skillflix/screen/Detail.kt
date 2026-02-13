package dev.eliaschen.skillflix.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.eliaschen.skillflix.LocalNavViewModel
import dev.eliaschen.skillflix.LocalNetworkViewModel
import dev.eliaschen.skillflix.R
import dev.eliaschen.skillflix.component.NetworkImage
import dev.eliaschen.skillflix.red
import dev.eliaschen.skillflix.schema.Episode
import dev.eliaschen.skillflix.schema.FeaturedDetail
import dev.eliaschen.skillflix.schema.People
import dev.eliaschen.skillflix.schema.PeopleType
import dev.eliaschen.skillflix.schema.Season
import dev.eliaschen.skillflix.schema.VideoType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Detail(modifier: Modifier = Modifier) {
    val api = LocalNetworkViewModel.current
    val nav = LocalNavViewModel.current
    val id = nav.navId
    var detail by remember { mutableStateOf<FeaturedDetail?>(null) }

    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            api.getDetail(id)?.let { data ->
                detail = data
            }
        }
    }

    detail?.let { feature ->
        Box {
            HeaderRow(
                feature.id,
                modifier
                    .align(Alignment.TopCenter)
                    .zIndex(10f)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + 20.dp
                )
            ) {
                item {
                    BasicInfo(feature)
                }
                item {
                    GenresRow(feature)
                }
                item {
                    PeopleBox(PeopleType.Directors, feature.directors)
                }
                item {
                    PeopleBox(PeopleType.Writers, feature.writers)
                }
                item {
                    PeopleBox(PeopleType.Stars, feature.stars)
                }
                item {
                    if (feature.type == VideoType.TVSeries) {
                        Spacer(Modifier.height(10.dp))
                        EpisodeArea(feature.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun GenresRow(feature: FeaturedDetail, modifier: Modifier = Modifier) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(feature.genres) {
            Text(
                it, modifier = Modifier
                    .background(
                        red.copy(0.3f), RoundedCornerShape(20f)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun BasicInfo(feature: FeaturedDetail, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        NetworkImage(
            feature.primaryImage.url,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
        ) { bitmap ->
            Image(
                bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.2f to Color.White.copy(0.4f),
                        1f to Color.White
                    )
                )
                .padding(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        feature.primaryTitle,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "${feature.type.label} / ${feature.startYear} ${if (feature.type == VideoType.Movie) "/ ${feature.runtimeSeconds / 60}分鐘" else ""} ",
                        color = Color.Black.copy(0.5f)
                    )
                }
                Text(
                    feature.rating.aggregateRating.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(feature.plot, fontSize = 15.sp, lineHeight = 20.sp)
        }
    }

}

@Composable
private fun EpisodeArea(id: String, modifier: Modifier = Modifier) {
    val api = LocalNetworkViewModel.current
    var selectedSeason by remember { mutableStateOf("") }
    var seasons = remember { mutableStateListOf<Season>() }

    LaunchedEffect(id) {
        val data = api.getSeason(id)
        seasons.clear()
        seasons.addAll(data)
        selectedSeason = seasons.first().season
    }

    if (seasons.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                "劇集列表",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                items(seasons) {
                    InputChip(
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = red.copy(
                                0.3f
                            )
                        ),
                        selected = selectedSeason == it.season,
                        onClick = { selectedSeason = it.season },
                        label = { Text("第${it.season}季") })
                }
            }
            EpisodesRow(id, selectedSeason)
        }
    }
}

@Composable
private fun HeaderRow(titleId: String, modifier: Modifier = Modifier) {
    val nav = LocalNavViewModel.current
    val api = LocalNetworkViewModel.current
    var isCollection by remember { mutableStateOf(false) }

    LaunchedEffect(titleId) {
        val data = api.getCollection()
        data.find { it.id == titleId }.let {
            isCollection = it != null
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { nav.pop() },
            modifier = Modifier
                .padding(5.dp)
                .statusBarsPadding()
                .zIndex(10f),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
        }
        IconButton(
            onClick = {
                if (isCollection) api.removeCollection(titleId) else api.addCollection(titleId)
                isCollection = !isCollection
            },
            modifier = Modifier
                .padding(5.dp)
                .statusBarsPadding()
                .zIndex(10f),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
        ) {
            Icon(
                painter = painterResource(if (isCollection) R.drawable.baseline_bookmark_24 else R.drawable.outline_bookmark_24),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun EpisodesRow(id: String, season: String, modifier: Modifier = Modifier) {
    val api = LocalNetworkViewModel.current
    var episodes = remember { mutableStateListOf<Episode>() }

    LaunchedEffect(id, season) {
        val data = api.getEpisodes(id, season)
        episodes.clear()
        episodes.addAll(data)
    }


    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = Modifier.height(500.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(episodes, { it.id }) {
            Box(modifier = Modifier.widthIn(max = 200.dp)) {
                Column(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier.height(130.dp)
                    ) {
                        NetworkImage(it.primaryImage.url) { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.clip(RoundedCornerShape(20f))
                            )
                        }
                    }
                    Text("${it.episodeNumber}. ${it.title}", fontWeight = FontWeight.Medium)
                    it.plot?.let { plot ->
                        Text(plot, fontSize = 12.sp, lineHeight = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PeopleBox(type: PeopleType, people: List<People>, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            type.label,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(people) { person ->
                Column(
                    modifier = Modifier.width(100.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        person.primaryImage?.let {
                            NetworkImage(it.url) { bitmap ->
                                Image(
                                    bitmap,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    Text(
                        person.displayName,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}