package com.example.data

object WorkflowConstants {
  const val INITIAL_WEBSITE_URL =
    "https://rodaemotor.com/toyota-lanca-gr-kart-para-facilitar-a-entrada-de-familias-no-automobilismo/"
  const val KEY_PAGE_URL = "https://aincradmods.com/getkey"

  const val INITIAL_DOMAIN = "rodaemotor.com"
  const val KEY_PAGE_DOMAIN = "aincradmods.com"

  /**
   * Validates if a given URL is a secure HTTPS URL.
   */
  fun isSecureUrl(url: String): Boolean {
    return url.startsWith("https://", ignoreCase = true)
  }

  /**
   * Clean domain extractor for UI display.
   */
  fun extractDomain(url: String): String {
    return try {
      val uri = android.net.Uri.parse(url)
      uri.host ?: url
    } catch (_: Exception) {
      url
    }
  }
}
