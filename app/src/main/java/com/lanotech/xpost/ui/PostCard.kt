package com.lanotech.xpost.ui

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.lanotech.xpost.R
import com.lanotech.xpost.data.PostData

@Composable
fun PostCard(
    post: PostData, //  New parameter: PostData object
    onLinkClick: (String) -> Unit, //  Click handler that accepts a URL String
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val transitionProgress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "cardTransition"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { post.url?.let { onLinkClick(it) } }
            .graphicsLayer {
                // Scale up slightly when selected
                scaleX = 1f + (0.05f * transitionProgress)
                scaleY = 1f + (0.05f * transitionProgress)
                // Move up as it transitions
                translationY = -50f * transitionProgress
                alpha = 1f - transitionProgress  // Fade out original
            }
            .zIndex(if (isSelected) 10f else 0f)  // Bring to front
    ) {
        Surface (
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    post.url?.let { onLinkClick(it) }
                },
            shadowElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Pass data to the profile row
                PostProfileRow(
                    userName = post.userName,
                    userHandle = post.userHandle,
                    time = post.time
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Post content
                PostTextContent(text = post.content)

                // Display URL if it exists (no separate clickable modifier needed here as the card handles it)
                post.urlText?.let { urlText ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = urlText,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                PostBottomToolbar()
            }
        }
    }
}

@Composable
fun PostProfileRow(
    userName: String,
    userHandle: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        // --- 1. Profile Image and Text (Grouped) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Profile Image
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder
                contentDescription = "Profile",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(10.dp))

            // User Text Details
            Column {
                Text(userName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "$userHandle · $time",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }

        // --- 2. Action Buttons (Pushed to the end) ---
        Row(verticalAlignment = Alignment.CenterVertically) {
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
fun PostTextContent(
    text: String,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    onShowMoreClick: () -> Unit = {},
    ) {
    if (isExpanded) {
        // Full Content (when card is fully expanded or in the feed list)
        Text(
            text = text,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            modifier = modifier
        )
    } else {
        // Truncated Content with "Show More" (when card is partially expanded)
        val singleLineText = text.substringBefore('\n').substringBefore('.') + "..." // Simple way to get the first sentence

        Row(
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = singleLineText,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )

            // "Show More" Button/Text (This is the visual "Show More")
            Text(
                text = "Show More",
                color = MaterialTheme.colorScheme.primary, // Highlight color
                fontSize = 15.sp,
                lineHeight = 20.sp,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clickable(onClick = onShowMoreClick)
            )
        }
    }
}

@Composable
fun PostBottomToolbar(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Internal States
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(12) }  // example initial number
    var commentCount by remember { mutableIntStateOf(3) }
    var repostCount by remember { mutableIntStateOf(1) }
    var bookmarkCount by remember { mutableIntStateOf(0) }

    val likeColor by animateColorAsState(
        targetValue = if (isLiked) Color(0xFFE0245E) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        label = "likeColorAnimation"
    )

    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🗨️ Comments
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                commentCount++
                Toast.makeText(context, "Opening comments...", Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comment",
                    tint = textColor
                )
            }
            if (commentCount > 0) {
                Text(
                    text = commentCount.toString(),
                    color = textColor,
                    fontSize = 12.sp
                )
            }
        }

        // 🔁 Repost
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                repostCount++
                Toast.makeText(context, "Reposted!", Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    Icons.Outlined.Loop,
                    contentDescription = "Repost",
                    tint = textColor
                )
            }
            if (repostCount > 0) {
                Text(
                    text = repostCount.toString(),
                    color = textColor,
                    fontSize = 12.sp
                )
            }
        }

        // ❤️ Like
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    isLiked = !isLiked
                    likeCount += if (isLiked) 1 else -1
                }
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isLiked) "Unlike" else "Like",
                    tint = likeColor
                )
            }
            if (likeCount > 0) {
                Text(
                    text = likeCount.toString(),
                    color = textColor,
                    fontSize = 12.sp
                )
            }
        }

        // 🔖 Bookmark
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                bookmarkCount++
                Toast.makeText(context, "Post bookmarked!", Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = textColor
                )
            }
            if (bookmarkCount > 0) {
                Text(
                    text = bookmarkCount.toString(),
                    color = textColor,
                    fontSize = 12.sp
                )
            }
        }

        // 📤 Share (no count)
        IconButton(onClick = {
            Toast.makeText(context, "Share menu opened", Toast.LENGTH_SHORT).show()
        }) {
            Icon(
                Icons.Outlined.Share,
                contentDescription = "Share",
                tint = textColor
            )
        }
    }
}
