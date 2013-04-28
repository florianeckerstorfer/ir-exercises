Information Retrieval Exercise 2
================================

Requirements
------------

To be able to run exercise 2 you need to setup up exercise 1 first. Please see the `README.md` file in the exercise1 directory for detailed instructions.

Usage
-----

You need to generate an index first. How this can be done is described in the `README.md` file of exercise1.

There are, again, different retrieval targets in the ANT build file to run the retrieval program with different index.

The index are taken from the `exercise1/output/` directory.

First of all, there are three different threshold sizes: `small`, `medium` and `large`.

	$ ant run-retrieval-small
	$ ant run-retrieval-medium
	$ ant run-retrieval-large
	$ ant run-retrieval

You can also decide to use the ARFF index format. Please note that we use the ARFF implementation provided by the WEKA library in a very naive way, therefore the ARFF reader requires a lot of memory (~10GB for the large index).

	$ ant run-retrieval-small-arff
	$ ant run-retrieval-medium-arff
	$ ant run-retrieval-large-arff
	$ ant run-retrieval-arff

The retrieval program is also able to load GZ compressed index files.

	$ ant run-retrieval-small-gz
	$ ant run-retrieval-medium-gz
	$ ant run-retrieval-large-gz
	$ ant run-retrieval-gz
	$ ant run-retrieval-small-arff-gz
	$ ant run-retrieval-medium-arff-gz
	$ ant run-retrieval-large-arff-gz
	$ ant run-retrieval-arff-gz

