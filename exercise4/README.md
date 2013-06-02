Information Retrieval Exercise 4
================================

Installation
------------

Clone (or download) this repository:

	$ git clone git@github.com:florianeckerstorfer/ir-exercises.git

Navigate into the directory for exercise 4:

	$ cd ./ir-exercises/exercise4

You need to have GATE installed and store the path to it in the environment variable `$GATE_HOME`

	$ export GATE_HOME=/path/to/GATE/

You also need to store the path to this exercise in `$CFP_HOME`:

	$ export CFP_HOME=/path/to/ir-exercises/exercise4

You need to place a copy of `log4j-1.2.17.jar` into the `vendor/` directory.

Then you can run CFP:

	$ ant run

The result will be stored as a XML file in `output/result.xml`.

How it works
------------

Our code works like this.

The training and test data is stored in `data/training-xml` and `data/test-xml` in the XML format. This data is the preprocessed corpus. That is, JAPE has been used (within GATE) to convert the annotations. In our case we transform all annotations (from a type like `workshopname`, `conferencename`, etc) to an annotation `IE` with the original type as feature.

Java is then used to execute Batch Learning PR and to extract the annotations from the corpus. The corpus was split at 20-80.


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


Resources
----------

- [Module 11: Machine Learning](http://gate.ac.uk/sale/talks/gate-course-jun12/track-3/module-11-machine-learning/module-11.pdf) and [resources](http://gate.ac.uk/sale/talks/gate-course-jun12/track-3/module-11-machine-learning/)
- [GATE Embedded](http://gate.ac.uk/sale/tao/splitch7.html#x11-1570007)
- [Batch Learning PR](http://gate.ac.uk/sale/tao/splitch18.html#x23-44600018.2)
