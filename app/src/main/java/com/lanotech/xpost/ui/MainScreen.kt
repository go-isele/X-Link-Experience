package com.lanotech.xpost.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.lanotech.xpost.R
import com.lanotech.xpost.data.PostData
import com.lanotech.xpost.data.samplePosts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedPost by remember { mutableStateOf<PostData?>(null) }
    var showWebView by remember { mutableStateOf(false) }

    // Use a key to ensure a new flow is created and reset when a new post is selected
    val scrollYFlow = remember(selectedPost) { MutableStateFlow(0f) }

    // track web scroll
    var scrollY by remember { mutableFloatStateOf(0f) }
    val maxScrollForMinimize = 600f // Scroll distance to achieve full minimization

    // Define card size limits
    val toolbarOnlyHeight = 80.dp  // when fully immersed
    val minCardHeight = toolbarOnlyHeight
    val maxHeight = 306.dp

    var measuredContentHeight by remember { mutableStateOf(maxHeight) }


    // **FIXED VALUE**: Maximum distance the card should slide UP (negative offset)
    // -100.dp is a safe value to prevent it from sliding too far off screen.
    val maxSlideUp = 1.dp // Keep the card's slide up for the float effect
    val controlBarHeight = 56.dp
    val cardPadding = 8.dp


    // The combined height change for both the card and the header.
    val combinedMaxHeight = maxHeight + controlBarHeight + (cardPadding * 2)
    val combinedMinHeight = minCardHeight + controlBarHeight + (cardPadding * 2)
    val combinedHeightReductionRange = combinedMaxHeight - combinedMinHeight


    // The range of height change: 360.dp - 120.dp = 240.dp
    val heightReductionRange = maxHeight - minCardHeight
    // drag state (removed interaction logic but kept state to track any future drag)
    val dragOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(scrollYFlow) {
        scrollYFlow.collectLatest { scrollY = it }
    }

    // blend scroll + drag -> normalized progress
    val effectiveScroll = (scrollY + dragOffset).coerceIn(0f, maxScrollForMinimize)
    val progress = (effectiveScroll / maxScrollForMinimize).coerceIn(0f, 1f)

    // card animation values

    // **FIXED LOGIC**: Use .toFloat() and Dp arithmetic
    val cardHeight by animateDpAsState(
        targetValue = if (progress >= 0.95f) toolbarOnlyHeight
        else measuredContentHeight - (measuredContentHeight - toolbarOnlyHeight) * progress,
        label = "heightAnim"
    )


    // **FIXED LOGIC**: Use .toFloat()
    val offsetY by animateDpAsState(
        // Start at 0.dp (bottom-aligned). When progress=1, move UP by maxSlideUp (e.g., -100.dp)
        targetValue = -maxSlideUp * progress,
        label = "offsetAnim"
    )

    // This variable represents the final resting vertical distance of the header's top from the screen bottom.
    val combinedHeight by animateDpAsState(
        targetValue = combinedMaxHeight - combinedHeightReductionRange * progress.toFloat(),
        label = "combinedHeightAnim"
    )

    // The distance from the BOTTOM of the card's total space (including padding) to the card's top edge.
    // Note: offsetY is negative, so adding it accounts for the upward slide.
    val cardTopEdgeFromBottom by animateDpAsState(
        targetValue = cardHeight + (cardPadding * 2) + offsetY,
        label = "cardTopEdgeFromBottomAnim"
    )

    // **FIXED LOGIC**: Use .toFloat()
    val cornerRadius by animateDpAsState(
        targetValue = (24.dp - (16.dp * progress)),
        label = "cornerAnim"
    )

    var loadingProgress by remember { mutableIntStateOf(0) }

    // Determine visibility states based on progress
    val isPartiallyImmersedOrExpanded = progress < 0.9f
    val isFullyExpanded = progress < 0.2f
    val isCollapsed = progress >= 0.9f

    // Box and content structure remain as in the previous fix
    // ...
    Box(modifier = Modifier.fillMaxSize()) {
        // Feed list
        // In MainScreen:
        // 📰 Feed list
        if (!showWebView) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 👇 UPDATE 1: Iterate over the samplePosts list
                items(samplePosts, key = { it.id }) { post ->
                    PostCard(
                        post = post, // Pass the PostData object
                        onLinkClick = { url ->
                            selectedPost = post
                            showWebView = true
                        },
                        isSelected = selectedPost?.id == post.id
                    )
                }
            }
        }
