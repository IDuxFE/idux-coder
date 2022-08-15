package org.iduxfe.coder.utils

import cn.hutool.core.text.NamingCase
import cn.hutool.core.util.StrUtil

/**
 * convert any-case to camel-case
 *
 */
fun camelCase(name: String): String = NamingCase.toCamelCase(name, '-')

/**
 * convert any-case to pascal-case
 */
fun pascalCase(name: String): String = StrUtil.upperFirst(camelCase(name))