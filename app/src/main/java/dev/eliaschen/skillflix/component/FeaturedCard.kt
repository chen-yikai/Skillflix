package dev.eliaschen.skillflix.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eliaschen.skillflix.LocalNavViewModel
import dev.eliaschen.skillflix.red
import dev.eliaschen.skillflix.schema.Featured
import dev.eliaschen.skillflix.viewmodel.Screen

@Composable
fun FeaturedCard(feature: Featured, modifier: Modifier = Modifier) {
    val nav = LocalNavViewModel.current

    Box(modifier = Modifier.clickable {
        nav.navigate(Screen.Detail, id = feature.id)
    }) {
        Column(
            modifier = Modifier.widthIn(max = 150.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp, 220.dp)
                    .border(0.5.dp, red, RoundedCornerShape(20f))
            ) {
                NetworkImage(feature.primaryImage.url) {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20f)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Column(Modifier.padding(5.dp)) {
                Text(
                    feature.primaryTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(feature.startYear.toString(), modifier = Modifier.alpha(0.5f))
                    Text(feature.rating.aggregateRating.toString(), modifier = Modifier)
                }
            }
        }
    }
}