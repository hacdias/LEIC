#!/bin/bash

for f in $(ls ./inputs/*.txt)
do
    echo filename: $f
    echo number of lines: $(expr $(wc -l < $f) + 1)
    echo number of interconections: $(grep -c "p" $f)
    echo =================================
done