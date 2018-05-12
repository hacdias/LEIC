#!/bin/bash

set -e

gcc -g -Wall -ansi -pedantic -o proj *.c
valgrind --tool=memcheck --leak-check=yes ./proj
