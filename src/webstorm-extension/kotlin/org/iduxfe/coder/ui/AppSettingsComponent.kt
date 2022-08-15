package org.iduxfe.coder.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel


class AppSettingsComponent {
    val panel: JPanel
    private val componentPrefixComp = JBTextField()
    private val baseURLComp = JBTextField()
    private val languageComp = ComboBox(arrayOf("zh", "en"))
    private val singleFileDescriptor: FileChooserDescriptor =
        FileChooserDescriptorFactory.createSingleFileDescriptor("json")

    private val customAPIsComp = TextFieldWithBrowseButton(
        FileChooserFactory.getInstance().createFileTextField(
            singleFileDescriptor, null
        ).field
    )

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Set the language for iDux docs"), languageComp, true)
            .addLabeledComponent(JBLabel("Prefix of component names:"), componentPrefixComp, 1, true)
            .addLabeledComponent(JBLabel("Set the website address for iDux docs:"), baseURLComp, 1, true)
            .addLabeledComponent(
                JBLabel("Set the custom APIs JSON file path for iDux docs:"), customAPIsComp, 1, true
            ).addComponentFillVertically(JPanel(), 0).panel

        customAPIsComp.addBrowseFolderListener(object : TextBrowseFolderListener(singleFileDescriptor) {
            override fun chosenFileToResultingText(chosenFile: VirtualFile): String {
                return chosenFile.path
            }
        })
    }

    var componentPrefix: String
        get() = componentPrefixComp.text
        set(newPrefix) {
            componentPrefixComp.text = newPrefix
        }
    var baseURL: String
        get() = baseURLComp.text
        set(value) {
            baseURLComp.text = value
        }
    var language: String
        get() = languageComp.selectedItem as String
        set(value) {
            languageComp.item = value
        }
    var customAPIs: String?
        get() = customAPIsComp.text
        set(value) {
            customAPIsComp.text = value ?: ""
        }
}