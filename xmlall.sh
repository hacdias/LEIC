#!/bin/sh

for file in examples/*.og
do
  echo $file
  ./og --target xml $file
done

echo "$0: finished"
