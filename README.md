Semver
======
[![Build Status](https://travis-ci.org/TAGC/Semver.svg)](https://travis-ci.org/TAGC/Semver) [ ![Download](https://api.bintray.com/packages/tagc/gradle-plugins/gradle-semver-plugin/images/download.svg) ](https://bintray.com/tagc/gradle-plugins/gradle-semver-plugin/_latestVersion)

A simple semantic versioning Gradle plugin for Git-controlled projects.

This plugin has been primarily developed just so that I get experience with developing Gradle plugins.

**Note!** This plugin has been altered significantly between versions 0.2 and 0.3, so a lot of this documentation is out of date. I will try to update the documentation as soon as possible.

### Setup

To configure your project to use this plugin, add the following to your Gradle build script:

```
apply plugin: 'semver'

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        // Replace <version> with preferred plugin version
        classpath 'com.github.tagc:semver-plugin:<version>'
    }
}

semver {
    versionFilePath = 'version.properties' // Or whatever other file stores version information
}
```

The `versionFilePath` property must point to a file that stores the 'raw' version string for your project in the form `<major>.<minor>.<patch>`, e.g. `version='1.2.3'` or simply `1.2.3`.

### Description

This plugin is designed to be applied to projects that are using Gradle as a build automation tool and Git for source code management. The plugin will attempt to extract a 'raw' semantic version from a specified file (e.g. `1.2.3`) and then perform either of two actions:
  - if the plugin detects that you are currently on the **master** Git branch, it will save that version string as-is to your project's `project.version` property.
  - if the plugin detects that you are currently on any other Git branch, it will append `-SNAPSHOT` to the version string and apply the result to your project's `project.version` property (e.g. `1.2.3-SNAPSHOT`).

This behaviour is performed during the execution stage of the build lifecycle through the `:setProjectVersionNumber` task. The plugin will set `:compileJava` and `:compileGroovy` to depend on this task if they exist.

##### Recommended Usage

The following workflow is recommended for use with this plugin:

  1. Download and install [bumpversion](https://pypi.python.org/pypi/bumpversion/0.5.0). This can be done from terminal via the Python package installer (pip).
  2. Add a `.bumpversion.cfg` to your project directory, such as the following:
  ```
  [bumpversion]
  current_version = 0.1.0
  commit = False
  tag = False
  
  [bumpversion:file:version.properties]
  ```
  3. Add a `version.properties` file to your project directory, such as the following:

  ```
  version='0.1.0'
  ```
  4. Adopt the [Git Flow workflow strategy](http://nvie.com/posts/a-successful-git-branching-model/) or something similar, where release versions are committed on **master** and snapshot versions are pulled from other branches. The [Git Flow Github project](https://github.com/nvie/gitflow) can facilitate this workflow strategy by providing Git flow commands to the terminal.

You can use the command `bumpversion major|minor|patch` to bump your project version. This will update the version string stored in `version.properties` (or any other file you prefer), which can then be accessed and processed by the SemVer plugin. If you're on any branch but **master**, this plugin will append `-SNAPSHOT` to that string and save it to your project's `project.version` property, as described above.

### Acknowledgements

The development of this project has been largely based on work done by:
 - [Andrew Oberstar](https://github.com/ajoberstar) for his [Gradle-Git](https://github.com/ajoberstar/gradle-git) project, which this plugin relies on to query the current Git branch.
 - [Benjamin Muschko](http://www.gradleware.com/team/benjamin-muschko/) for his book, *Gradle in Action*, from which I've been trying to learn Gradle.
