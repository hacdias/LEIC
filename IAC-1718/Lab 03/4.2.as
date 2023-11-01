        ORIG    8000h
VarX    WORD    0
VarM    WORD    10
VarN    WORD    20
        ORIG    0000h
INICIO: MOV     R1, M[VarX]
        MOV     R2, M[VarM]
        MOV     R3, M[VarN]
        ADD     R1, R3
        ADD     R1, 40
        ADD     R1, 20
        SUB     R1, R2
FIM:    BR      FIM