// ...

        // 🌐 WebView
        if (showWebView && selectedPost != null) {
            // 👇 FIX: Assign the delegated property to a local variable to enable smart cast
            val post = selectedPost

            if (loadingProgress < 100) {
                LinearProgressIndicator(
                    progress = { loadingProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .zIndex(5f) // Ensure it's above everything
                )
            }

            post?.url?.let { url ->
                ScrollAwareWebView(
                    url = url,
                    scrollYFlow = scrollYFlow,
                    onProgressChange = { loadingProgress = it }
                )
            }
        }

        // Measurement for content height
        val configuration = LocalConfiguration.current
        if (selectedPost != null) {
            Column(
                modifier = Modifier
                    .offset(x = (-configuration.screenWidthDp - 100).dp)
                    .alpha(0f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .onGloballyPositioned { coordinates ->
                        measuredContentHeight = coordinates.size.height.dp.coerceIn(minCardHeight, maxHeight)
                    }
            ) {
                PostProfileRow(
                    userName = selectedPost!!.userName,
                    userHandle = selectedPost!!.userHandle,
                    time = selectedPost!!.time,
                    modifier = Modifier
                        .padding(top = 16.dp)
                )
                PostTextContent(
                    text = selectedPost!!.content,
                    modifier = Modifier.padding(top = 8.dp)
                )
                PostBottomToolbar()
            }
        }

        // 🧩 Floating post (bottom sheet card)
        if (selectedPost != null) {
            // 🚀 Transparent Floating Header (Control Bar) - Z-INDEX 3
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = cardPadding)
                    .offset(y = offsetY - (combinedHeight - controlBarHeight - cardPadding * 2))
                    .zIndex(3f) // HIGHER Z-index to float above the card
                    .background(Color.Transparent)
                    .height(controlBarHeight)
            ) {
                FloatingControlBar(
                    url = selectedPost!!.url ?: "", // Pass the current URL
                    onClose = {
                        showWebView = false
                        selectedPost = null
                    },
                    onMore = {
                        showWebView = false
                        selectedPost = null
                    },
                    onReload = {
                        selectedPost?.let {
                            showWebView = false
                            selectedPost = it // Trigger recomposition to reload
                            showWebView = true
                        }
                    },
                    progress = progress,
                    modifier = Modifier.fillMaxSize()
                )
                Spacer(Modifier.height(6.dp))
            }

            // 🧩 Floating post (bottom sheet card) - Z-INDEX 2
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY)
                    .zIndex(2f)
                    .clip(MaterialTheme.shapes.large.copy(all = CornerSize(cornerRadius)))
                    .background(MaterialTheme.colorScheme.surface)
                    .height(cardHeight)
            ) {
                selectedPost?.let { post ->
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 16.dp)

                    ) {
                        // --- 1. Profile Row (Visible unless fully collapsed) ---
                        if (isPartiallyImmersedOrExpanded) {
                            val profileAlpha = (1f - progress * 2f).coerceIn(0.2f, 1f) // Fade out faster
                            val profileScale = (1f - progress * 0.2f).coerceIn(0.8f, 1f) // Slight shrink

                            PostProfileRow(
                                userName = post.userName,
                                userHandle = post.userHandle,
                                time = post.time,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .graphicsLayer(
                                        alpha = profileAlpha,
                                        scaleX = profileScale,
                                        scaleY = profileScale,
                                        transformOrigin = TransformOrigin(0f, 0f)
                                    )
                            )
                        } else {
                            // Ensure some space is taken up when fully collapsed if needed
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // --- 2. Post Content (Shrinks/Hides) ---
                        if (isFullyExpanded) { // Show full content only when fully expanded
                            PostTextContent(
                                text = post.content,
                                modifier = Modifier.padding(top = 8.dp).weight(1f)
                            )
                        } else if (isPartiallyImmersedOrExpanded) { // Show one line with 'Show more' implied
                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .weight(1f)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    // Use the actual post content
                                    text = post.content.substringBefore('\n').substringBefore('.') + if (post.content.contains(' ')) "..." else "",
                                    maxLines = 1,
                                    fontSize = 15.sp,
                                    overflow = TextOverflow.Ellipsis, // Ensure it truncates if too long
                                    modifier = Modifier.weight(1f, fill = false)
                                )

                                Text(
                                    text = " Show More", // Added a space for separation
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clickable { /* Handle click to fully expand the card if needed, or simply read the text */ }
                                        .padding(start = 4.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        PostBottomToolbar()
                    }
                }
            }
        }
    }
}

/**
 * Parses a full URL string to extract just the domain part (e.g., "kinandu.somocloud.org").
 * @param url The full URL string.
 * @return The stripped domain string.
 */
private fun stripUrlPrefix(url: String): String {
    return url
        .replace("https://", "")
        .replace("http://", "")
        .removeSuffix("/")
}

@Composable
fun FloatingControlBar(
    url: String,
    onClose: () -> Unit,
    onReload: () -> Unit,
    onMore: () -> Unit, // 👈 new action handler
    progress: Float, // immersion progress 0–1
    modifier: Modifier = Modifier
) {
    val scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
    val contentColor = MaterialTheme.colorScheme.onSurface

    val sideAlpha = 1f - progress
    val centerScale = 1f - (0.2f * progress)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .height(46.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1️⃣ Close Button
        if (sideAlpha > 0.05f) {
            Box(
                modifier = Modifier
                    .graphicsLayer(alpha = sideAlpha)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(scrimColor)
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close Post View",
                        tint = contentColor
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }

        // 2️⃣ Center Link
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = centerScale,
                        scaleY = centerScale
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .background(scrimColor)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Secure Connection",
                    tint = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stripUrlPrefix(url),
                    color = contentColor,
                    fontSize = 13.sp,
                    maxLines = 1,
                )
            }
        }

        // 3️⃣ Reload Button
        if (sideAlpha > 0.05f) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .graphicsLayer(alpha = sideAlpha)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(scrimColor)
                ) {
                    IconButton(onClick = onReload) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_refresh),
                            contentDescription = "Reload Page",
                            tint = contentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 4️⃣ More Button (new)
                Box(
                    modifier = Modifier
                        .graphicsLayer(alpha = sideAlpha)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(scrimColor)
                ) {
                    IconButton(onClick = onMore) {
                        Icon(
                            painter = painterResource(R.drawable.outline_more),
                            contentDescription = "More Options",
                            tint = contentColor
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.width(96.dp))
        }
    }
}