#!/bin/bash

#TODO: Verify if arguments are the right type
#ASK: Should we create inputs.res and old while running this script?
if [ "$#" -ne 2 ]
then
    echo "Invalid Arguments"
    exit 1
fi

N="$1"
input="$2"
output="$input.res"
speedupFile="$input.speedups.csv"
echo "#threads,exec_time,speedup" >> $speedupFile

./CircuitRouter-SeqSolver/CircuitRouter-SeqSolver "$input"
seqTime=$( echo $(grep -o 'Elapsed time    = .*' "$output") | cut -f 4 -d " ")
echo "1S,$seqTime,1" >> $speedupFile

for i in $(seq 1 $N)
do
    ./CircuitRouter-ParSolver/CircuitRouter-ParSolver "$input" -t $i
    parTime=$( echo $(grep -o 'Elapsed time    = .*' "$output") | cut -f 4 -d " ")
    speedup=$( echo "scale=6; ${seqTime}/${parTime}" | bc)
    echo $i,$parTime,$speedup >> $speedupFile
done
