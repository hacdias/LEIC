#!/bin/sh

rm -f xml/*
for file in examples/*.og
do
  echo $file
  ./og --target xml $file
done
mv examples/*.xml xml/
echo "$0: finished"
