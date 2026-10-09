package com.example.data

data class SavedKey(
  val id: String = java.util.UUID.randomUUID().toString(),
  val keyText: String,
  val createdAt: Long = System.currentTimeMillis(),
  val source: String = WorkflowConstants.KEY_PAGE_URL,
  val note: String = "Retrieved via Aincrad Assistant"
)
