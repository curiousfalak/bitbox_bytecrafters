package com.example.mltest.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mltest.R




@Composable
fun HomeScreen(nav: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Floating “Camera” button: goes straight to your geotag screen
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF87CEFA), Color(0xFFADD8E6)), // Light to deeper blue
                    ),
                    shape = CircleShape
                )
                .clickable { nav.navigate("camera") },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.img), // Replace with your image resource
                    contentDescription = "Fish",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Identify Species",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFADD8E6)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Take or upload photo to start",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // This row’s left button → camera, right → classifier
        ButtonRow("Upload Photo", "Browse Gallery", nav)
        Spacer(modifier = Modifier.height(16.dp))

        // You can wire these up similarly to other routes later
        ButtonRow("Report Threat", "View Map", nav)

    }
}

@Composable
fun ButtonRow(
    leftText: String,
    rightText: String,
    nav: NavController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ButtonBox(text = leftText, modifier = Modifier.weight(1f)) {
            when (leftText) {
                "Upload Photo" -> nav.navigate("camera")
                "Report Threat"  -> { /* TODO: nav.navigate("report_threat") */ }
            }
        }
        ButtonBox(text = rightText, modifier = Modifier.weight(1f)) {
            when (rightText) {
                "Browse Gallery" -> nav.navigate("classifier")
                "View Map"       -> { /* TODO: nav.navigate("map") */ }
            }
        }
    }
}

@Composable
fun ButtonBox(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(
                width = 4.dp,
                color = Color(0xFFD5EEF1), // Light blue border
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF87CEFA), Color(0xFFADD8E6)), // Light to deeper blue
                ), shape = RoundedCornerShape(12.dp))

            .size(120.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

