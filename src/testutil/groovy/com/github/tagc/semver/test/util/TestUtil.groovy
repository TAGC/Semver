package com.github.tagc.semver.test.util

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.exception.GrgitException
import org.gradle.api.Plugin
import org.gradle.api.Project

import com.github.tagc.semver.Version

/**
 * Static utility class for test specifications.
 *
 * @author davidfallah
 * @since v0.5.0
 */
public class TestUtil {

    private TestUtil() {
        assert false : "Should not be instantiable"
    }

    static URL getVersionFileAsResource(String versionFilePath) {
        Thread.currentThread().contextClassLoader.getResource(versionFilePath)
    }

    static void evaluateProjectForReleaseTests(Plugin plugin, Project project, URL url) {
        initialiseGitDirectory(project)
        plugin.apply(project)
        project.semver.versionFilePath = url.path
        project.evaluate()
    }

    static void evaluateProjectForSnapshotTests(Plugin plugin, Project project, URL url, Version.Category bump) {
        initialiseGitDirectory(project)
        checkoutBranch(project, 'develop')
        plugin.apply(project)
        project.semver.versionFilePath = url.path
        project.semver.snapshotBump = bump
        project.evaluate()
    }

    static void initialiseGitDirectory(Project project) {
        Grgit.init(dir:project.projectDir)
    }

    static void checkoutBranch(Project project, String branch) {
        Grgit grgit = Grgit.open(project.projectDir)
        try {
            grgit.checkout(branch:branch, createBranch:true)
        } catch (GrgitException e) {
            grgit.add(patterns:['.'], update:true)
            grgit.commit(message:'Initial commit')
            grgit.checkout(branch:branch, createBranch:true)
        }
    }

    static boolean cleanupGitDirectory(Project project) {
        new File("$project.projectDir/.git").deleteDir()
    }
}

