; Programa lab5-1.as
; ZONA I: Definicao de constantes
SP_INICIAL EQU FDFFh
; ZONA II: definicao de variaveis
; ZONA III: codigo
; conjunto de instrucoes Assembly do programa
ORIG 0000h
JMP Inicio
fatorial: CMP M[SP+2], R0 ; N==0?
BR.NZ recurs ; se não, salta
MOV R1, 1 ; valor de retorno 1
MOV M[SP+3], R1 ; escreve-o na pilha
RETN 1 ; liberta um param. entrada
recurs: MOV R1, M[SP+2] ; obtem parametro n
DEC R1 ; decrementa n
PUSH R0 ; reserva espaço p/valor ret
PUSH R1 ; coloca paramet. de entrada
CALL fatorial
POP R1 ; obtem resultado
MOV R2, M[SP+2] ; obtem N
MUL R1, R2 ; multiplica (resultado em R2)
MOV M[SP+3], R2 ; coloca result. na pilha
RETN 1 ; liberta um param. entrada
Inicio: MOV R7, SP_INICIAL
MOV SP, R7
PUSH R0
PUSH 2
CALL fatorial
POP R7
Fim: BR Fim