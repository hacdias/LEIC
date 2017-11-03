            ORIG    8000h
VarTexto1   STR     'IAC'

            ORIG    0000h
INICIO:     MOV     R1, 'Z'
            MOV     R2, VarTexto1
            MOV     M[R2+2], R1
FIM:        BR      FIM