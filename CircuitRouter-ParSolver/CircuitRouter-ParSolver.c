/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This code is an adaptation of the Lee algorithm's implementation originally included in the STAMP Benchmark
 * by Stanford University.
 *
 * The original copyright notice is included below.
 *
  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) Stanford University, 2006.  All Rights Reserved.
 * Author: Chi Cao Minh
 *
 * =============================================================================
 *
 * Unless otherwise noted, the following license applies to STAMP files:
 *
 * Copyright (c) 2007, Stanford University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 *
 *     * Neither the name of Stanford University nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY STANFORD UNIVERSITY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL STANFORD UNIVERSITY BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * =============================================================================
 *
 * CircuitRouter-ParSolver.c
 *
 * =============================================================================
 */


#include <assert.h>
#include <getopt.h>
#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include "lib/list.h"
#include "maze.h"
#include "router.h"
#include "lib/timer.h"
#include "lib/types.h"
#include "string.h"
#include "unistd.h"

enum param_types {
    PARAM_BENDCOST = (unsigned char)'b',
    PARAM_XCOST    = (unsigned char)'x',
    PARAM_YCOST    = (unsigned char)'y',
    PARAM_ZCOST    = (unsigned char)'z',
    PARAM_TASKS    = (unsigned char)'t'
};

enum param_defaults {
    PARAM_DEFAULT_BENDCOST = 1,
    PARAM_DEFAULT_XCOST    = 1,
    PARAM_DEFAULT_YCOST    = 1,
    PARAM_DEFAULT_ZCOST    = 2,
    PARAM_DEFAULT_TASK     = -1
};

char* global_inputFile = NULL;
long global_params[256]; /* 256 = ascii limit */


/* =============================================================================
 * displayUsage
 * =============================================================================
 */
static void displayUsage (const char* appName){
    printf("Usage: %s [options]\n", appName);
    puts("\nOptions:                            (defaults)\n");
    printf("    b <INT>    [b]end cost          (%i)\n", PARAM_DEFAULT_BENDCOST);
    printf("    x <UINT>   [x] movement cost    (%i)\n", PARAM_DEFAULT_XCOST);
    printf("    y <UINT>   [y] movement cost    (%i)\n", PARAM_DEFAULT_YCOST);
    printf("    z <UINT>   [z] movement cost    (%i)\n", PARAM_DEFAULT_ZCOST);
    printf("    t <UINT>   [t]asks              (%i)\n", PARAM_DEFAULT_TASK);
    printf("    h          [h]elp message       (false)\n");
    exit(1);
}


/* =============================================================================
 * setDefaultParams
 * =============================================================================
 */
static void setDefaultParams (){
    global_params[PARAM_BENDCOST] = PARAM_DEFAULT_BENDCOST;
    global_params[PARAM_XCOST]    = PARAM_DEFAULT_XCOST;
    global_params[PARAM_YCOST]    = PARAM_DEFAULT_YCOST;
    global_params[PARAM_ZCOST]    = PARAM_DEFAULT_ZCOST;
    global_params[PARAM_TASKS]    = PARAM_DEFAULT_TASK;
}


/* =============================================================================
 * parseArgs
 * =============================================================================
 */
static void parseArgs (long argc, char* const argv[]){
    long opt;
    long i;

    opterr = 0;

    if (argc < 2) {
        printf("Please provide a filename.\n");
        exit(1);
    }

    setDefaultParams();

    while ((opt = getopt(argc, argv, "hb:t:x:y:z:")) != -1) {
        switch (opt) {
            case 'b':
            case 'x':
            case 'y':
            case 'z':
            case 't':
                global_params[(unsigned char)opt] = atol(optarg);
                break;
            case '?':
            case 'h':
            default:
                opterr++;
                break;
        }
    }

    global_inputFile = argv[optind++];

    for (i = optind; i < argc; i++) {
        fprintf(stderr, "Non-option argument: %s\n", argv[i]);
        opterr++;
    }

    if (global_params[PARAM_TASKS] == PARAM_DEFAULT_TASK) {
        fprintf(stderr, "You must set the number of tasks.\n");
        opterr++;
    }

    if (opterr) {
        displayUsage(argv[0]);
    }
}

