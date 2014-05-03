Ehyo
====
A command line tool for scaffolding Android Applications. Heavyily inspired by and loosely named after the yeoman web scaffolding tool. The goal of this project is to provide a command line tool for developers looking to perform worry free boiler-plate operations to their Android projects.

The tool allows you to perform common tasks to your Android project
+ Search/Add/Remove permissions from your manifest
+ Search/Add/Remove maven repository dependecies to your build
+ Apply common Android templates to your project
+ Apply templates provided by jgilfelt with https://github.com/jgilfelt/android-adt-templates

Usage
-----
To view the list of actions ehyo supports, run the list command
`./bin/ehyo list`

To add or remove a permission to a manifest, run the permissions command. By default, ehyo assumes you're performing modifications to a project in the current directory. To tell ehyo which project to apply to, include the --directory <dir> argume
`./bin/ehyo permissions --add internet`
`./bin/ehyo --directory <dir> permissions --remove internet`

To search, add or remove dependencies from a build, run the dependencies command. If you'd like to see how this will affect your project file, ehyo can be run with a --dry-run option 
`./bin/ehyo dependencies -s butterknife --add`
`./bin/ehyo dependencies -g com.jakewharton`
`./bin/ehyo dependencies --remove retrofit --dry-run`

To apply a template to your project, run the templates command.
`./bin/ehyo templates list`
`./bin/ehyo templates [templatename]`

Limitations
-----------
+ This project was designed around the default Android gradle project structure, and therefore makes a lot of assumptions based on the way it structures its code. It looks for settings.gradle files to determine the project structure.
++ Overridden source set paths in builds are not supported

+ Templates will always add dependencies to the 'compile' configuration

+ Ruins the formatting of the AndroidManifest.xml

+ There's no rollback during failures. If part of a template is applied, and an exception occurs, then there's no way to undo what failed. As a suggestion, run ehyo within a version controlled project with a fresh checkout. That will allow you to rollback your changes when ehyo craps out on you.

Building the jar
----------------
`mvn package`

Developing using Eclipse
------------------------
`mvn clean package dependency:sources dependency:resolve -Dclassifier=javadoc eclipse:eclipse`

TODO
----
##Bugs
+ FIX: ApplicationRunActionFactoryTest, AndroidManifestDocumentTest

+ GradleBuildDocumentModel doesn't support the following kind of dependency
++ This was seen in the Muzei Gradle build
++ compile ('de.greenrobot:eventbus:2.2.0') { exclude group:'com.google.android', module: 'support-v4' // already included below }
+++ Current model will think its a context
++ Fails for certain types (e.g. tasks)

+ There's a bug displaying the diff: 
++ the line below the added line is incorrect, it shows the line after that.

+ Displays the following when commands are run
++ Manifest for source set: [app:SourceSetType [type=debug]] is null!!!!

+ Ugly exception when no internet connection with dependency command

+ Need to create/modify build.gradle if user wants to add a dependency to a build config that isn't explicitly defined.
++ check GradleBuildDocument and ProjectRegistryBuilder

##Tasks
+ Show better version information
++ Better option description for usage
+ Have the PatchApplier not modify the formatting of the XML file

+ Improve dry-run diff
++ Remove line numbers
++ Should print the number of documents modified (look a the way git does commits)

+ Check if the project structure is valid
++ You don't want to throw an exception when they run ./bin/ehyo ... you want them to see the usage. 

+ As part of the list command, we should be able to provide a filter option

+ Add support for a --debug for verbose printing

+ Add support for an "interactive" mode. Allows for users to step and select what is applied.

+ Should all actions have names so that output is more legible?
++ Might be useful with --debug (Track when an action is added, whether its executed or not, etc...)

+ Move maven search into the Service
++ Support searching by name, artifact id, etc..

+ Make all core models package protected
++ Document public API

+ Finish the dependency command
++ Finish search by group ID

+ Finish template command

+++ Need to support a non-existent manifest (as an empty file)
++ Prompt user to fill in fields (without resorting to default values)
++ Support adding build variables in the freemaker template (minApiLevel, build version, etc...)
++ Support loading templates from disk or remote (and not just ones from the packaged JAR)

+ Write integration tests 
++ for templates
++ for entire application

+ Unit test existing commands

##Roadmap
+ Provide a way to create a new Android application
++ Create a project with basic pieces
+++ Dependency Injection
+++ Unit testing Framework

+ Create a template that add the 'big cookie model to your project'

+ Provide a command that lets you rename your package namespace

+ Read templates from github

+ Provide a way to easily create templates from a diff

+ Provide a way to save templates (as a gist?)

+ Provide support for android projects not created with gradle

+ Improve Command line
++ Need to be able to multi-select
++ Inquire.js for java?
