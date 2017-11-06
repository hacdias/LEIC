SP_INICIAL      EQU FDFFh
IO_DISPLAY      EQU FFF0h
INT_MASK_ADDR   EQU FFFAh

TEMP            EQU FFF6h
TEMP_S          EQU FFF7h
INT_MASK        EQU 1000000000000000b

                ; Tabela de interrupcoes
                ORIG FE0Fh
INT_TEMP        WORD INT_TEMP_F

                ; Codigo
                ORIG 0000h
                JMP Inicio

RESET_TEMP:     MOV R7, 10
                MOV M[TEMP], R7
                MOV R7, 1
                MOV M[TEMP_S], R7
                RET

INT_TEMP_F:     CALL RESET_TEMP
                INC R2
                RTI

Inicio:         MOV R7, SP_INICIAL
                MOV SP, R7
                MOV R7, INT_MASK
                MOV M[INT_MASK_ADDR], R7
                CALL RESET_TEMP
                ENI

Ciclo:          MOV R1, 4
                MOV R3, IO_DISPLAY
                MOV R4, R2
Ciclo2:         DEC R1
                MOV M[R3], R4
                ROR R4, 4
                INC R3
                CMP R1, R0
                BR.NZ Ciclo2
                BR Ciclo