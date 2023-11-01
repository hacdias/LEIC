LETRA_S     EQU     'S'
LETRA_T     EQU     'T'
VAL1        EQU     1234 ; decimal
VAL2        EQU     1234h ; hexadecimal
LetraA      EQU     'A' ; caracter ASCII

            ORIG    0000h
INICIO:     MOV     R1, 'I'
            MOV     R1, LETRA_S
            MOV     R1, LETRA_T
            MOV     R2, VAL1
            MOV     R3, VAL2