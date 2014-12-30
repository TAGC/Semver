package com.github.tagc.semver

import groovy.transform.PackageScope

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
    @PackageScope static final String EXTENSION_NAME = 'semver'

    private static final String MASTER_BRANCH = "master"
    private static final Pattern VERSION_PATTERN = ~/(\d+)\.(\d+).(\d+)/

    private Grgit repo
    private Logger logger

    @Override
    public void apply(Project project) {
        if (!project) {
            throw new GradleException("Plugin cannot be applied to null project")    
        }
        
        logger = project.logger
        project.extensions.create(EXTENSION_NAME, SemVerPluginExtension)

        project.afterEvaluate { setVersionProjectNumber(project) }
    }

    private void setVersionProjectNumber(Project project) {
        assert project : "Null project is illegal"
        
        try {
            this.repo = Grgit.open(project.file("$project.projectDir"))
        } catch(RepositoryNotFoundException e) {
            throw new GradleException("No Git repository can be found for this project")
        }

        def rawVersion = readRawVersion(project)
        
        def extension = project.extensions.findByName(EXTENSION_NAME)
        Version.Category snapshotBump = extension.snapshotBump

        if (isOnMasterBranch()) {
            project.version = rawVersion.toRelease()
        } else {
            project.version = getAppropriateSnapshotVersion(project, rawVersion, snapshotBump)
        }

        logger.info "Set project version to $project.version"
    }

    private Version readRawVersion(Project project) {
        assert project : "Null project is illegal"
        
        def extension = project.extensions.findByName(EXTENSION_NAME)
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

    private Version getAppropriateSnapshotVersion(Project project, Version currVersion, Version.Category snapshotBump) {
        assert currVersion : "Null currVersion is illegal"
        assert project : "Null project is illegal"
        switch(snapshotBump) {
            case Version.Category.PATCH:
                return currVersion.bumpPatch().toDevelop()
            case Version.Category.MINOR:
                return currVersion.bumpMinor().toDevelop()
            case Version.Category.MAJOR:
                return currVersion.bumpMajor().toDevelop()
            default:
                assert false : "Invalid version category provided: $snapshotBump"
        }
    }

    private boolean isOnMasterBranch() {
        def currentBranch = getCurrentBranch()
        logger.info "Current Git branch: $currentBranch"
        currentBranch == MASTER_BRANCH
    }

    private String getCurrentBranch() {
        assert repo : "Repo does not exist"
        repo.branch.current.name
    }
}
