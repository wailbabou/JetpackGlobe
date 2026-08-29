package com.ouail.jetpackglobe.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ouail.globe.GlobeDefaults
import com.ouail.globe.GlobeView
import com.ouail.jetpackglobe.data.SampleDataProvider
import com.ouail.jetpackglobe.model.SampleUser
import com.ouail.jetpackglobe.ui.theme.JetpackGlobeTheme

@Composable
fun GlobeShowcaseScreen() {
    val users = remember { SampleDataProvider.sampleUsers }
    var selectedUser by remember { mutableStateOf<SampleUser?>(null) }
    var isAutoRotate by remember { mutableStateOf(true) }

    val globeConfig = remember(isAutoRotate) {
        GlobeDefaults.DefaultConfig.copy(
            isAutoRotationEnabled = isAutoRotate,
            autoRotationDurationMs = 22000,
            selectedZoomScale = 1.5f,
            unselectedBlurRadius = 7.dp,
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // App Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Jetpack Globe",
                            color = Color(0xFF0F172A),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Interactive 3D Library Sample",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }

                    // Status Pill
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${users.size} Nodes",
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Control Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Auto-spin toggle
                    IconButton(
                        onClick = { isAutoRotate = !isAutoRotate },
                        modifier = Modifier
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (isAutoRotate) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Toggle Auto Rotation",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Reset selection
                    IconButton(
                        onClick = { selectedUser = null },
                        enabled = selectedUser != null,
                        modifier = Modifier
                            .shadow(if (selectedUser != null) 2.dp else 0.dp, CircleShape)
                            .background(if (selectedUser != null) Color.White else Color(0xFFF1F5F9), CircleShape)
                            .border(
                                1.dp,
                                if (selectedUser != null) Color(0xFFE2E8F0) else Color.Transparent,
                                CircleShape
                            )
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Selection",
                            tint = if (selectedUser != null) Color(0xFF0F172A) else Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 3D Globe Component
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    GlobeView(
                        items = users,
                        selectedItem = selectedUser,
                        onItemSelected = { selectedUser = it },
                        config = globeConfig,
                        wireframeColor = Color.Black.copy(alpha = 0.08f),
                        centerGlowColor = Color.Black.copy(alpha = 0.03f)
                    ) { user, isSelected, _ ->
                        SampleGlobeItem(
                            user = user,
                            isSelected = isSelected,
                            onDetailClick = { selectedUser = user }
                        )
                    }
                }

                // Bottom Detail Card when node is selected - Clean White Card with Black Text
                AnimatedVisibility(
                    visible = selectedUser != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    selectedUser?.let { user ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                                .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = user.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, Color(0xFFE2E8F0), CircleShape)
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.name,
                                            color = Color(0xFF0F172A),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = user.countryFlag, fontSize = 14.sp)
                                    }
                                    Text(
                                        text = "${user.role} • ${user.city}",
                                        color = Color(0xFF64748B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFEAB308),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${user.rating} rating",
                                            color = Color(0xFF64748B),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { /* Demo Action */ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0F172A),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Connect",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (selectedUser == null) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Drag to rotate • Tap node to center",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Globe Showcase Screen - Light", showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
fun GlobeShowcaseScreenPreview() {
    JetpackGlobeTheme(darkTheme = false) {
        GlobeShowcaseScreen()
    }
}
