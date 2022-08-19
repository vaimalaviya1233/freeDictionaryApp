/*
 *     Owl: an android app for Owlbot Dictionary API
 *     MainTopBar.kt Created by Yamin Siahmargooei at 2022/7/10
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

package io.github.yamin8000.owl.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.github.yamin8000.owl.R
import io.github.yamin8000.owl.ui.composable.ClickableIcon
import io.github.yamin8000.owl.ui.composable.PersianText

data class MainTopAppBarCallbacks(
    val onHistoryClick: () -> Unit,
    val onFavouritesClick: () -> Unit,
    val onRandomWordClick: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onInfoClick: () -> Unit,
)

@Composable
fun MainTopBar(
    callbacks: MainTopAppBarCallbacks
) {
    GenericMainTopAppBar(callbacks)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun GenericMainTopAppBar(
    callbacks: MainTopAppBarCallbacks? = null
) {
    MediumTopAppBar(title = {
        PersianText(
            text = stringResource(R.string.app_name),
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }, actions = {
        ClickableIcon(
            iconPainter = painterResource(id = R.drawable.ic_history),
            contentDescription = stringResource(id = R.string.search_history),
        ) {
            callbacks?.onHistoryClick?.invoke()
        }

        ClickableIcon(
            iconPainter = painterResource(id = R.drawable.ic_favorites),
            contentDescription = stringResource(id = R.string.favourites),
        ) {
            callbacks?.onFavouritesClick?.invoke()
        }

        ClickableIcon(
            iconPainter = painterResource(id = R.drawable.ic_casino),
            contentDescription = stringResource(id = R.string.random_word),
        ) {
            callbacks?.onRandomWordClick?.invoke()
        }
        ClickableIcon(
            iconPainter = painterResource(id = R.drawable.ic_settings_applications),
            contentDescription = ""
        ) {
            callbacks?.onSettingsClick?.invoke()
        }
        ClickableIcon(
            iconPainter = painterResource(id = R.drawable.ic_contact_support),
            contentDescription = ""
        ) {
            callbacks?.onInfoClick?.invoke()
        }
    }, navigationIcon = {
        Icon(
            painterResource(R.drawable.ic_launcher_foreground),
            "Owl",
            tint = MaterialTheme.colorScheme.onSurface
        )
    })
}