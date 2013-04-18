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
- [`commons-cli-1.2.jar`](http://tweedo.com/mirror/apache//commons/cli/binaries/commons-cli-1.2-bin.zip)
- [`weka.jar`](http://sourceforge.net/projects/weka/files/latest/download)
- [`args4j-2.0.21.jar`](http://maven.jenkins-ci.org/content/repositories/releases/args4j/args4j/2.0.21/args4j-2.0.21.jar)
- [`colt.jar`](http://acs.lbl.gov/software/colt/colt-download/releases/colt-1.2.0.zip) (and `concurrent.jar` from the same download)


Usage
-----

Ant is used to compile and run the text indexer. Compile the project:

    $ ant compile

You need to probably increase the memory the Java heap has:

    export ANT_OPTS=-Xmx10g

Note: the memory is mostly when reading and reading ARFF files and highly depends on the Gargabe Collector of JVM (that is, luck).

Now you can run the text indexer;

    $ ant run-index-small
    $ ant run-index-medium
    $ ant run-index-large

Or you can run all three at once:

    $ ant run-index

To fire 20 sample queries against the index you can use:

    $ ant run-retrieval-small
    $ ant run-retrieval-medium
    $ ant run-retrieval-large

Or you can execute all three at once:

    $ ant run-retrieval

The index is stored in `./output` and the results of the queries are stored in `./output/query`.

