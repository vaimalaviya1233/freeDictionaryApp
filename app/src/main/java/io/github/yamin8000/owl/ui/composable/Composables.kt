/*
 *     Owl: an android app for Owlbot Dictionary API
 *     Composables.kt Created by Yamin Siahmargooei at 2022/7/3
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

package io.github.yamin8000.owl.ui.composable

import android.speech.tts.TextToSpeech
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import io.github.yamin8000.owl.R
import io.github.yamin8000.owl.util.Constants.DNS_SERVERS
import io.github.yamin8000.owl.util.Constants.INTERNET_CHECK_DELAY
import io.github.yamin8000.owl.util.TTS
import io.github.yamin8000.owl.util.findActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun SwitchWithText(
    caption: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val internalChecked = rememberSaveable { mutableStateOf(checked) }
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clickable(
                role = Role.Switch,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    internalChecked.value = !internalChecked.value
                    onCheckedChange(internalChecked.value)
                }
            ),
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Switch(
                    checked = checked,
                    onCheckedChange = null
                )
                PersianText(caption)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Ripple(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        content = { content() },
        modifier = modifier
            .combinedClickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = onClick,
                onLongClick = onLongClick
            )
    )
}

@Composable
fun EmptyList() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_list))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
    )
}

@Composable
fun MySnackbar(
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
    dismissAction: @Composable (() -> Unit)? = null,
    actionOnNewLine: Boolean = false,
    containerColor: Color = SnackbarDefaults.color,
    contentColor: Color = SnackbarDefaults.contentColor,
    actionContentColor: Color = SnackbarDefaults.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
    content: @Composable () -> Unit
) {
    Snackbar(
        modifier = modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .padding(WindowInsets.ime.asPaddingValues()),
        action = action,
        dismissAction = dismissAction,
        actionOnNewLine = actionOnNewLine,
        shape = RoundedCornerShape(10.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = actionContentColor,
        dismissActionContentColor = dismissActionContentColor,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTitle(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onBackClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Surface(
                shadowElevation = 8.dp
            ) {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        PersianText(
                            modifier = Modifier.padding(8.dp),
                            text = title,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                    actions = {
                        ClickableIcon(
                            imageVector = Icons.TwoTone.ArrowBack,
                            contentDescription = "",
                            onClick = { onBackClick() }
                        )
                    }
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp)
                    .fillMaxHeight(),
                content = { content() }
            )
        }
    )
}

enum class IconClickState {
    Clicked,
    Normal;

    fun flip() = if (this == Clicked) Normal else Clicked
}

@Composable
fun ClickableIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val defaultSize = imageVector.defaultHeight

    var currentState by remember { mutableStateOf(IconClickState.Normal) }
    val transition = updateTransition(currentState, label = "")

    val tint by transition.animateColor(
        label = ""
    ) {
        when (it) {
            IconClickState.Normal -> LocalContentColor.current
            IconClickState.Clicked -> MaterialTheme.colorScheme.primary
        }
    }

    val size by transition.animateDp(
        label = ""
    ) {
        when (it) {
            IconClickState.Normal -> defaultSize
            IconClickState.Clicked -> (defaultSize.value * 1.25).dp
        }
    }

    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            currentState = currentState.flip()
            delay(300)
            currentState = currentState.flip()
        }
        isAnimating = false
    }

    ClickableIcon(
        modifier = modifier,
        onClick = {
            onClick()
            isAnimating = true
        },
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(size),
                tint = tint
            )
        }
    )
}

@Composable
fun ClickableIcon(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    IconButton(
        modifier = modifier,
        content = icon,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
    )
}

@Composable
fun TtsAwareContent(
    ttsLanguageLocaleTag: String = Locale.US.toLanguageTag(),
    content: @Composable (TextToSpeech) -> Unit
) {
    val ttsHelper = TTS(LocalContext.current, Locale.forLanguageTag(ttsLanguageLocaleTag))
    val tts: MutableState<TextToSpeech?> = remember { mutableStateOf(null) }
    LaunchedEffect(Unit) { tts.value = ttsHelper.getTts() }
    if (tts.value != null) tts.value?.let { content(it) }
}

@Composable
fun TtsAwareFeature(
    ttsLanguageLocaleTag: String = Locale.US.toLanguageTag(),
    onTtsReady: (TextToSpeech) -> Unit
) {
    val ttsHelper = TTS(LocalContext.current, Locale.forLanguageTag(ttsLanguageLocaleTag))
    val tts: MutableState<TextToSpeech?> = remember { mutableStateOf(null) }
    LaunchedEffect(Unit) { tts.value = ttsHelper.getTts() }
    if (tts.value != null) tts.value?.let { onTtsReady(it) }
}

@Composable
fun InternetAwareComposable(
    dnsServers: List<String> = DNS_SERVERS,
    delay: Long = INTERNET_CHECK_DELAY,
    successContent: (@Composable () -> Unit)? = null,
    errorContent: (@Composable () -> Unit)? = null,
    onlineChanged: ((Boolean) -> Unit)? = null
) {
    suspend fun dnsAccessible(
        dnsServer: String
    ) = try {
        withContext(Dispatchers.IO) {
            Runtime.getRuntime().exec("/system/bin/ping -c 1 $dnsServer").waitFor()
        } == 0
    } catch (e: Exception) {
        false
    }

    var isOnline by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            isOnline = dnsServers.any { dnsAccessible(it) }
            onlineChanged?.invoke(isOnline)
            delay(delay)
        }
    }
    if (isOnline) successContent?.invoke()
    else errorContent?.invoke()
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}