package org.iduxfe.coder.services

import com.intellij.javascript.nodejs.library.NodeModulesDirectoryManager
import org.iduxfe.coder.ui.AppSettingsState
import org.iduxfe.coder.utils.markdownToHTML
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.File
import kotlin.collections.HashMap

val apis: MutableMap<String, String> = HashMap()

private val iduxDirs = arrayOf(
  "@idux/cdk",
  "@idux/components",
  "@idux/pro",
  "@idux-vue2/cdk",
  "@idux-vue2/components",
  "@idux-vue2/pro"
).map { "$it/api.json" }

private val apisFiles = arrayListOf<String>()
private val settingState = AppSettingsState.instance

private fun scanJSONs() {
  currProject?.let { projectModule ->
    val nodeModulesInstance = NodeModulesDirectoryManager.getInstance(projectModule)
    nodeModulesInstance.nodeModulesDirectories.forEach { module ->
      iduxDirs.forEach { iduxLib ->
        val r = module.nodeModulesDir.findFileByRelativePath(iduxLib)
        if (r != null) {
          apisFiles.add(r.path)
        }
      }
    }

    settingState.customAPIsPath?.let {
      apisFiles.add(it)
    }
  }
}

fun initAPIMap() {
  scanJSONs()

  if (apisFiles.isEmpty()) {
    return
  }

  apisFiles.forEach {
    val text = File(it).readText(Charsets.UTF_8)
    flatAPIs(Json.parseToJsonElement(text) as JsonObject)
  }
}


private fun flatAPIs(json: JsonObject) {
  val settingState = AppSettingsState.instance
  try {
    json.entries.forEach { comp ->
      (comp.value as JsonObject).entries.forEach { lang ->
        (lang.value as JsonObject).entries.forEach { type ->
          when (type.key) {
            "raw" -> {
              apis["${comp.key}.${lang.key}.${type.key}"] =
                markdownToHTML((type.value as JsonPrimitive).content, settingState.baseURL)
            }

            "path" -> {
            }

            else -> {
              (type.value as JsonObject).entries.forEach {
                val compWithLang = lang.value as JsonObject
                val path =
                  if (compWithLang["path"] == null) "" else "/${(compWithLang["path"] as JsonPrimitive).content}"

                apis["${comp.key}.${lang.key}.${type.key}.${it.key}"] = markdownToHTML(
                  dataMarkdown(it.value as JsonObject),
                  "${settingState.baseURL}${path}"
                )
              }
            }
          }
        }
      }
    }
  } catch (e: Exception) {
    println(e)
  }
}


private fun dataWrapper(data: String, tag: String): String = if (data != "-") "*@${tag}*: ${data}\n\n" else "";

private fun dataMarkdown(data: JsonObject): String {

  return """
${(data["description"] as JsonPrimitive).content}
---
${dataWrapper((data["type"] as JsonPrimitive).content, "type")}
${dataWrapper((data["default"] as JsonPrimitive).content, "default")}
${dataWrapper((data["globalConfig"] as JsonPrimitive).content, "globalConfig")}
"""
}
