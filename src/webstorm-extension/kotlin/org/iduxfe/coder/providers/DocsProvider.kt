package org.iduxfe.coder.providers

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlElementType
import com.intellij.psi.xml.XmlTokenType
import org.jetbrains.vuejs.codeInsight.documentation.VueItemDocumentation
import org.iduxfe.coder.services.apis
import org.iduxfe.coder.ui.AppSettingsState
import org.iduxfe.coder.utils.camelCase
import org.iduxfe.coder.utils.pascalCase
import java.util.regex.Pattern

/**
 * @author: tuchg
 * @date: 2022/8/13 17:28
 * @description:
 */
class DocsProvider : DocumentationProvider {
    private val settingsState = AppSettingsState.instance
    private val lang = settingsState.language

    private val purifyProp = Pattern.compile("(.*)([:@#])(\\S+)")
    private val purifyPrefix = Pattern.compile(settingsState.componentPrefix, Pattern.CASE_INSENSITIVE)
    private var lastElName: String? = null

    override fun generateDoc(el: PsiElement?, oriEl: PsiElement?): String? {
        if (el == null || oriEl == null) {
            return null
        }
//        VueItemDocumentation
        if (oriEl.elementType == XmlElementType.XML_NAME) {
            var tagName = oriEl.text
            var propType = "raw"
            var propName = ""

            if (tagName != null) {
                if (oriEl.nextSibling.elementType == XmlTokenType.XML_EQ || oriEl.parent.elementType == XmlElementType.XML_ATTRIBUTE) {
                    val wrapperEl = oriEl.parent?.context?.firstChild?.nextSibling
                    tagName = if (wrapperEl?.text == "template") {
                        // slots
                        propName = tagName
                        wrapperEl.context?.context?.firstChild!!.nextSibling.text
                    } else
                        wrapperEl?.text

                    propType = if (propName.indexOf('#') >= 0) "slots" else "props"

                    // convert any-case to pascal-case
                    propName = purifyProp.matcher(oriEl.text).replaceAll {
                        if (it.group(1).isEmpty())
                            camelCase(it.group(3))
                        else
                            "${it.group(1)}${it.group(2)}${camelCase(it.group(3))}"
                    }
                }

                // convert any-case to pascal-case
                tagName = pascalCase(purifyPrefix.matcher(tagName).replaceFirst(""))

                val key = "${tagName}.${lang}.${propType}${if (propName.isNotEmpty()) ".${propName}" else ""}"
                return apis[key]
            }
        }
        if (oriEl.elementType?.index == 400.toShort() && lastElName !== null) {
            val key = "${pascalCase(purifyPrefix.matcher(lastElName!!).replaceFirst(""))}.${lang}.raw"
            lastElName = null
            return apis[key]
        }

        return null
    }

    /**
     * Ctrl-Q 时触发
     */
    override fun getDocumentationElementForLookupItem(
        psiManager: PsiManager?,
        `object`: Any?,
        element: PsiElement?
    ): PsiElement? {
        if (element.elementType == XmlElementType.HTML_TAG) {
            `object` as Pair<*, *>
            val first = `object`.first
            if (first is VueItemDocumentation) {
                this.lastElName = first.defaultName
            }
        }
        return super.getDocumentationElementForLookupItem(psiManager, `object`, element)
    }
}