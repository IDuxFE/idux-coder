// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.iduxfe.coder.ui;

import org.iduxfe.coder.services.initAPIMap
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class AppSettingsConfigurable : Configurable {
    private var appSettingsComponent: AppSettingsComponent? = null

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP
    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "iDux Coder"
    }

    override fun createComponent(): JComponent {
        appSettingsComponent = AppSettingsComponent()
        return appSettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings: AppSettingsState = AppSettingsState.instance
        var modified = appSettingsComponent!!.baseURL != settings.baseURL
        modified = modified or (appSettingsComponent!!.customAPIs != settings.customAPIsPath)
        // 修改baseURL和自定义api后需重建文档
        if (modified) {
            initAPIMap()
        }
        modified = modified or (appSettingsComponent!!.componentPrefix != settings.componentPrefix)
        modified = modified or (appSettingsComponent!!.language != settings.language)
        return modified
    }

    override fun apply() {
        val settings: AppSettingsState = AppSettingsState.instance
        settings.baseURL = appSettingsComponent!!.baseURL
        settings.customAPIsPath = appSettingsComponent!!.customAPIs
        settings.componentPrefix = appSettingsComponent!!.componentPrefix
        settings.language = appSettingsComponent!!.language

    }

    override fun reset() {
        val settings: AppSettingsState = AppSettingsState.instance
        appSettingsComponent!!.baseURL = settings.baseURL
        appSettingsComponent!!.language = settings.language
        appSettingsComponent!!.componentPrefix = settings.componentPrefix
        appSettingsComponent!!.customAPIs = settings.customAPIsPath
    }

    override fun disposeUIResources() {
        appSettingsComponent = null
    }
}