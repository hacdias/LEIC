CFLAGS= -g

CircuitRouter-SeqSolver: coordinate.o grid.o CircuitRouter-SeqSolver.o maze.o router.o lib/list.o lib/pair.o lib/queue.o lib/vector.o -lm
	gcc $(CFLAGS) -o CircuitRouter-SeqSolver coordinate.o grid.o CircuitRouter-SeqSolver.o maze.o router.o lib/list.o lib/pair.o lib/queue.o lib/vector.o -lm

lib/vector.o: lib/vector.c lib/vector.h lib/types.h lib/utility.h
	gcc $(CFLAGS) -c -o lib/vector.o lib/vector.c 

lib/queue.o: lib/queue.c lib/queue.h lib/types.h
	gcc $(CFLAGS) -c -o lib/queue.o lib/queue.c

lib/pair.o: lib/pair.c lib/pair.h
	gcc $(CFLAGS) -c -o lib/pair.o lib/pair.c

lib/list.o: lib/list.c lib/list.h lib/types.h
	gcc $(CFLAGS) -c -o lib/list.o lib/list.c

router.o: router.c router.h coordinate.h grid.h lib/queue.h lib/vector.h
	gcc $(CFLAGS) -c -o router.o router.c

maze.o: maze.c maze.h coordinate.h grid.h lib/list.h lib/queue.h lib/pair.h lib/types.h lib/vector.h
	gcc $(CFLAGS) -c -o maze.o maze.c 

CircuitRouter-SeqSolver.o: CircuitRouter-SeqSolver.c lib/list.h maze.h router.h lib/timer.h lib/types.h
	gcc $(CFLAGS) -c -o CircuitRouter-SeqSolver.o CircuitRouter-SeqSolver.c

grid.o: grid.c grid.h coordinate.h lib/types.h lib/vector.h
	gcc $(CFLAGS) -c grid.c -o grid.o

coordinate.o: coordinate.c coordinate.h lib/pair.h lib/types.h
	gcc $(CFLAGS) -c -o coordinate.o coordinate.c 

clean:
	rm -f *.o lib/*.o CircuitRouter-SeqSolver