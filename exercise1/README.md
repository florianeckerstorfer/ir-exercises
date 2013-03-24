Information Retrieval Exercise 1
================================

Requirements
------------

The following libraries must be put into the `vendor/` directory.

- [`cglib-nodep-2.2.3.jar`](http://sourceforge.net/projects/cglib/files/cglib2/2.2.3/cglib-nodep-2.2.3.jar/download)
- [`easymock-3.1.jar`](http://sourceforge.net/projects/easymock/files/EasyMock/3.1/easymock-3.1.zip/download)
- [`hamcrest-core-1.3.jar`](http://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar)
- [`junit-4.11.jar`](http://search.maven.org/remotecontent?filepath=junit/junit/4.11/junit-4.11.jar)
- [`objenesis-1.3.jar`](http://objenesis.googlecode.com/files/objenesis-1.3-bin.zip)

Usage
-----

Ant is used to compile and run the text indexer. Compile the project:

    $ ant compile

Create an executable JAR:

    $ ant dist

Run the text indexer:

    $ ant run

Run all tests:

    $ ant test

