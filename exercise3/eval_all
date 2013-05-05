#!/bin/bash

count=0

for e in 1 2
do
    for s in "large" "medium" "small"
    do
        for i in {1..20}
        do
            official="./data/golden-standard/postings_topic$i.qrels.txt"
            submitted="./data/exercise${e}/${s}_topic${i}_groupC"
            output="./output/exercise${e}/${s}_topic${i}_groupC.txt"
            if [ -f $official ]; then
                if [ -f $submitted ]; then
                    count=$[count+1]
                    trec_eval $official $submitted > $output
                fi
            fi;
        done
    done
done

echo "execucuted trec_eval ${count} times"
