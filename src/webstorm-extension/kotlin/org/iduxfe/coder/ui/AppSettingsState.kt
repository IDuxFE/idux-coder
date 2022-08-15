package org.iduxfe.coder.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

/**
 * @author: tuchg
 * @date: 2022/8/13 17:28
 * @description:
 */
@State(name = "idux-coder.AppSettingsState", storages = [Storage("iDuxCoderPlugin.xml")])
class AppSettingsState : PersistentStateComponent<AppSettingsState> {
    var componentPrefix = "Ix"
    var language = "zh"
    var baseURL = "https://idux.site"
    var customAPIsPath: String? = null
    var packageLocation = ""

    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: AppSettingsState
            get() = ApplicationManager.getApplication().getService(AppSettingsState::class.java)
    }
}