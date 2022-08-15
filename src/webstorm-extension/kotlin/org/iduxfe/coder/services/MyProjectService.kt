package org.iduxfe.coder.services

import com.intellij.javascript.nodejs.library.NodeModulesDirectoryManager
import com.intellij.openapi.project.Project
import com.jetbrains.rd.util.insert

class MyProjectService(project: Project) {
    val iduxDirs = arrayOf(
        "@idux/cdk",
        "@idux/components",
        "@idux/pro",
        "@idux-vue2/cdk",
        "@idux-vue2/components",
        "@idux-vue2/pro"
    ).map { "$it/api.json" }

    init {
        project.let {
            val nodeModulesInstance = NodeModulesDirectoryManager.getInstance(it)
            nodeModulesInstance.nodeModulesDirectories.forEach { module ->
                iduxDirs.forEach { iduxLib ->
                    val r = module.nodeModulesDir.findFileByRelativePath(iduxLib)
                    if (r != null) {
                        apisFiles.add(r.path)
                    }
                }
            }
            initAPIMap()
        }
    }
}

