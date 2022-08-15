package org.iduxfe.coder.utils

import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet

private val options: MutableDataSet = MutableDataSet().set(
    Parser.EXTENSIONS, listOf(
        TablesExtension.create()
    )
) // set GitHub table parsing options
    .set(TablesExtension.WITH_CAPTION, false)
    .set(TablesExtension.COLUMN_SPANS, false)
    .set(TablesExtension.MIN_HEADER_ROWS, 1)
    .set(TablesExtension.MAX_HEADER_ROWS, 1)
    .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
    .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
    .set(
        TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH,
        true
    )
    .set(Parser.LISTS_ORDERED_LIST_MANUAL_START, true)

val parser: Parser = Parser.builder(options).build()
val renderer: HtmlRenderer = HtmlRenderer.builder(options).build()

fun markdownToHTML(text: String, baseUrl: String?): String {
    val src = text.replace(Regex("\\\\"), "").replace("$(link-external)", "↗")
    val document: Node = parser.parse(src)

    document.children.filter { it.hasChildren() }.forEach { node ->
        node.children.filterIsInstance<Link>().forEach {
            it.url = it.url.prefixWith(baseUrl)
        }
    }
    return renderer.render(document)
}