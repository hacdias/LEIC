CFLAGS= -g -O2 -Wall -pedantic -ansi

OBJECTS1= main.o recurs.o
OBJECTS2= main.o iter.o

all:
	make factorial-recurs 
	make factorial-iter

factorial-recurs : $(OBJECTS1)
	gcc $(CFLAGS) -o factorial-recurs $(OBJECTS1) 

factorial-iter : $(OBJECTS2)
	gcc $(CFLAGS) -o factorial-iter $(OBJECTS2) 

main.o: main.c fact.h

iter.o: iter.c fact.h

recurs.o: recurs.c fact.h

clean :
	rm factorial-recurs
	rm factorial-iter
	rm *.o                                                                                                                                                                                                                                                                                                             
run :                                                                                                                                                                                  make all
	./factorial-iter
