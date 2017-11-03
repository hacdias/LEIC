; Programa lab5-1.as
; ZONA I: Definicao de constantes
; ZONA II: definicao de variaveis
; Diretivas : WORD - palavra (16 bits)
ORIG 8000h
Var1 WORD 1
; ZONA III: codigo
; conjunto de instrucoes Assembly do programa
ORIG 0000h
JMP Inicio
; ConvAscii: Rotina para converter dígitos em binário em ASCII.
; Entradas: R1 - palavra com o digito a processar
; Saidas: R1 – resultado processado
; Efeitos: altera R1
ConvAscii: ADD R1, '0'
RET
Inicio: MOV R1, M[Var1]
ProcWord: CALL ConvAscii ; Converte o valor do digito em caracter ASCII
MOV M[Var1], R1
Fim: BR Fim