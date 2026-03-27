package com.shubham.androidcustomtabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    url: String,
    onUrlChange: (String) -> Unit,
    errorMessage: String,
    session: DemoSession?,
    onCustomTabsClick: () -> Unit,
    onWebViewClick: () -> Unit,
    onClearSession: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SSO",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.W100,
                fontSize = 11.sp,
                letterSpacing = 8.sp,
                color = Color(0xFF38BDF8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Login Demo",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp,
                color = Color.White,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Compare browser-based Custom Tabs against embedded WebView auth",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "For real SSO, prefer Custom Tabs because the browser keeps cookies, saved accounts, and stronger security boundaries.",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 18.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (session != null) {
                SessionCard(session = session, onClearSession = onClearSession)
                Spacer(modifier = Modifier.height(24.dp))
            }

            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "http://10.0.2.2:3000",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF475569),
                        fontSize = 14.sp
                    )
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = { onCustomTabsClick() }),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = Color(0xFF1E293B),
                    cursorColor = Color(0xFF38BDF8),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A)
                )
            )

            Text(
                text = "Use 10.0.2.2 for the Android emulator to reach your local Node server.",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Color(0xFF475569),
                modifier = Modifier.padding(top = 8.dp)
            )

            AnimatedVisibility(
                visible = errorMessage.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFDA4AF),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onCustomTabsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E),
                    contentColor = Color(0xFF04130A)
                )
            ) {
                Text(
                    text = "Login with Custom Tabs",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "recommended for SSO · browser cookies · account chooser reuse",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Color(0xFF475569),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onWebViewClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
            ) {
                Text(
                    text = "Login with WebView",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "demo/legacy path · in-app JS bridge · isolated auth handling",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Color(0xFF475569),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Custom Tabs",
                    accentColor = Color(0xFF22C55E),
                    points = listOf("Shared browser session", "Great for SSO/OAuth", "Less auth surface in app")
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "WebView",
                    accentColor = Color(0xFF38BDF8),
                    points = listOf("Embedded UX", "Requires JS bridge", "You own more security work")
                )
            }
        }
    }
}

@Composable
private fun SessionCard(session: DemoSession, onClearSession: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "ACTIVE SESSION",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            color = Color(0xFF86EFAC)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = session.name,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White
        )
        Text(
            text = session.email,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = Color(0xFFCBD5E1)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "source: ${session.source} · via ${session.transport}",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onClearSession) {
            Text("Clear local session")
        }
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    accentColor: Color,
    points: List<String>
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF111827))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        points.forEach { point ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(accentColor.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = point,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        url = "http://10.0.2.2:3000",
        onUrlChange = {},
        errorMessage = "",
        session = DemoSession("Demo User", "demo@sso.com", "login", "webview-bridge"),
        onCustomTabsClick = {},
        onWebViewClick = {},
        onClearSession = {}
    )
}
