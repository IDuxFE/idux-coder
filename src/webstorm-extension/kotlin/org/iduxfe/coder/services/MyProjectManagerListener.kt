package org.iduxfe.coder.services

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import org.iduxfe.coder.services.MyProjectService
import com.intellij.javascript.nodejs.library.NodeModulesDirectoryManager

internal class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<MyProjectService>()
    }
}
