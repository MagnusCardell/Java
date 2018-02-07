# Makefile for Backend server
.SUFFIXES:  .class .java
.java.class:
	javac $*.java
default:  JavaAPIServer.class HttpParser.class RestModel.class RestApiHandler.class RestApiModel.class
javadoc:
	cd doc ; javadoc -author -package *.java