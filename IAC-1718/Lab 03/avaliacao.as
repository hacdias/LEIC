        ORIG 8000h
VarX    WORD    5
VarY    TAB     1
VarW    WORD    10
        ORIG 0000h
INICIO: MOV     R1,M[VarX]
        MOV     R2,M[VarW]
        MOV     R3,2
        MOV     R4,4
        MUL     R3,R1
        MUL     R4,R2
        ADD     R1,R2
        SUB     R1,7
        MOV     M[VarY],R1
FIM:    BR      FIM