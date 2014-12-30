package com.github.tagc.semver

import java.util.regex.Pattern

import org.ajoberstar.grgit.Grgit
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger

/**
 * An {@link org.gradle.api.Plugin} class that handles the application of semantic
 * versioning logic to an {@link org.gradle.api.Project}
 * 
 * @author davidfallah
 * @since v0.1.0
 */
class SemVerPlugin implements Plugin<Project> {
    private static final String MASTER_BRANCH = "master"
    private static final Pattern VERSION_PATTERN = ~/(\d+)\.(\d+).(\d+)/
    
    private static final String EXTENSION_NAME = 'semver'
    private Grgit repo
    private Logger logger

    @Override
    public void apply(Project project) {
        logger = project.logger
        project.extensions.create(EXTENSION_NAME, SemVerPluginExtension)
        
        project.afterEvaluate {
            setVersionProjectNumber(project)
        }
    }

    private void setVersionProjectNumber(Project project) {
        try {
            this.repo = Grgit.open(project.file("$project.projectDir"))
        } catch(RepositoryNotFoundException e) {
            throw new GradleException("No Git repository can be found for this project")
        }

        def rawVersion = readRawVersion(project)

        if (isOnMasterBranch()) {
            project.version = rawVersion.toRelease()
        } else {
            project.version = rawVersion.toDevelop()
        }

        logger.info "Set project version to $project.version"
    }

    private Version readRawVersion(Project project) {
        def extension = project.extensions.findByName(EXTENSION_NAME)
        println "Extension=$extension"
        final String versionFilePath = extension.versionFilePath
        
        if (!versionFilePath) {
            throw new GradleException("Version file has not been specified")
        }    
        
        def versionFile = new File(versionFilePath)
        if (!versionFile.exists()) {
            throw new GradleException("Missing version file: $versionFile.canonicalPath")
        }

        Version.Parser.getInstance().parse(versionFile.text)
    }

    private boolean isOnMasterBranch() {
        def currentBranch = getCurrentBranch()
        logger.info "Current Git branch: $currentBranch"
        currentBranch == MASTER_BRANCH
    }

    private String getCurrentBranch() {
        repo.branch.current.name
    }
}
