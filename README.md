Ehyo
====
A command line tool for scaffolding Android Applications. Heavyily inspired by and loosely named after the yeoman web scaffolding tool. The goal of this project is to provide a command line tool for developers looking to perform worry free boiler-plate operations to their Android projects.

TODO: The tool supports android templates and https://github.com/jgilfelt/android-adt-templates

TODO: Plugin architecture

Usage
-----
./bin/ehyo list
./bin/ehyo manage-permissions --add internet
./bin/ehyo manage-permissions --remove internet
./bin/ehyo search --lib butterknife
./bin/ehyo --plugins <ns> --directory <dir> search --add flow

Limitations
-----------
+ This project was designed around the default Android gradle project structure, and therefore makes a lot of assumptions based on the way it structures its code. 
++ Modified source set paths are not supported

+ Templates will always add dependencies to the compile configuration

+ Can't maintain the formatting of the AndroidManifest.xml

Developing using Eclipse
------------------------
`mvn clean package dependency:sources dependency:resolve -Dclassifier=javadoc eclipse:eclipse`

Building the jar
----------------
`mvn package`

TODO
----
##High Level Goals
+ Provide a way to create a new Android application
++ Might create a project with basic pieces
+++ Dependency Injection
+++ Unit testing Framework
+++ etc...
+ Create a template that add the 'big cookie model to your project'
+ Provide a plugin that lets you rename your package namespace

##Tasks
+ Show user formatted error when the name given in plugin is not found
+ Show better version information
++ Better option description for usage
+ Have the PatchApplier not modify the formatting of the XML file

+ Improve dry-run diff
++ Remove line numbers
++ Should print the number of documents modified (look a the way git does commits)

+ Check if the project structure is valid
++ Need to determine when to notify the user that the directory they are loading from is invalid.
++ You don't want to throw an exception when they run ./bin/ehyo ... you want them to see the usage. 
++ You also don't want to show an exception when all they want to do is run list

+ As part of the list plugin, we should be able to provide a filter option

+ Add support for a --debug for verbose printing

+ Add support for an "interactive" mode. Allows for users to step and select what is applied.

+ Should all actions have names so that output is more legible?
++ Might be useful with --debug (Track when an action is added, whether its executed or not, etc...)

+ Move maven search into the Service
++ Support searching by name, artifact id, etc..

+ Make all core models package protected
++ Document public API

+ Finish the maven search library plugin
++ Should it be renamed to dependency, providing a search, add, remove capability?
++ Clean up add / remove
++ Support adding libraries directly from command line (i.e. no searching, just raw text)

+ Finish the manifest-permission plugin
++ It should support an optional remove argument, which should display the list of existing permissions to remove

+ Finish Template Plugin
++ Should support applying a template to a sourceset without a manifest
+++ Need to support a non-existent manifest (as an empty file)
++ Prompt user to fill in fields (without resorting to default values)
++ Support adding build variables in the freemaker template (minApiLevel, build version, etc...)
++ Support loading templates from disk or remote (and not just ones from the packaged JAR)

##Bugs
+ Broke Usage
++ It is no longer shown

+ FIX: ApplicationRunActionFactoryTest

+ GradleBuildDocumentModel doesn't support the following kind of dependency
++ This was seen in the Muzei Gradle build
++ compile ('de.greenrobot:eventbus:2.2.0') { exclude group:'com.google.android', module: 'support-v4' // already included below }
+++ Current model will think its a context
++ Fails for certain types (e.g. tasks)

+ There's a bug with the diff: 
++ the line below the added line is incorrect, it shows the line after that.

+ manifestOut needs to be supplied only when not available.