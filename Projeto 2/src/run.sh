#!/bin/bash

set -e

gcc -g -Wall -ansi -pedantic -o proj main.c utils.c task.c
valgrind --tool=memcheck --leak-check=yes ./proj
