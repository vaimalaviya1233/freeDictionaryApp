/*
 *     Owl: an android app for Owlbot Dictionary API
 *     HomeState.kt Created by Yamin Siahmargooei at 2022/8/22
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

package io.github.yamin8000.owl.ui.home

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.github.yamin8000.owl.R
import io.github.yamin8000.owl.model.Definition
import io.github.yamin8000.owl.model.RandomWord
import io.github.yamin8000.owl.model.Word
import io.github.yamin8000.owl.network.APIs
import io.github.yamin8000.owl.network.Web
import io.github.yamin8000.owl.network.Web.getAPI
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class HomeState(
    val listState: LazyListState,
    var isSearching: MutableState<Boolean>,
    val lifecycleOwner: LifecycleOwner,
    private val focusManager: FocusManager,
    var searchText: String,
    var rawWordSearchBody: MutableState<Word?>,
    var searchResult: MutableState<List<Definition>>,
    var errorMessage: MutableState<String>,
    private val context: Context
) {
    private val scope = lifecycleOwner.lifecycleScope.coroutineContext

    val isShowingError: Boolean
        get() = errorMessage.value.isNotBlank()

    val floatingActionButtonVisibility: Boolean
        get() = !listState.isScrollInProgress

    suspend fun searchForRandomWord() {
        reset()
        val randomWord = withContext(scope) {
            try {
                getNewRandomWord()
            } catch (e: HttpException) {
                getNewRandomWord()
            } catch (e: Exception) {
                null
            }
        }
        searchText = randomWord?.word ?: ""
        if (searchText.isBlank()) searchForRandomWord()
        withContext(scope) {
            searchForDefinition()
        }
    }

    private suspend fun internalSearchForDefinition(
        searchTerm: String
    ): Word? {
        reset()
        searchText = searchTerm
        val body = withContext(scope) {
            try {
                Web.retrofit.getAPI<APIs.OwlBotWordAPI>().searchWord(searchTerm)
            } catch (e: HttpException) {
                errorMessage.value = getErrorMessage(e.code(), context)
                null
            } catch (e: Exception) {
                errorMessage.value = getErrorMessage(999, context)
                null
            }
        }
        isSearching.value = false
        return body
    }

    suspend fun searchForDefinition() {
        rawWordSearchBody.value = withContext(scope) {
            internalSearchForDefinition(searchText)
        }
        searchResult.value = rawWordSearchBody.value?.definitions ?: listOf()
        searchResult.value = searchResult.value.sortedByDescending { it.imageUrl }
    }

    private suspend fun getNewRandomWord(): RandomWord {
        return Web.ninjaApiRetrofit.getAPI<APIs.NinjaAPI>().getRandomWord()
    }

    private fun reset() {
        focusManager.clearFocus()
        isSearching.value = true
        errorMessage.value = ""
    }
}

@Composable
fun rememberHomeState(
    listState: LazyListState = rememberLazyListState(),
    isSearching: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    focusManager: FocusManager = LocalFocusManager.current,
    searchText: String = rememberSaveable { mutableStateOf("").value },
    rawWordSearchBody: MutableState<Word?> = rememberSaveable { mutableStateOf(null) },
    searchResult: MutableState<List<Definition>> = rememberSaveable { mutableStateOf(emptyList()) },
    errorMessage: MutableState<String> = rememberSaveable { mutableStateOf("") },
    context: Context = LocalContext.current
) = remember(
    listState,
    isSearching,
    lifecycleOwner,
    focusManager,
    searchText,
    rawWordSearchBody,
    searchResult,
    errorMessage,
    context
) {
    HomeState(
        listState,
        isSearching,
        lifecycleOwner,
        focusManager,
        searchText,
        rawWordSearchBody,
        searchResult,
        errorMessage,
        context
    )
}

private fun getErrorMessage(
    code: Int,
    context: Context
) = when (code) {
    401 -> context.getString(R.string.api_authorization_error)
    404 -> context.getString(R.string.definition_not_found)
    429 -> context.getString(R.string.api_throttled)
    else -> context.getString(R.string.general_net_error)
}