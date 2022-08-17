package org.iduxfe.coder.services

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

var currProject: Project? = null

internal class MyProjectManagerListener : ProjectManagerListener {

  override fun projectOpened(project: Project) {
    project.service<MyProjectService>()
  }
}

class MyProjectService(project: Project) {

  init {
    currProject = project
    initAPIMap()
  }
}

