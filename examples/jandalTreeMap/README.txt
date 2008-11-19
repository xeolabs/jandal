This is TreeMap, a demonstration application built using the Jandal Web Application Framework.

Lindsay Kay
December 24, 2007
lindsay.stanley.kay@gmail.com
http://www.neocoders.com/projects/jandal


DIRECTORY LAYOUT
================

ant                   ANT build script and project properties
classes               ANT-built Java classes
jetty-x.x.x           Bundled minimal Jetty server for trying out this application
lib                   Libraries used by this application
META-INF              Copied into ANT-generated war file
src                   Java source files, FreeMarker templates and other bundled resources
war                   ANT-generated war file
WEB-INF               web.xml that goes into the war file 
README.txt            This file


ANT TARGETS FOR THIS PROJECT
============================

From the ant directory, you can build the following ANT targets. 
These are like a pipeline; each depends on the one before it, so that when you build it, it
will build the ones preceeding it in the order lised here. The "run" target is the one
to build if you want to try the application out right away.


1. 	Delete class files, delete war, delete deployed 
	application from Jetty (stop Jetty first if running):

		ant clean

2. 	Clean build of class files (clean first):

		ant build

3.	Generate war file (clean and build first):

		ant package

5.	Deploy to Jetty, but do not start Jetty 
	(clean, build and package first):

		ant deploy

6.	Stop Jetty, deploy, then start Jetty (clean, build, package and 
	deploy first):

		ant run

After you have successfully built the "run" target, you can check
this application out in your browser at 

   http://localhost:8080/jandal-treemap

where "jandal-treemap" is the value for the "project.distName" property
the build.properties file in the ant directory.


