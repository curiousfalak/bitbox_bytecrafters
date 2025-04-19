package com.example.mltest.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Camera button
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFADD8E6), shape = CircleShape)
                .clickable { /* Handle camera click */ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Camera",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Heading text
        Text(
            text = "Identify Species",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFADD8E6)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subheading text
        Text(
            text = "Take or upload photo to start",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Button rows
        ButtonRow("Upload Photo", "Browse Gallery")
        Spacer(modifier = Modifier.height(16.dp))
        ButtonRow("Report Threat", "View Map")
        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun ButtonRow(leftText: String, rightText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ButtonBox(text = leftText, modifier = Modifier.weight(1f)) { /* Left button action */ }
        ButtonBox(text = rightText, modifier = Modifier.weight(1f)) { /* Right button action */ }
    }
}

@Composable
fun ButtonBox(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(Color(0xFFADD8E6), shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
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