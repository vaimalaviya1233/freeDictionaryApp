/*
 *     Owl: an android app for Owlbot Dictionary API
 *     Favourites.kt Created by Yamin Siahmargooei at 2022/8/22
 *     This file is part of Owl.
 *     Copyright (C) 2022  Yamin Siahmargooei
 *
 *     Owl is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Owl is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Owl.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.yamin8000.owl.content.favourites

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.yamin8000.owl.R
import io.github.yamin8000.owl.ui.composable.EmptyListErrorText
import io.github.yamin8000.owl.ui.composable.RemovableCard
import io.github.yamin8000.owl.ui.composable.SurfaceWithTitle
import io.github.yamin8000.owl.ui.composable.TextProvider
import io.github.yamin8000.owl.ui.util.theme.PreviewTheme
import kotlinx.coroutines.launch

@Composable
fun FavouritesContent(
    onFavouritesItemClick: (String) -> Unit
) {
    val favouritesState = rememberFavouritesState()

    SurfaceWithTitle(
        title = stringResource(R.string.favourites)
    ) {
        if (favouritesState.favourites.value.isNotEmpty()) {
            FavouritesGrid(
                favourites = favouritesState.favourites.value.toList(),
                onItemClick = onFavouritesItemClick,
                onItemLongClick = { favourite ->
                    favouritesState.lifeCycleScope.launch {
                        favouritesState.removeFavourite(favourite)
                    }
                }
            )
        } else EmptyListErrorText()
    }
}

@Composable
fun FavouritesGrid(
    favourites: List<String>,
    onItemClick: (String) -> Unit,
    onItemLongClick: (String) -> Unit
) {
    val gridColumns = if (favourites.size == 1) 1 else 2
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        columns = GridCells.Fixed(gridColumns)
    ) {
        items(favourites) { favourite ->
            FavouriteItem(
                favourite = favourite,
                onClick = onItemClick,
                onLongClick = { onItemLongClick(favourite) }
            )
        }
    }
}

@Composable
private fun FavouriteItem(
    @PreviewParameter(TextProvider::class)
    favourite: String,
    onClick: ((String) -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    RemovableCard(
        item = favourite,
        onClick = { onClick?.invoke(favourite) },
        onLongClick = { onLongClick?.invoke() }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun FavouriteItemPreview() {
    PreviewTheme { FavouriteItem("Owl") }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun FavouritesGridPreview() {
    PreviewTheme {
        FavouritesGrid(
            favourites = listOf("Owl", "Bird", "Android"),
            onItemClick = {},
            onItemLongClick = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun Preview() {
    PreviewTheme { FavouritesContent {} }
}