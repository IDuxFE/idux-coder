package org.iduxfe.coder.services

import org.iduxfe.coder.ui.AppSettingsState
import org.iduxfe.coder.utils.markdownToHTML
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.File
import kotlin.collections.HashMap

val apis: MutableMap<String, String> = HashMap()

val apisFiles = arrayListOf<String>()

fun initAPIMap() {
    if (apisFiles.isEmpty()) {
        return
    }

//  val fileLoc = "D:\\api.json"
    apisFiles.forEach {
        val text = File(it).readText(Charsets.UTF_8)
        flatAPIs(Json.parseToJsonElement(text) as JsonObject)
    }
    mergeCustomAPIs()
}

private fun mergeCustomAPIs() {
    val settingState = AppSettingsState.instance
    if (settingState.customAPIsPath != null) {
        flatAPIs(Json.parseToJsonElement(File(settingState.customAPIsPath!!).readText(Charsets.UTF_8)) as JsonObject)
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