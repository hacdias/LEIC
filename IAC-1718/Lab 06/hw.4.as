SP_INICIAL      EQU     FDFFh
IO_DISPLAY      EQU     FFF0h
INT_MASK_ADDR   EQU     FFFAh
INT_TEMP        EQU     FFF6h
INT_TEMP_CTRL   EQU     FFF7h
INT_MASK        EQU     1000000000000010b

                ; Tabela de interrupcoes
                ORIG    FE01h
INT_KEY1        WORD    INT1F               ; KEY 1
                ORIG    FE0Fh
INT_TEMPK       WORD    INT_TEMP_F          ; TEMP

                ORIG    8000h
INC_VAL         WORD    1
COUNTER         WORD    0
TICK            WORD    0

                ; Código
                ORIG    0000h
                JMP     Inicio
    
RESET_TEMP:     PUSH    R1
                MOV     R1, 10
                MOV     M[INT_TEMP], R1
                MOV     R1, 1
                MOV     M[INT_TEMP_CTRL], R1
                POP     R1
                RET

INT_TEMP_F:     CALL    RESET_TEMP
                PUSH    R1
                MOV     R1, M[INC_VAL]
                ADD     M[COUNTER], R1   
                INC     M[TICK]
                POP     R1
                RTI

INT1F:          NEG     M[INC_VAL]
                RTI

UPDATE:         PUSH    R1
                PUSH    R2
                PUSH    R3
                DEC     M[TICK]
                MOV     R1, 4
                MOV     R2, M[COUNTER]
                MOV     R3, IO_DISPLAY
UPDATE_C:       DEC     R1
                MOV     M[R3], R2
                ROR     R2, 4
                INC     R3
                CMP     R1, R0
                BR.NZ   UPDATE_C
                POP     R3
                POP     R2
                POP     R1
                RET

Inicio:         MOV R7, SP_INICIAL
                MOV SP, R7
                MOV R7, INT_MASK
                MOV M[INT_MASK_ADDR], R7
                CALL RESET_TEMP                                                                                     
                ENI

INFINITY:       CMP     M[TICK], R0
                CALL.NZ UPDATE
                BR      INFINITY
