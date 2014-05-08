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
`ehyo list`

To add or remove a permission to a manifest, run the permissions command. By default, ehyo assumes you're performing modifications to a project in the current directory. To tell ehyo which project to apply to, include the --directory <dir> argument.

`ehyo permissions --add internet`
`ehyo --directory <dir> permissions --remove internet`

To search, add or remove dependencies from a build, run the dependencies command. If you'd like to see how this will affect your project file, ehyo can be run with a --dry-run option 
`ehyo dependencies -s butterknife --add`
`ehyo dependencies -g com.jakewharton`
`ehyo dependencies --remove retrofit --dry-run`

To apply a template to your project, run the templates command.
`ehyo templates --list`
`ehyo templates -l`
`ehyo templates -a [templatename]`
`ehyo templates --apply [templatename]`

After downloading ehyo, a script to use ehyo is provided in ./bin/ehyo ... Add this to your path or give the full path to the script in bin and enjoy.

Limitations
-----------
+ This project was designed around the default Android gradle project structure, and therefore makes a lot of assumptions based on the way it structures its code. It looks for settings.gradle files to determine the project structure.
++ Overridden source set paths in builds are not supported

+ Templates will always add dependencies to the 'compile' configuration

+ Ruins the formatting of the AndroidManifest.xml

+ There's no rollback during failures. If part of a template is applied, and an exception occurs, then there's no way to undo what failed. As a suggestion, run ehyo within a version controlled project with a fresh checkout. That will allow you to rollback your changes when ehyo craps out on you.

+ Developed on and only tested on OSX. There isn't any OS specific stuff in the code, per-se, but consider this warning if things don't work ask you expect.

Building the jar
----------------
`mvn package`

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

+ PatchApplier modifies the formatting of XML (doesn't respect the desired Android formatting)

+ Doesn't cope well with dependencies defined as "project(':libraries:lib1')" or "files('libs/foo.jar')"

##Tasks
+ Show better version information
++ Better option description for usage

+ Improve dry-run diff
++ Remove line numbers
++ Should print the number of documents modified (look a the way git does commits)

+ Check if the project structure is valid
++ You don't want to throw an exception when they run ./bin/ehyo ... you want them to see the usage. 

+ Add support for a --debug for verbose printing

+ Should all actions have names so that output is more legible?
++ Might be useful with --debug (Track when an action is added, whether its executed or not, etc...)

+ Make all core models package protected
++ Document public API

+ Need to create/modify build.gradle if user wants to add a dependency to a build config that isn't explicitly defined.
++ check GradleBuildDocument and ProjectRegistryBuilder

+ All commands
++ Support --project id or --manifest id or --sourceset id where the ID is something you can use to distinctly select the right item to apply a command to (avoiding prompts)

+ Dependencies command
++ Support upgrading dependencies

+ Finish template command
++ Add support for an "interactive" mode. Allows for users to step and select what is applied.
+++ Need to support a non-existent manifest (as an empty file)
++ Support adding build variables in the freemaker template (minApiLevel, build version, etc...)
++ Support loading templates from disk or remote (and not just ones from the packaged JAR)
++ Support optional in the template parameter file
+++ See blank activity template
++ Support --<parameter> where the argument is the same parameter in the template
++ Read the min- supported properties on the template object and filter out non-applicable ones during execution
+ Support the 'revision' attribute on dependencies from template (see Login Activity)

+ Write integration tests 
++ for templates
++ for entire application

+ Move all TODOs to Issues list.

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

+ Provide ehyo through Brew
