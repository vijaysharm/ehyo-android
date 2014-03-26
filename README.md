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
This project was designed around the default Android Studio project structure, and therefore makes a lot of assumptions based on the way the code is structured. 

TODO
----
##High Level Goals
+ Provide a way to create a new Android application
++ Might create a project with basic pieces
+++ Dependency Injection
+++ Unit testing Framework
+++ etc...
+ Provide a way to add a new Component to the Android project 
++ Includes adding the new Component to the Manifest
+ Provide a way to add permissions to the project
+ Provide a way to search maven and add libraries to the project
+ Provide a way to add new plugins to do more command line stuff


##Tasks
+ Support 3rd party plugins
+ Show user formatted error when the name given in plugin is not found
+ Show better version information