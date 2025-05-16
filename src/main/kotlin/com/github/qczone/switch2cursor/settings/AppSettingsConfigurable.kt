package com.github.qczone.switch2cursor.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.JComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder

class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    override fun getDisplayName(): String = "Open In Cursor"

    override fun createComponent(): JComponent {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.panel
    }

    private fun isActuallyModified(): Boolean {
        val settings = AppSettingsState.getInstance()
        // 获取界面上当前选中的预设配置
        val selectedConfig = settings.cursorConfigs[mySettingsComponent!!.selectedConfigIndex]
        
        // 检查用户是否修改了当前选中的预设配置的值
        return mySettingsComponent!!.cursorPath != selectedConfig.path ||
               mySettingsComponent!!.fileProtocol != selectedConfig.protocol
    }

    override fun isModified(): Boolean {
        val settings = AppSettingsState.getInstance()
        return mySettingsComponent!!.selectedConfigIndex != settings.selectedConfigIndex ||
               mySettingsComponent!!.cursorPath != settings.cursorPath ||
               mySettingsComponent!!.fileProtocol != settings.fileProtocol
    }

    override fun apply() {
        val settings = AppSettingsState.getInstance()
        
        // 只有当实际配置发生变化时才显示警告
        if (isActuallyModified()) {
            val result = com.intellij.openapi.ui.Messages.showYesNoDialog(
                """
                警告：修改配置可能会导致插件无法正常工作。
                除非您完全理解这些配置的含义，否则建议使用默认配置。
                
                是否确定要保存修改？
                """.trimIndent(),
                "配置修改确认",
                "确定保存",
                "取消",
                com.intellij.openapi.ui.Messages.getWarningIcon()
            )

            if (result != com.intellij.openapi.ui.Messages.YES) {
                reset()
                return
            }
        }
        
        // 保存配置
        settings.selectedConfigIndex = mySettingsComponent!!.selectedConfigIndex
        settings.cursorPath = mySettingsComponent!!.cursorPath
        settings.fileProtocol = mySettingsComponent!!.fileProtocol
    }

    override fun reset() {
        val settings = AppSettingsState.getInstance()
        mySettingsComponent!!.selectedConfigIndex = settings.selectedConfigIndex
        mySettingsComponent!!.cursorPath = settings.cursorPath
        mySettingsComponent!!.fileProtocol = settings.fileProtocol
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}

class AppSettingsComponent {
    val panel: JPanel
    private val configComboBox: JComboBox<String>
    private val cursorPathText = JTextField()
    private val fileProtocolText = JTextField()

    init {
        val settings = AppSettingsState.getInstance()
        configComboBox = JComboBox(settings.cursorConfigs.map { it.name }.toTypedArray())
        configComboBox.addActionListener {
            val selectedConfig = settings.cursorConfigs[selectedConfigIndex]
            cursorPathText.text = selectedConfig.path
            fileProtocolText.text = selectedConfig.protocol
        }

        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("预设配置: "), configComboBox, 1, false)
            .addLabeledComponent(JBLabel("Cursor 路径: "), cursorPathText, 1, false)
            .addLabeledComponent(JBLabel("文件协议: "), fileProtocolText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    var selectedConfigIndex: Int
        get() = configComboBox.selectedIndex
        set(value) {
            configComboBox.selectedIndex = value
        }

    var cursorPath: String
        get() = cursorPathText.text
        set(value) {
            cursorPathText.text = value
        }

    var fileProtocol: String
        get() = fileProtocolText.text
        set(value) {
            fileProtocolText.text = value
        }
}