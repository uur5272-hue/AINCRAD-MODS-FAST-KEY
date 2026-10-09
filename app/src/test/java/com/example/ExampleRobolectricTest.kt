package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.KeyVaultRepository
import com.example.data.WorkflowConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Aincrad Key Assistant", appName)
  }

  @Test
  fun `workflow URLs are secure and valid`() {
    assertTrue(WorkflowConstants.isSecureUrl(WorkflowConstants.INITIAL_WEBSITE_URL))
    assertTrue(WorkflowConstants.isSecureUrl(WorkflowConstants.KEY_PAGE_URL))
    assertEquals("rodaemotor.com", WorkflowConstants.extractDomain(WorkflowConstants.INITIAL_WEBSITE_URL))
    assertEquals("aincradmods.com", WorkflowConstants.extractDomain(WorkflowConstants.KEY_PAGE_URL))
  }

  @Test
  fun `key vault repository saves and retrieves key`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = KeyVaultRepository(context)
    repo.clearAll()

    val added = repo.addKey("TEST-AINCRAD-KEY-12345")
    assertNotNull(added.id)
    assertEquals("TEST-AINCRAD-KEY-12345", added.keyText)
    assertEquals(1, repo.savedKeys.value.size)

    repo.clearAll()
    assertEquals(0, repo.savedKeys.value.size)
  }
}
