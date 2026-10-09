package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class KeyVaultRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("aincrad_key_vault_prefs", Context.MODE_PRIVATE)

  private val _savedKeys = MutableStateFlow<List<SavedKey>>(emptyList())
  val savedKeys: StateFlow<List<SavedKey>> = _savedKeys.asStateFlow()

  init {
    loadKeys()
  }

  private fun loadKeys() {
    val jsonString = prefs.getString("saved_keys_json", null) ?: "[]"
    val list = mutableListOf<SavedKey>()
    try {
      val array = JSONArray(jsonString)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          SavedKey(
            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
            keyText = obj.optString("keyText", ""),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            source = obj.optString("source", WorkflowConstants.KEY_PAGE_URL),
            note = obj.optString("note", "")
          )
        )
      }
    } catch (_: Exception) {
      // Fallback
    }
    _savedKeys.value = list
  }

  private fun saveToDisk(keys: List<SavedKey>) {
    val array = JSONArray()
    for (k in keys) {
      val obj = JSONObject().apply {
        put("id", k.id)
        put("keyText", k.keyText)
        put("createdAt", k.createdAt)
        put("source", k.source)
        put("note", k.note)
      }
      array.put(obj)
    }
    prefs.edit().putString("saved_keys_json", array.toString()).apply()
  }

  fun addKey(keyText: String, source: String = WorkflowConstants.KEY_PAGE_URL, note: String = ""): SavedKey {
    val trimmed = keyText.trim()
    val existing = _savedKeys.value.find { it.keyText.equals(trimmed, ignoreCase = true) }
    if (existing != null) {
      // Move to top with updated timestamp
      val updated = existing.copy(createdAt = System.currentTimeMillis())
      val newList = listOf(updated) + _savedKeys.value.filter { it.id != existing.id }
      _savedKeys.value = newList
      saveToDisk(newList)
      return updated
    }

    val newKey = SavedKey(
      keyText = trimmed,
      createdAt = System.currentTimeMillis(),
      source = source,
      note = note.ifBlank { "Retrieved Key" }
    )
    val newList = listOf(newKey) + _savedKeys.value
    _savedKeys.value = newList
    saveToDisk(newList)
    return newKey
  }

  fun deleteKey(id: String) {
    val newList = _savedKeys.value.filter { it.id != id }
    _savedKeys.value = newList
    saveToDisk(newList)
  }

  fun clearAll() {
    _savedKeys.value = emptyList()
    prefs.edit().remove("saved_keys_json").apply()
  }
}
