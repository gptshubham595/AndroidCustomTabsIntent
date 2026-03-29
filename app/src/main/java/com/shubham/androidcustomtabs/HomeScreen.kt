package com.shubham.androidcustomtabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

private val Background = Color(0xFFF8F9FA)
private val SurfaceLow = Color(0xFFF3F4F5)
private val SurfaceCard = Color(0xFFFFFFFF)
private val SurfaceMuted = Color(0xFFEDEEEF)
private val TextPrimary = Color(0xFF191C1D)
private val TextSecondary = Color(0xFF586676)
private val TextMuted = Color(0xFF727783)
private val Primary = Color(0xFF005FB8)
private val PrimaryContainer = Color(0xFFD6E3FF)
private val TertiaryFixed = Color(0xFFFFDBCB)
private val TertiaryFixedText = Color(0xFF341100)
private val OutlineSoft = Color(0x1F727783)
private val ErrorSoft = Color(0xFFB91C1C)

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
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 24.dp,
                    bottom = 140.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    EditorialHeader()
                }

                item {
                    if (session != null) {
                        SessionCard(session = session, onClearSession = onClearSession)
                    }
                }

                item {
                    EndpointInput(
                        url = url,
                        onUrlChange = onUrlChange,
                        errorMessage = errorMessage,
                        onGo = onCustomTabsClick
                    )
                }

                item {
                    ArchitectureCard(
                        onCustomTabsClick = onCustomTabsClick,
                        onWebViewClick = onWebViewClick
                    )
                }
            }
        }
    }
}

@Composable
private fun EditorialHeader() {
    Column {
        Surface(
            color = TertiaryFixed,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
        ) {
            Text(
                text = "Architecture Lab",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = TertiaryFixedText
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "CustomTabs vs WebView login sso",
            fontSize = 36.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select the implementation architecture for your authentication flow.",
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun EndpointInput(
    url: String,
    onUrlChange: (String) -> Unit,
    errorMessage: String,
    onGo: () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Backend Endpoint",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                color = TextPrimary,
                fontSize = 14.sp
            ),
            placeholder = {
                Text(
                    text = "http://10.0.2.2:3000",
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = { onGo() }),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard,
                focusedBorderColor = Primary,
                unfocusedBorderColor = OutlineSoft,
                cursorColor = Primary
            )
        )

        AnimatedVisibility(
            visible = errorMessage.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorMessage,
                color = ErrorSoft,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun ArchitectureCard(
    onCustomTabsClick: () -> Unit,
    onWebViewClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OptionCard(
            recommended = true,
            title = "Login with Custom Tabs",
            subtitle = "Recommended for SSO • browser cookies • account chooser reuse",
            icon = { Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Primary, modifier = Modifier.size(30.dp)) },
            bullets = listOf(
                "Shared browser session",
                "Great for SSO/OAuth",
                "Less auth surface in app"
            ),
            onClick = onCustomTabsClick
        )

        OptionCard(
            recommended = false,
            title = "Login with WebView",
            subtitle = "Demo/legacy path • in-app JS bridge • isolated auth handling",
            icon = { Icon(Icons.Default.Terminal, contentDescription = null, tint = TextMuted, modifier = Modifier.size(30.dp)) },
            bullets = listOf(
                "Embedded UX",
                "Requires JS bridge",
                "You own more security work"
            ),
            onClick = onWebViewClick
        )
    }
}

@Composable
private fun OptionCard(
    recommended: Boolean,
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    bullets: List<String>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (recommended) SurfaceCard else SurfaceLow
        ),
        border = BorderStroke(
            1.dp,
            if (recommended) Primary.copy(alpha = 0.1f) else Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (recommended) 6.dp else 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (recommended) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(bottomStart = 16.dp))
                        .background(PrimaryContainer)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RECOMMENDED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (recommended) PrimaryContainer.copy(alpha = 0.3f) else SurfaceMuted),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    bullets.forEach { bullet ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (recommended) Primary else TextMuted)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = bullet,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SchemaNode(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(108.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFF0F4F8), Color(0xFFE3EAF3))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary.copy(alpha = 0.4f),
                modifier = Modifier.size(42.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SessionCard(session: DemoSession, onClearSession: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "ACTIVE SESSION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
                color = Primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = session.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = session.email,
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "source: ${session.source} · via ${session.transport}",
                fontSize = 12.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onClearSession,
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.18f))
            ) {
                Text("Clear local session", color = Primary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            url = "http://10.0.2.2:3000",
            onUrlChange = {},
            errorMessage = "",
            session = DemoSession("Demo User", "demo@sso.com", "login", "deep-link"),
            onCustomTabsClick = {},
            onWebViewClick = {},
            onClearSession = {}
        )
    }
}
