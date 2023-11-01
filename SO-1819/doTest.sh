#!/bin/bash

set -e

usage () {
    echo -e "usage: $(basename "$0") numTasks inputFile"
    echo -e "\narguments:"
    echo -e "\tnumTasks\ta positive integer"
    echo -e "\tinputFile\tpath to input file (must exist)"
}

n="$1"
input="$2"

if [ "$#" -ne 2 ] || ! [[ $n =~ ^[1-9][0-9]*$ ]] || ! [ -f $input ]
then
    usage
    exit 1
fi

output="$input.res"
speedupFile="$input.speedups.csv"
echo "#threads,exec_time,speedup" > $speedupFile

./CircuitRouter-SeqSolver/CircuitRouter-SeqSolver "$input"
seqTime=$( echo $(grep -o 'Elapsed time    = .*' "$output") | cut -f 4 -d " ")
echo "1S,$seqTime,1" >> $speedupFile

for i in $(seq 1 $n)
do
    ./CircuitRouter-ParSolver/CircuitRouter-ParSolver "$input" -t $i
    parTime=$( echo $(grep -o 'Elapsed time    = .*' "$output") | cut -f 4 -d " ")
    speedup=$( echo "scale=6; ${seqTime}/${parTime}" | bc)
    echo $i,$parTime,$speedup >> $speedupFile
done
