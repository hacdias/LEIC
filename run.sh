#!/bin/bash

gcc -c -o coordinate.o coordinate.c 
gcc -c -o grid.o grid.c 
gcc -c -o CircuitRouter-SeqSolver.o CircuitRouter-SeqSolver.c 
gcc -c -o maze.o maze.c 
gcc -c -o router.o router.c 
gcc -c -o lib/list.o lib/list.c 
gcc -c -o lib/pair.o lib/pair.c 
gcc -c -o lib/queue.o lib/queue.c 
gcc -c -o lib/vector.o lib/vector.c 
gcc -o CircuitRouter-SeqSolver coordinate.o grid.o CircuitRouter-SeqSolver.o maze.o router.o lib/list.o lib/pair.o lib/queue.o lib/vector.o -lm
