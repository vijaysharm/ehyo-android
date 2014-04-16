Ehyo
====
An Android scaffolding command line tool. Heavyily inspired by and loosely named after the yeoman web scaffolding tool. The goal of this project is to provide a command line tool for developers looking to perform worry free boiler-plate operations to their Android projects.

Developing using Eclipse
------------------------
`mvn clean package dependency:sources dependency:resolve -Dclassifier=javadoc eclipse:eclipse`

Building the jar
----------------
`mvn package`

Running the command-line
------------------------
`java -jar ehyo.jar`

Limitations
-----------
This project was designed around the default Android Studio project structure, and therefore makes a lot of assumptions based on the way it structures its code. 

Usage
-----
ehyo list
ehyo manage-permissions --add internet
ehyo manage-permissions --remove internet
ehyo search --lib butterknife
ehyo --plugins <ns> --directory <dir> search --add flow

TODO
----
##High Level Goals
+ Provide a way to create a new Android application
++ Might create a project with basic pieces
+++ Dependency Injection
+++ Unit testing Framework
+++ etc...
+ Provide a way to add a new Component to the Android project (Activity, Services, etc..)
++ Includes adding the new Component to the Manifest
+ Provide a plugin that lets you rename your package namespace
+ Provide a plugin that add the 'big cookie model to your project'

##Tasks
+ Show user formatted error when the name given in plugin is not found
+ Show better version information
+ Have the custom logger respect debug mode
++ Better option description for usage
+ Finish the Permissions plugin
++ Have Permissions plugin take the permission type from the command line, or search a repository of known permissions
++ Check what permissions are already applied to the manifest
++ Support removal
+ Improve ManifestActionHandler to only add permissions if defined in manifest
+ Improve ManifestActionHandler to support removing permissions (defined in the xml)
+ Have the ManifestChangeManagerFactory not modify the formatting of the XML file
+ Read the gradle build to understand the project structure better.
+ Improve dry-run diff
+ Check if the project structure is valid
++ Need to determine when to notify the user that the directory they are loading from is invalid.
++ You don't want to throw an exception when they run ./bin/ehyo ... you want them to see the usage. 
++ You also don't want to show an exception when all they want to do is run list
+ FIX: ApplicationRunActionFactoryTest, RunActionTest
+ As part of the list plugin, we should be able to provide a filter option
+ Should all actions have names so that output is more legible?
++ Might be useful for --debug (Track when an action is added, whether its executed or not, etc...)
+ Add support for a --debug for verbose printing
+ Move maven search into the Service
+ Have the *Actions index by Manifest or Build instead of IDs

##Bugs
+ There's a bug with the optionselector
++ I entered 2 when two manifests we're shown to me, and both manifests we're used (should have just been the second one, not all)
+ Broke Usage
++ It is no longer shown
