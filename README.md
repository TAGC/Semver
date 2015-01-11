<img src="https://docs.google.com/drawings/d/1MMJypd9RZKUI0X8dnWBfSfQmg93O2Xkb9NRDN3xtaX8/pub?w=250&amp;h=250">

A simple semantic versioning Gradle plugin for Git-controlled projects. You can use this plugin to help Gradle keep track of which version to consider the current state of your project as. This plugin has been primarily developed just so that I get experience with developing Gradle plugins.

As of v0.3.0, this plugin now depends on itself for its own version numbering management. The logo is meant to represent a very minimalist Ouroboros based on this fact - and by minimalist, I mean I suck at art. If you would like a practical example demonstrating usage of this plugin, just look in this project's build script!

[![Build Status](https://travis-ci.org/TAGC/Semver.svg)](https://travis-ci.org/TAGC/Semver) [ ![Download](https://api.bintray.com/packages/tagc/gradle-plugins/gradle-semver-plugin/images/download.svg) ](https://bintray.com/tagc/gradle-plugins/gradle-semver-plugin/_latestVersion)

Setup
---

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

    // Optional
    snapshotBump = 'PATCH' // Or 'MINOR' or 'MAJOR'
}
```

The `versionFilePath` property must point to a file that stores the 'raw' version string for your project in the form `<major>.<minor>.<patch>`, e.g. `version='1.2.3'` or simply `1.2.3`.

The `snapshotBump` property is optional and specifies what snapshot versions the plugin should derive from the latest released versions. For instance, if the last released version of a project was `v0.1.0` and `snapshotBump=PATCH` was specified, any development work will be treated as `v0.1.1-SNAPSHOT`. If `snapshotBump=MINOR` is specified instead, development work will be treated as `v0.2.0-SNAPSHOT`, and for `snapshotBump=MAJOR` it would be treated as `v1.0.0-SNAPSHOT`. This helps reconcile the differences between workflow strategies such as [Git Flow](http://nvie.com/posts/a-successful-git-branching-model/) which expect release versions to be decided only as releases are about to be made with [Maven's philosophy of always deciding on (or guessing) what the next release version will be](http://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN401). Predict whether your next release will introduce minor bug fixes, new (but backwards-compatible) features, or breaking changes, and set `snapshotBump` accordingly. By default (when this property is not explicitly set), `snapshotBump` will be treated as set to `PATCH`.

Description
---

This plugin is designed to be applied to projects that are using Gradle as a build automation tool and Git for source code management. The plugin will attempt to extract a 'raw' semantic version from a specified file (e.g. `1.2.3`) and then perform either of two actions:
  - if the plugin detects that you are currently on the **master** Git branch, it will save that version string as-is to your project's `project.version` property.
  - if the plugin detects that you are currently on any other Git branch, it will generate an appropriately-bumped version with '-SNAPSHOT' appended to the corresponding version string (e.g. `1.2.4-SNAPSHOT`). This is then applied to your project's `project.version` property.

This behaviour is performed during the configuration stage of the build lifecycle to allow the `project.version` property to be evaluated properly by other parts of the project's build script. If you find that `version.property` is being evaluated as `unspecified` in other parts of your build script, you can try wrapping instances of it in a `project.afterEvaluate` closure, as follows:

```
project.afterEvaluate {
    assert project.version
    version = project.version
}
```

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

Project Health Metrics
---

This project is continuously analysed by Jacoco (to calculate test code coverage) and CodeNarc (to identify coding issues).

- Present code coverage: 46.5%
- Present high priority issues: 0
- Present medium priority issues: 2
- Present low priority issues: 3

More detailed information about code coverage can be found in the [Jacoco report](http://tagc.github.io/Semver/health/jacoco/index.html)*.

More detailed information about coding issues can be found in the [CodeNarc report](http://tagc.github.io/Semver/health/codenarc/main.html)*.


Documentation
---

You can access the documentation for this project from [here](http://tagc.github.io/Semver/docs/groovydoc/index.html)*.

Acknowledgements
---

The development of this project has been largely based on work done by:
 - [Andrew Oberstar](https://github.com/ajoberstar) for his [Gradle-Git](https://github.com/ajoberstar/gradle-git) project, which this plugin relies on to query the current Git branch.
 - [Benjamin Muschko](http://www.gradleware.com/team/benjamin-muschko/) for his book, *Gradle in Action*, from which I've been trying to learn Gradle.

<sub>*Note: The reports and documentation may not be kept up to date, and some of the information kept in this README may not match up with the information provided in them. I'm intending to improve the automation in this area to help keep everything consistent.</sub>