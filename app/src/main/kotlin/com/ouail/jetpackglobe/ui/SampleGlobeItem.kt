package com.ouail.jetpackglobe.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ouail.jetpackglobe.model.SampleUser

@Composable
fun SampleGlobeItem(
    user: SampleUser,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onDetailClick: () -> Unit = {}
) {
    val sizeDp = if (isSelected) 130.dp else 84.dp
    val avatarSizeDp = if (isSelected) 84.dp else 60.dp

    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(sizeDp),
            contentAlignment = Alignment.Center
        ) {
            // Clean Avatar with solid border & subtle elevation
            Box(
                modifier = Modifier
                    .size(avatarSizeDp)
                    .shadow(if (isSelected) 10.dp else 4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
                    .border(
                        width = if (isSelected) 3.dp else 1.5.dp,
                        color = if (isSelected) Color(0xFF0F172A) else Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Country Flag Badge (Top Left)
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = if (isSelected) (-34).dp else (-24).dp,
                        y = if (isSelected) (-34).dp else (-24).dp
                    )
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .padding(3.dp)
            ) {
                Text(
                    text = user.countryFlag,
                    fontSize = if (isSelected) 14.sp else 11.sp
                )
            }

            // City Tag (Top Right)
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = if (isSelected) 34.dp else 24.dp,
                        y = if (isSelected) (-34).dp else (-24).dp
                    )
                    .background(
                        color = if (isSelected) Color(0xFF0F172A) else Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color(0xFF0F172A) else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = user.city,
                    color = if (isSelected) Color.White else Color(0xFF0F172A),
                    fontSize = if (isSelected) 11.sp else 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Selected Name Capsule (Bottom) - Clean dark capsule with bold white text
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .background(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Globe Node - Normal", showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
fun SampleGlobeItemNormalPreview() {
    SampleGlobeItem(
        user = SampleUser(
            id = "1",
            name = "Sophia Chen",
            role = "AI Engineer",
            city = "Tokyo",
            countryFlag = "🇯🇵",
            avatarUrl = ""
        ),
        isSelected = false
    )
}

@Preview(name = "Globe Node - Selected", showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
fun SampleGlobeItemSelectedPreview() {
    SampleGlobeItem(
        user = SampleUser(
            id = "1",
            name = "Sophia Chen",
            role = "AI Engineer",
            city = "Tokyo",
            countryFlag = "🇯🇵",
            avatarUrl = ""
        ),
        isSelected = true
    )
}
