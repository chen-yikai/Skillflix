package dev.eliaschen.skillflix.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eliaschen.skillflix.LocalNavViewModel
import dev.eliaschen.skillflix.LocalNetworkViewModel
import dev.eliaschen.skillflix.R
import dev.eliaschen.skillflix.component.FeaturedCard
import dev.eliaschen.skillflix.red
import dev.eliaschen.skillflix.schema.Featured
import dev.eliaschen.skillflix.schema.SortBy
import dev.eliaschen.skillflix.schema.VideoType

@Composable
fun ActualList(modifier: Modifier = Modifier) {
    val nav = LocalNavViewModel.current
    val api = LocalNetworkViewModel.current
    val type = nav.navType
    val searchQuery = rememberTextFieldState()
    var showSearch by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var selectedSortBy by remember { mutableStateOf(SortBy.Title) }
    val featuredList = remember { mutableStateListOf<Featured>() }

    LaunchedEffect(searchQuery.text, selectedSortBy) {
        val data =
            api.getFeatured(
                search = searchQuery.text.toString(),
                sortBy = selectedSortBy,
                type = type
            )
        featuredList.clear()
        featuredList.addAll(data)
    }

    Scaffold(topBar = {
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .statusBarsPadding()
        ) {
            if (!showSearch) {
                IconButton(
                    onClick = { nav.pop() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("精選${type.label}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .size(30.dp, 5.dp)
                            .background(red, RoundedCornerShape(20f))
                    )
                }
            }
            Row(modifier.align(Alignment.CenterEnd)) {
                if (showSearch) {
                    BasicTextField(
                        searchQuery,
                        textStyle = TextStyle(fontSize = 20.sp),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .weight(1f),
                        decorator = { textField ->
                            if (searchQuery.text.isEmpty()) {
                                Text("搜尋標題", fontSize = 20.sp, color = Color.Gray)
                            }
                            textField()
                        }
                    )
                }
                Box {
                    DropdownMenu(
                        showDropdownMenu,
                        onDismissRequest = { showDropdownMenu = false }) {
                        SortBy.entries.forEach {
                            val isSelected = it == selectedSortBy
                            DropdownMenuItem(text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(it.label, modifier = Modifier.weight(1f))
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }, onClick = {
                                selectedSortBy = it
                            })
                        }
                    }
                    IconButton(onClick = {
                        showDropdownMenu = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_filter_list_24),
                            contentDescription = null
                        )
                    }
                }
                IconButton(onClick = {
                    showSearch = !showSearch
                    searchQuery.clearText()
                }) {
                    Icon(
                        if (showSearch) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = null
                    )
                }
            }
        }
    }) { paddingValues ->
        Surface(color = Color.White) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), modifier = Modifier
                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + 20.dp,
                    bottom = paddingValues.calculateBottomPadding() + 20.dp,
                    start = 20.dp,
                    end = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(featuredList) { featured ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        FeaturedCard(featured, modifier = Modifier)
                    }
                }
            }
        }
    }
}