char *dumpPlusSuffix (const char *s) {
    char *d = malloc(strlen(s) + 5);   // Space for length + dot suffix + NULL
    if (d == NULL) return NULL;
    strcpy(d,s);
    return d;
}

FILE* get_output_file () {
    char* file_name = dumpPlusSuffix(global_inputFile);
    if (file_name == NULL) {
        return NULL;
    }

    strcat(file_name, ".res");

    if (access(file_name, F_OK) != -1) {
        char *new_name = dumpPlusSuffix(file_name);
        if (new_name == NULL) {
            return NULL;
        }

        strcat(new_name, ".old");
        remove(new_name);
        rename(file_name, new_name);
        free(new_name);
    }

    FILE* fp = fopen(file_name, "w");
    free(file_name);
    return fp;
}

/* =============================================================================
 * main
 * =============================================================================
 */
int main(int argc, char** argv){
    /*
     * Initialization
     */
    parseArgs(argc, (char** const)argv);
    maze_t* mazePtr = maze_alloc();
    assert(mazePtr);

    FILE * fpin = fopen(global_inputFile, "r");
    if (fpin == NULL) {
        printf("Error while reading file.\n");
        exit(1);
    }

    FILE * fpout = get_output_file();
    if (fpout == NULL) {
        printf("Error while writing file.\n");
        exit(1);
    }

    long numPathToRoute = maze_read(mazePtr, fpin, fpout);
    router_t* routerPtr = router_alloc(global_params[PARAM_XCOST],
                                       global_params[PARAM_YCOST],
                                       global_params[PARAM_ZCOST],
                                       global_params[PARAM_BENDCOST]);
    assert(routerPtr);
    list_t* pathVectorListPtr = list_alloc(NULL);
    assert(pathVectorListPtr);

    router_solve_arg_t routerArg = {
        routerPtr,
        mazePtr,
        pathVectorListPtr
    };

    sem_init(&routerArg.workQueueSem, 0, 1);
    sem_init(&routerArg.pathVectorListSem, 0, 1);

    TIMER_T startTime;
    TIMER_READ(startTime);

    pthread_t threads[global_params[PARAM_TASKS]];

    for (int i = 0; i < global_params[PARAM_TASKS]; i++)
        pthread_create(&threads[i], NULL, router_solve, &routerArg); 

    for (int i = 0; i < global_params[PARAM_TASKS]; i++)
        pthread_join(threads[i], NULL);

    TIMER_T stopTime;
    TIMER_READ(stopTime);

    sem_destroy(&routerArg.workQueueSem);
    sem_destroy(&routerArg.pathVectorListSem); 

    long numPathRouted = 0;
    list_iter_t it;
    list_iter_reset(&it, pathVectorListPtr);
    while (list_iter_hasNext(&it, pathVectorListPtr)) {
        vector_t* pathVectorPtr = (vector_t*)list_iter_next(&it, pathVectorListPtr);
        numPathRouted += vector_getSize(pathVectorPtr);
	}
    fprintf(fpout, "Paths routed    = %li\n", numPathRouted);
    fprintf(fpout, "Elapsed time    = %f seconds\n", TIMER_DIFF_SECONDS(startTime, stopTime));

    /*
     * Check solution and clean up
     */
    assert(numPathRouted <= numPathToRoute);
    bool_t status = maze_checkPaths(mazePtr, pathVectorListPtr, fpout);
    assert(status == TRUE);
    fputs("Verification passed.", fpout);

    maze_free(mazePtr);
    router_free(routerPtr);

    list_iter_reset(&it, pathVectorListPtr);
    while (list_iter_hasNext(&it, pathVectorListPtr)) {
        vector_t* pathVectorPtr = (vector_t*)list_iter_next(&it, pathVectorListPtr);
        vector_t* v;
        while((v = vector_popBack(pathVectorPtr))) {
            // v stores pointers to longs stored elsewhere; no need to free them here
            vector_free(v);
        }
        vector_free(pathVectorPtr);
    }
    list_free(pathVectorListPtr);

    fclose(fpin);
    fclose(fpout);
    exit(0);
}


/* =============================================================================
 *
 * End of CircuitRouter-ParSolver.c
 *
 * =============================================================================
 */
