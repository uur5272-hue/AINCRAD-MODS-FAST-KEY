package com.example.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.SavedKey
import com.example.data.WorkflowConstants
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonCard
import com.example.ui.components.NeonSecondaryButton
import com.example.ui.components.SecurityBadge
import com.example.ui.components.StepBadge
import com.example.ui.theme.AccentError
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderGlow
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  onStartWorkflow: () -> Unit,
  onOpenKeyPageDirectly: () -> Unit,
  savedKeys: List<SavedKey>,
  step1Done: Boolean,
  step2Done: Boolean,
  onDeleteKey: (String) -> Unit,
  onClearAllKeys: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  var showInfoDialog by remember { mutableStateOf(false) }

  fun copyKey(key: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Aincrad Key", key)
    clipboard.setPrimaryClip(clip)
    coroutineScope.launch {
      snackbarHostState.showSnackbar("Copied key: $key")
    }
    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFF03141F))
                .border(1.2.dp, NeonCyan, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Key,
                contentDescription = "App Icon",
                tint = NeonCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "AINCRAD",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = NeonCyan
              )
              Text(
                text = "KEY ASSISTANT",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontSize = 10.sp,
                  letterSpacing = 1.sp
                ),
                color = TextSecondary
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = { showInfoDialog = true },
            modifier = Modifier.testTag("info_dialog_button")
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Security Info",
              tint = NeonCyanLight
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = DarkSurface,
          titleContentColor = TextPrimary
        )
      )
    },
    containerColor = DarkBackground,
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 18.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(6.dp))
      }

      // Hero Banner Card
      item {
        NeonCard(
          glow = true,
          borderColor = DarkBorderGlow,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color(0xFF131D30),
                    DarkSurfaceCard
                  )
                )
              )
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            SecurityBadge(
              isHttps = true,
              domain = "Secure Verification Workflow",
              modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
              text = "One-Tap Key Access",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
              ),
              color = TextPrimary,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Launch guided verification, fulfill website instructions, and retrieve your access key in moments.",
              style = MaterialTheme.typography.bodyMedium,
              color = TextSecondary,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Prominent "GET KEY" Button (Requirement 3)
            NeonButton(
              text = "GET KEY",
              icon = Icons.Default.PlayArrow,
              onClick = onStartWorkflow,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("get_key_button")
            )
          }
        }
      }

      // Workflow Steps Section
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "GUIDED WORKFLOW STEPS",
            style = MaterialTheme.typography.labelLarge.copy(
              letterSpacing = 1.sp,
              fontWeight = FontWeight.Bold
            ),
            color = NeonCyan,
            modifier = Modifier.padding(bottom = 10.dp)
          )

          // Step 1
          StepBadge(
            stepNumber = 1,
            isCurrent = !step1Done,
            isCompleted = step1Done,
            title = "Visit Sponsor Page",
            subtitle = WorkflowConstants.INITIAL_DOMAIN + " • Fulfill site rules",
            onClick = onStartWorkflow,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("step_1_badge")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Step 2
          StepBadge(
            stepNumber = 2,
            isCurrent = step1Done && !step2Done,
            isCompleted = step2Done,
            title = "Aincrad Mods Key Generation",
            subtitle = WorkflowConstants.KEY_PAGE_DOMAIN + " • Verify & retrieve key",
            onClick = onOpenKeyPageDirectly,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("step_2_badge")
          )
        }
      }

      // Quick Action Buttons
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          NeonSecondaryButton(
            text = "Direct to Step 2",
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            onClick = onOpenKeyPageDirectly,
            modifier = Modifier
              .weight(1f)
              .testTag("direct_step_2_button")
          )

          NeonSecondaryButton(
            text = "Chrome Browser",
            icon = Icons.AutoMirrored.Filled.OpenInNew,
            onClick = {
              try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(WorkflowConstants.INITIAL_WEBSITE_URL))
                context.startActivity(intent)
              } catch (e: Exception) {
                Toast.makeText(context, "Cannot open external browser", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier
              .weight(1f)
              .testTag("open_external_browser_button")
          )
        }
      }

      // Key Vault Header
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Key,
              contentDescription = null,
              tint = NeonTeal,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "KEY VAULT (${savedKeys.size})",
              style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
              ),
              color = TextPrimary
            )
          }

          if (savedKeys.isNotEmpty()) {
            TextButton(onClick = onClearAllKeys) {
              Text("Clear All", color = TextMuted, fontSize = 12.sp)
            }
          }
        }
      }

      // Key Vault List
      if (savedKeys.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated.copy(alpha = 0.6f))
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.Key,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "No keys retrieved yet",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
              )
              Text(
                text = "Tap 'GET KEY' above to launch the verification portal and generate your access key.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
              )
            }
          }
        }
      } else {
        items(savedKeys, key = { it.id }) { item ->
          KeyItemCard(
            keyItem = item,
            onCopy = { copyKey(item.keyText) },
            onDelete = { onDeleteKey(item.id) }
          )
        }
      }

      // Reliability & Security Notice Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = "Safe Policy",
              tint = NeonCyan,
              modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Privacy & Verification Compliance",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "This assistant loads the official verification workflow in a secure container. It does NOT bypass CAPTCHAs, bot protections, or access controls. Private tokens and passwords are never logged or exported.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                color = TextSecondary
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Security Information Dialog
  if (showInfoDialog) {
    AlertDialog(
      onDismissRequest = { showInfoDialog = false },
      containerColor = DarkSurfaceElevated,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Shield, contentDescription = null, tint = NeonCyan)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Security & Workflow Details", color = TextPrimary, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "• Strict HTTPS: Enforces encrypted transport and verifies certificates.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Text(
            text = "• No Secret Interception: Never intercepts passwords, session tokens, or private user credentials.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Text(
            text = "• Legitimate Verification: All CAPTCHAs and sponsor checkpoints are executed organically through the browser engine.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Text(
            text = "• Local Storage Only: Retrieved keys are stored strictly in private device preferences.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      },
      confirmButton = {
        NeonButton(
          text = "UNDERSTOOD",
          onClick = { showInfoDialog = false },
          modifier = Modifier.padding(horizontal = 8.dp)
        )
      }
    )
  }
}

@Composable
fun KeyItemCard(
  keyItem: SavedKey,
  onCopy: () -> Unit,
  onDelete: () -> Unit
) {
  val dateFormatted = remember(keyItem.createdAt) {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    formatter.format(Date(keyItem.createdAt))
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, DarkBorderGlow, RoundedCornerShape(12.dp)),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(NeonCyan)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = dateFormatted,
            fontSize = 11.sp,
            color = TextMuted
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete key",
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Key Code Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF090E18))
          .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
          .padding(horizontal = 12.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = keyItem.keyText,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            ),
            color = NeonCyanLight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          IconButton(
            onClick = onCopy,
            modifier = Modifier
              .size(32.dp)
              .testTag("copy_key_item_button")
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Key",
              tint = NeonCyan,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}
