Information Retrieval Exercise 4
================================

HOW-TO
------

Step 1:

- download generic installer (https://sourceforge.net/projects/gate/files/gate/7.1/gate-7.1-build4485-installer.jar/download) and install it under vendor

Step 2:

add following jars into the build path:

	- vendor/GATE_Developer_7.1/bin/gate.jar 
	- vendor/GATE_Developer_7.1/lib/*
	- vendor/GATE_Developer_7.1/plugins/Learning/learningapi.jar

- split the corpus into two separate corpora, training and test. 2/3 in training and 1/3 in test. 

Step 3:

- convert the training corpus from gate's "inline annotations" into gate's standoff markup. 
	1. create a Corpus in Gate (opt. called it "training")
	2. populate it with the files in the training folder
	3. save the corpus as xml
- reason: the xml format is supported by the Batch Learning PR

Step 4: 

- run CFPExtractor.java

Step 5: 

- check the xml result
	- TODO


DESCRIPION
----------

This Java application uses Gate to extract the following information from conference workshop call for papers (CFP):

	- Name of Workshop
	- Acronym of Workshop
	- Date of Workshop
	- Location of Workshop
	- Name of Conference
	- Acronym of Conference
	- Paper Submission Date (of Workshop)

Gate provides many useful functionality through it's creole plugins. For our purpose the plugin "Learning" (gate.learning.LearningAPIMain) is used. The Processing Resource (PR) "Batch Leraning PR" is set up with an configuration file (ml-config.xml) and will be run in two run modes:

	1. TRAINING mode. The classes will be learned according to ml-config.xml on the training corpus.
	2. APPLICATION mode. The learned classes are applied on the test corpus.

The automatically applied Annotations will be saved in a Annotation Set called "machine_learned".

The output is an XML-file contains the extracted information (Annotations). 


