package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderGlow
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanDark
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NeonButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  testTag: String = "neon_button"
) {
  val infiniteTransition = rememberInfiniteTransition(label = "neon_pulse")
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.5f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_alpha"
  )

  val buttonBrush = if (enabled) {
    Brush.horizontalGradient(
      colors = listOf(
        NeonCyan,
        ElectricBlue
      )
    )
  } else {
    Brush.horizontalGradient(
      colors = listOf(
        Color(0xFF243046),
        Color(0xFF1B2438)
      )
    )
  }

  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .defaultMinSize(minHeight = 54.dp)
      .shadow(
        elevation = if (enabled) 12.dp else 0.dp,
        shape = RoundedCornerShape(14.dp),
        ambientColor = NeonCyan.copy(alpha = glowAlpha * 0.4f),
        spotColor = NeonCyan.copy(alpha = glowAlpha * 0.6f)
      )
      .border(
        width = 1.5.dp,
        brush = Brush.horizontalGradient(
          colors = listOf(
            NeonCyanLight.copy(alpha = if (enabled) glowAlpha else 0.2f),
            ElectricBlue.copy(alpha = if (enabled) glowAlpha else 0.2f)
          )
        ),
        shape = RoundedCornerShape(14.dp)
      )
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = Color.Transparent,
      contentColor = Color(0xFF03141F),
      disabledContainerColor = Color.Transparent,
      disabledContentColor = TextMuted
    ),
    contentPadding = ButtonDefaults.ContentPadding
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(buttonBrush)
        .padding(vertical = 14.dp, horizontal = 18.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        if (icon != null) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) Color(0xFF03141F) else TextMuted,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
        }
        Text(
          text = text,
          style = MaterialTheme.typography.labelLarge.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = if (enabled) Color(0xFF03141F) else TextMuted
        )
      }
    }
  }
}

@Composable
fun NeonSecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  borderColor: Color = NeonCyan.copy(alpha = 0.7f),
  testTag: String = "neon_secondary_button"
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier
      .defaultMinSize(minHeight = 48.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.2.dp, borderColor),
    colors = ButtonDefaults.outlinedButtonColors(
      containerColor = DarkSurfaceElevated.copy(alpha = 0.7f),
      contentColor = NeonCyanLight
    )
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = NeonCyan,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp
        )
      )
    }
  }
}

@Composable
fun NeonCard(
  modifier: Modifier = Modifier,
  borderColor: Color = DarkBorderGlow,
  glow: Boolean = false,
  cornerRadius: Dp = 16.dp,
  content: @Composable () -> Unit
) {
  Card(
    modifier = modifier
      .border(
        width = 1.dp,
        color = borderColor,
        shape = RoundedCornerShape(cornerRadius)
      )
      .shadow(
        elevation = if (glow) 8.dp else 2.dp,
        shape = RoundedCornerShape(cornerRadius),
        ambientColor = if (glow) NeonCyan.copy(alpha = 0.15f) else Color.Transparent,
        spotColor = if (glow) NeonCyan.copy(alpha = 0.25f) else Color.Transparent
      ),
    shape = RoundedCornerShape(cornerRadius),
    colors = CardDefaults.cardColors(
      containerColor = DarkSurfaceCard
    )
  ) {
    content()
  }
}

@Composable
fun StepBadge(
  stepNumber: Int,
  totalSteps: Int = 2,
  isCurrent: Boolean,
  isCompleted: Boolean,
  title: String,
  subtitle: String,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val borderColor = when {
    isCompleted -> AccentSuccess.copy(alpha = 0.6f)
    isCurrent -> NeonCyan
    else -> DarkBorder
  }

  val bgAlpha = if (isCurrent) 0.85f else 0.5f

  val clickableModifier = if (onClick != null) {
    Modifier.clickable { onClick() }
  } else Modifier

  Box(
    modifier = modifier
      .then(clickableModifier)
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSurfaceElevated.copy(alpha = bgAlpha))
      .border(1.dp, borderColor, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(
            when {
              isCompleted -> AccentSuccess.copy(alpha = 0.2f)
              isCurrent -> NeonCyan.copy(alpha = 0.2f)
              else -> Color(0xFF1E293B)
            }
          )
          .border(
            1.dp,
            when {
              isCompleted -> AccentSuccess
              isCurrent -> NeonCyan
              else -> DarkBorder
            },
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        if (isCompleted) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Completed",
            tint = AccentSuccess,
            modifier = Modifier.size(20.dp)
          )
        } else {
          Text(
            text = "$stepNumber",
            fontWeight = FontWeight.Bold,
            color = if (isCurrent) NeonCyan else TextSecondary,
            fontSize = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "STEP $stepNumber OF $totalSteps",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCurrent) NeonCyan else TextMuted,
            letterSpacing = 1.sp
          )
          if (isCurrent) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(NeonCyan)
            )
          }
        }
        Text(
          text = title,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary,
          fontSize = 14.sp
        )
        Text(
          text = subtitle,
          color = TextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp
        )
      }
    }
  }
}

@Composable
fun SecurityBadge(
  isHttps: Boolean,
  domain: String,
  modifier: Modifier = Modifier
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .background(Color(0xFF0C1424))
      .border(
        1.dp,
        if (isHttps) NeonCyan.copy(alpha = 0.4f) else Color(0xFFEF4444).copy(alpha = 0.4f),
        RoundedCornerShape(20.dp)
      )
      .padding(horizontal = 10.dp, vertical = 5.dp)
  ) {
    Icon(
      imageVector = Icons.Default.Lock,
      contentDescription = "Security Status",
      tint = if (isHttps) NeonCyan else Color(0xFFEF4444),
      modifier = Modifier.size(13.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = if (isHttps) "HTTPS • $domain" else "INSECURE • $domain",
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      color = if (isHttps) NeonCyanLight else Color(0xFFFCA5A5)
    )
  }
}
