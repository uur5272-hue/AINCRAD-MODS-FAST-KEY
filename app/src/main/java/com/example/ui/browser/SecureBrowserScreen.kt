package com.example.ui.browser

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.WorkflowConstants
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonSecondaryButton
import com.example.ui.theme.AccentError
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderGlow
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SecureBrowserScreen(
  initialUrl: String,
  onClose: () -> Unit,
  onKeySaved: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  var currentUrl by rememberSaveable { mutableStateOf(initialUrl) }
  var webViewTitle by remember { mutableStateOf("Aincrad Secure Portal") }
  var progress by remember { mutableIntStateOf(0) }
  var isLoading by remember { mutableStateOf(true) }
  var canGoBack by remember { mutableStateOf(false) }
  var canGoForward by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var detectedKey by remember { mutableStateOf<String?>(null) }
  var showManualKeyDialog by remember { mutableStateOf(false) }
  var manualKeyInput by remember { mutableStateOf("") }

  var webViewInstance by remember { mutableStateOf<WebView?>(null) }

  // Check which step the user is currently on based on URL
  val isStep2 = currentUrl.contains("aincradmods.com", ignoreCase = true)

  // Handle hardware / gesture Back
  BackHandler {
    if (webViewInstance?.canGoBack() == true) {
      webViewInstance?.goBack()
    } else {
      onClose()
    }
  }

  // Copy helper
  fun copyToClipboard(key: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Aincrad Key", key.trim())
    clipboard.setPrimaryClip(clip)
    onKeySaved(key.trim())
    coroutineScope.launch {
      snackbarHostState.showSnackbar("Key copied to clipboard!")
    }
    Toast.makeText(context, "Key copied to clipboard!", Toast.LENGTH_SHORT).show()
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSurface)
      ) {
        TopAppBar(
          title = {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "SSL Verified",
                  tint = if (WorkflowConstants.isSecureUrl(currentUrl)) NeonCyan else AccentError,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isStep2) "STEP 2: KEY GENERATION" else "STEP 1: VERIFICATION PREP",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isStep2) NeonTeal else NeonCyan,
                  letterSpacing = 0.5.sp
                )
              }
              Text(
                text = WorkflowConstants.extractDomain(currentUrl),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          },
          navigationIcon = {
            IconButton(
              onClick = {
                if (webViewInstance?.canGoBack() == true) {
                  webViewInstance?.goBack()
                } else {
                  onClose()
                }
              },
              modifier = Modifier.testTag("browser_back_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = NeonCyan
              )
            }
          },
          actions = {
            // Forward button
            IconButton(
              onClick = { webViewInstance?.goForward() },
              enabled = canGoForward,
              modifier = Modifier.testTag("browser_forward_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Forward",
                tint = if (canGoForward) NeonCyanLight else TextMuted
              )
            }

            // Reload / Retry button
            IconButton(
              onClick = {
                errorMessage = null
                webViewInstance?.reload()
              },
              modifier = Modifier.testTag("browser_refresh_button")
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reload",
                tint = NeonCyan
              )
            }

            // Open in External Browser
            IconButton(
              onClick = {
                try {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                  context.startActivity(intent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Could not open external browser: ${e.message}", Toast.LENGTH_SHORT).show()
                }
              },
              modifier = Modifier.testTag("browser_open_external_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Open in External Browser",
                tint = TextSecondary
              )
            }

            // Close button
            IconButton(
              onClick = onClose,
              modifier = Modifier.testTag("browser_close_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = TextSecondary
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkSurface,
            titleContentColor = TextPrimary
          )
        )

        // Loading Bar
        if (isLoading && progress < 100) {
          LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier
              .fillMaxWidth()
              .height(3.dp),
            color = NeonCyan,
            trackColor = DarkBackground
          )
        } else {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(DarkBorder)
          )
        }
      }
    },
    bottomBar = {
      // Bottom Step Action Dock
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSurfaceElevated)
          .border(1.dp, DarkBorderGlow, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
          .padding(16.dp)
      ) {
        if (!isStep2) {
          // STEP 1 CONTROLS
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text("1", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Follow site instructions above",
              style = MaterialTheme.typography.bodyMedium,
              color = TextSecondary,
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          NeonButton(
            text = "CONTINUE TO KEY PAGE",
            icon = Icons.Default.Key,
            onClick = {
              errorMessage = null
              val nextUrl = WorkflowConstants.KEY_PAGE_URL
              currentUrl = nextUrl
              webViewInstance?.loadUrl(nextUrl)
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "continue_to_key_page_button"
          )
        } else {
          // STEP 2 CONTROLS
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(NeonTeal.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text("2", color = NeonTeal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (detectedKey != null) "Key ready for retrieval" else "Complete verification to unlock key",
              style = MaterialTheme.typography.bodyMedium,
              color = if (detectedKey != null) NeonCyanLight else TextSecondary,
              fontWeight = if (detectedKey != null) FontWeight.SemiBold else FontWeight.Normal,
              modifier = Modifier.weight(1f)
            )
          }

          // If a key was detected on the page
          if (detectedKey != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0C172B))
                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "DETECTED KEY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 0.5.sp
                  )
                  Text(
                    text = detectedKey!!,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            NeonButton(
              text = if (detectedKey != null) "COPY KEY" else "COPY KEY",
              icon = Icons.Default.ContentCopy,
              onClick = {
                if (detectedKey != null) {
                  copyToClipboard(detectedKey!!)
                } else {
                  // Let user inspect or manually paste/confirm key
                  showManualKeyDialog = true
                }
              },
              modifier = Modifier.weight(1f),
              testTag = "copy_key_button"
            )

            NeonSecondaryButton(
              text = "Inspect",
              icon = Icons.Default.Key,
              onClick = {
                // Trigger JS extraction to find key on page
                val js = """
                  (function() {
                    var bodyText = document.body ? document.body.innerText : '';
                    var keyInput = document.querySelector('input[type="text"], input[readonly], pre, code, .key-box, #key, .key');
                    var found = '';
                    if (keyInput) {
                      found = keyInput.value || keyInput.innerText || '';
                    }
                    if (!found) {
                      var match = bodyText.match(/(?:key|Key|access_key)[\s:=]+([A-Za-z0-9_-]{8,64})/);
                      if (match && match[1]) {
                        found = match[1];
                      }
                    }
                    return found;
                  })();
                """.trimIndent()
                webViewInstance?.evaluateJavascript(js) { result ->
                  val clean = result?.replace("\"", "")?.trim()
                  if (!clean.isNullOrEmpty() && clean != "null") {
                    detectedKey = clean
                    copyToClipboard(clean)
                  } else {
                    showManualKeyDialog = true
                  }
                }
              },
              modifier = Modifier.width(100.dp),
              testTag = "inspect_key_button"
            )
          }
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .background(DarkBackground)
    ) {
      // WebView interop
      AndroidView(
        factory = { ctx ->
          WebView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
              javaScriptEnabled = true
              domStorageEnabled = true
              databaseEnabled = true
              useWideViewPort = true
              loadWithOverviewMode = true
              cacheMode = WebSettings.LOAD_DEFAULT
              mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
              userAgentString = settings.userAgentString + " AincradKeyAssistant/1.0"
              setSupportZoom(true)
              builtInZoomControls = true
              displayZoomControls = false
            }

            // JavaScript Interface for secure notification of detected key
            addJavascriptInterface(object {
              @JavascriptInterface
              fun onKeyFound(key: String) {
                if (key.isNotBlank()) {
                  detectedKey = key.trim()
                }
              }
            }, "AincradAssistantBridge")

            webViewClient = object : WebViewClient() {
              override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val uri = request?.url ?: return false
                val scheme = uri.scheme ?: ""
                val urlString = uri.toString()

                // Security check: Only allow HTTPS connections
                if (!scheme.equals("https", ignoreCase = true)) {
                  errorMessage = "Blocked insecure non-HTTPS connection: $urlString"
                  return true
                }

                currentUrl = urlString
                return false
              }

              override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isLoading = true
                errorMessage = null
                if (url != null) {
                  currentUrl = url
                }
                canGoBack = view?.canGoBack() == true
                canGoForward = view?.canGoForward() == true
              }

              override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoading = false
                if (url != null) {
                  currentUrl = url
                }
                canGoBack = view?.canGoBack() == true
                canGoForward = view?.canGoForward() == true

                // On Key Page, safely scan for key presence
                if (url?.contains("aincradmods.com", ignoreCase = true) == true) {
                  val extractorJs = """
                    (function() {
                      try {
                        var el = document.querySelector('input[readonly], input#key, #generated-key, .key-code, code, pre');
                        if (el && (el.value || el.innerText)) {
                          var val = (el.value || el.innerText).trim();
                          if (val.length >= 6) {
                            AincradAssistantBridge.onKeyFound(val);
                          }
                        }
                      } catch(e) {}
                    })();
                  """.trimIndent()
                  view?.evaluateJavascript(extractorJs, null)
                }
              }

              override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                  val desc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    error?.description?.toString() ?: "Network error"
                  } else {
                    "Failed to load page"
                  }
                  errorMessage = "Page connection failed ($desc). Please check your internet connection."
                }
              }

              override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                // Strict SSL protection: do not ignore SSL errors
                handler?.cancel()
                errorMessage = "SSL Certificate Verification Failed. Insecure connection blocked."
              }
            }

            webChromeClient = object : WebChromeClient() {
              override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress = newProgress
                if (newProgress == 100) {
                  isLoading = false
                }
              }

              override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (!title.isNullOrBlank()) {
                  webViewTitle = title
                }
              }
            }

            loadUrl(initialUrl)
            webViewInstance = this
          }
        },
        update = { webView ->
          // Webview updates handled via methods
        },
        modifier = Modifier.fillMaxSize()
      )

      // Network / SSL Error Overlay
      if (errorMessage != null) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.95f))
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, AccentError.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
          ) {
            Column(
              modifier = Modifier.padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = AccentError,
                modifier = Modifier.size(44.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Connection Notice",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = errorMessage ?: "Unable to load page.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp)
              )
              Spacer(modifier = Modifier.height(18.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                NeonSecondaryButton(
                  text = "Retry",
                  icon = Icons.Default.Refresh,
                  onClick = {
                    errorMessage = null
                    webViewInstance?.reload()
                  },
                  modifier = Modifier.weight(1f)
                )

                NeonButton(
                  text = "Open in Chrome",
                  icon = Icons.AutoMirrored.Filled.OpenInNew,
                  onClick = {
                    try {
                      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                      context.startActivity(intent)
                    } catch (e: Exception) {
                      Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                    }
                  },
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }
      }
    }
  }

  // Manual Key Copy / Entry Dialog
  if (showManualKeyDialog) {
    AlertDialog(
      onDismissRequest = { showManualKeyDialog = false },
      containerColor = DarkSurfaceElevated,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Key, contentDescription = null, tint = NeonCyan)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Copy or Enter Key", color = TextPrimary, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column {
          Text(
            text = "If the key has been unlocked on the web page, paste or enter it below to copy and store it securely.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(14.dp))
          OutlinedTextField(
            value = manualKeyInput,
            onValueChange = { manualKeyInput = it },
            placeholder = { Text("e.g. AINCRAD-XXXX-YYYY", color = TextMuted) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("manual_key_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = NeonCyan,
              unfocusedBorderColor = DarkBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              cursorColor = NeonCyan
            )
          )
        }
      },
      confirmButton = {
        NeonButton(
          text = "SAVE & COPY",
          onClick = {
            if (manualKeyInput.isNotBlank()) {
              copyToClipboard(manualKeyInput)
              showManualKeyDialog = false
              manualKeyInput = ""
            } else {
              Toast.makeText(context, "Please enter a key", Toast.LENGTH_SHORT).show()
            }
          },
          modifier = Modifier.padding(horizontal = 8.dp),
          testTag = "save_and_copy_button"
        )
      },
      dismissButton = {
        TextButton(onClick = { showManualKeyDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}
