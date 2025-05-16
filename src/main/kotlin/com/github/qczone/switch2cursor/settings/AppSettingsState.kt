package com.github.qczone.switch2cursor.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Property
import com.intellij.util.xmlb.annotations.XCollection

@State(
    name = "com.github.qczone.switch2cursor.settings.AppSettingsState",
    storages = [Storage("Switch2CursorSettings.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState> {
    @Property
    @XCollection(style = XCollection.Style.v2)
    var cursorConfigs: MutableList<CursorConfig> = defaultConfigs.toMutableList()
    
    var selectedConfigIndex: Int = 0
    var customPath: String = "cursor"
    var customProtocol: String = "cursor://"

    // 当前使用的配置
    var cursorPath: String
        get() = if (selectedConfigIndex < cursorConfigs.size - 1) {
            cursorConfigs[selectedConfigIndex].path
        } else {
            customPath
        }
        set(value) {
            if (selectedConfigIndex == cursorConfigs.size - 1) {
                customPath = value
            }
        }

    var fileProtocol: String
        get() = if (selectedConfigIndex < cursorConfigs.size - 1) {
            cursorConfigs[selectedConfigIndex].protocol
        } else {
            customProtocol
        }
        set(value) {
            if (selectedConfigIndex == cursorConfigs.size - 1) {
                customProtocol = value
            }
        }

    override fun getState(): AppSettingsState = this

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
        if (cursorConfigs.isEmpty()) {
            cursorConfigs = defaultConfigs.toMutableList()
        }
    }

    companion object {
        fun getInstance(): AppSettingsState = ApplicationManager.getApplication().getService(AppSettingsState::class.java)

        data class CursorConfig(
            @Property var name: String = "",
            @Property var path: String = "",
            @Property var protocol: String = ""
        )

        private val defaultConfigs = listOf(
            CursorConfig("Cursor", "cursor", "cursor://"),
            CursorConfig("VS Code", "/Applications/Visual Studio Code.app", "vscode://"),
        )
    }
} 