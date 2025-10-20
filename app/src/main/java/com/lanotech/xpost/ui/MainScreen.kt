package com.lanotech.xpost.ui

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.lanotech.xpost.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ScrollAwareWebView(
    url: String,
    scrollYFlow: MutableStateFlow<Float>
) {
    val context = LocalContext.current
    // Use a key to reset WebView when the URL changes (e.g., when the post is re-selected)
    AndroidView(
        factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                setOnScrollChangeListener { _, _, scrollY, _, _ ->
                    // Emit scroll position
                    scrollYFlow.value = scrollY.toFloat()
                }
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedPost by remember { mutableStateOf<String?>(null) }
    var showWebView by remember { mutableStateOf(false) }

    // Use a key to ensure a new flow is created and reset when a new post is selected
    val scrollYFlow = remember(selectedPost) { MutableStateFlow(0f) }

    // track web scroll
    var scrollY by remember { mutableStateOf(0f) }
    val maxScrollForMinimize = 600f // Scroll distance to achieve full minimization

    // Define card size limits
    val toolbarOnlyHeight = 80.dp  // when fully immersed
    val minCardHeight = toolbarOnlyHeight
    val maxHeight = 346.dp


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
        else maxHeight - (maxHeight - toolbarOnlyHeight) * progress,
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

    // Determine visibility states based on progress
    val isPartiallyImmersedOrExpanded = progress < 0.9f
    val isFullyExpanded = progress < 0.2f
    val isCollapsed = progress >= 0.9f

    // Box and content structure remain as in the previous fix
    // ...
    Box(modifier = Modifier.fillMaxSize()) {
        // 📰 Feed list
        if (!showWebView) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(10) {
                    PostCard(
                        onLinkClick = {
                            selectedPost = "https://docs.x.com/overview"
                            showWebView = true
                        }
                    )
                }
            }
        }

        // 🌐 WebView
        if (showWebView && selectedPost != null) {
            ScrollAwareWebView(url = selectedPost!!, scrollYFlow = scrollYFlow)
        }

        // 🧩 Floating post (bottom sheet card)
        if (selectedPost != null) {
            // 🚀 Transparent Floating Header (Control Bar) - Z-INDEX 3
            // The position is controlled dynamically to appear when card minimizes.
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
                    url = selectedPost!!, // Pass the current URL
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // --- 1. Profile Row (Visible unless fully collapsed) ---
                    if (isPartiallyImmersedOrExpanded) {
                        PostProfileRow(modifier = Modifier.padding(top = 16.dp))
                    } else {
                        // Ensure some space is taken up when fully collapsed if needed
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // --- 2. Post Content (Shrinks/Hides) ---
                    if (isFullyExpanded) { // Show full content only when fully expanded
                        PostTextContent(
                            text = "Urgently looking for an interior designer and architectural designer to work for Ayiks Construction Ltd. Starting Salary Ksh 100,000.\n\nQualification: Must have a master's degree in architecture.\n\nMungu Mbele.",
                            modifier = Modifier.padding(top = 8.dp).weight(1f)
                        )
                    } else if (isPartiallyImmersedOrExpanded) { // Show one line with 'Show more' implied
                        Text(
                            text = "Urgently looking for an interior designer and architectural designer to work for Ayiks Construction Ltd. Starting Salary Ksh 100,000.",
                            maxLines = 1,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 8.dp).weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    PostBottomToolbar()
                    Spacer(modifier = Modifier.height(4.dp))
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



@Composable
fun PostCard(onLinkClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PostProfileRow()
            Spacer(modifier = Modifier.height(10.dp))
            PostTextContent(
                text = "Urgently looking for an interior designer and architectural designer to work for Ayiks Construction Ltd. Starting Salary Ksh 100,000.\n\nQualification: Must have a master's degree in architecture.\n\nMungu Mbele."
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "https://docs.x.com/overview",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onLinkClick() }
            )
            Spacer(modifier = Modifier.height(8.dp))
            PostBottomToolbar()
        }
    }
}

@Composable
fun PostProfileRow(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        // Use Arrangement.SpaceBetween to push action items to the end
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        // --- 1. Profile Image and Text (Grouped) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            // Use Modifier.weight(1f) to allow the profile area to take up available space
            // and push the actions to the right edge.
            modifier = Modifier.weight(1f)
        ) {
            // Profile Image
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder
                contentDescription = "Profile",
                modifier = Modifier
                    .size(42.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color.Gray),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(10.dp))

            // User Text Details
            Column {
                Text("Peter Muli", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "@peter_mullih · 1h",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }

        // --- 2. Action Buttons (Pushed to the end) ---
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Follow Button
            Button(
                onClick = { /* Handle Follow action */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Follow", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            // MoreVert Icon
            IconButton(onClick = { /* Handle More actions */ }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun PostTextContent(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        modifier = modifier
    )
}

@Composable
fun PostBottomToolbar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icons using your R.drawable references
        IconButton(onClick = { /* Comment */ }) {
            Icon(painter = painterResource(id = R.drawable.outline_chat), contentDescription = "Comment")
        }
        IconButton(onClick = { /* Share */ }) {
            Icon(painter = painterResource(id = R.drawable.outline_cycle), contentDescription = "Share")
        }
        IconButton(onClick = { /* Like */ }) {
            Icon(painter = painterResource(id = R.drawable.outline_heart), contentDescription = "Like")
        }
        IconButton(onClick = { /* Bookmark */ }) {
            Icon(painter = painterResource(id = R.drawable.outline_bookmark), contentDescription = "Bookmark")
        }
        IconButton(onClick = { /* Share */ }) {
            Icon(painter = painterResource(id = R.drawable.outline_share), contentDescription = "Share")
        }
    }
}