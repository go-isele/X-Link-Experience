package com.lanotech.xpost.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lanotech.xpost.R

@Composable
fun PostCardOld(
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Peter Muli", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = "@peter_mullih · 1h",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Text
            Text(
                text = "Urgently looking for an interior designer and architectural designer to work for Ayiks Construction Ltd. Starting Salary Ksh 100,000.\n\nQualification: Must have a master's degree in architecture.\n\nMungu Mbele.",
                fontSize = 15.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Link
            Text(
                text = "https://kinandu.somocloud.org/",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onLinkClick() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🧭 Action Row (Like, Comment, Share, Bookmark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* Like */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_heart),
                        contentDescription = "Like",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { /* Comment */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_chat),
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { /* Share */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_share),
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { /* Bookmark */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_bookmark),
                        contentDescription = "Bookmark",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

