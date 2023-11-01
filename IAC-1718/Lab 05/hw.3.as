SP_INICIAL  EQU FDFFh
        
            ORIG    0000h
            JMP     Inicio

fib:        MOV R3, 1
            CMP     M[SP+2], R3
            BR.P    rec
            MOV     M[SP+3], R3
            RETN    1

rec:        MOV     R1, M[SP+2] ; obtem o N
            DEC     R1          ; decrementa N
            PUSH    R1          ; guarda o N AGAIN
            PUSH    R0          ; reserva espaco p/ retorno
            PUSH    R1          ; coloca parametro de entrada
            CALL    fib

            MOV     R2, M[SP+2] ; obtem N
            DEC     R2
            PUSH    R0
            PUSH    R2
            CALL    fib

            POP     R1
            POP     R4
            ADD     R1, R4
            MOV     M[SP+3], R1
            RETN    1

Inicio:     MOV     R7, SP_INICIAL
            MOV     SP, R7
            PUSH    R0
            PUSH    6
            CALL    fib
            POP     R6
Fim:        BR      Fim