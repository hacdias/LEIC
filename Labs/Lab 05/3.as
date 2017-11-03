SP_INICIAL	EQU	FDFFh

		ORIG	8000h
Vetor		STR	2, 6, 7, 89, 45, 12, 45, 78, 45, 96
Elementos	WORD	10

		ORIG	0000h
		JMP	Inicio

Incrementa:	INC	R3
		BR	Cont

Counter:	PUSH	R1
		PUSH	R2
		PUSH	R3
		PUSH	R4
		PUSH	R5	
		MOV	R1, R0
		MOV	R2, M[SP+7]
		MOV	R3, R0

Rec:		MOV	R4, M[R2]
		MOV	R5, 3
		DIV	R4, R5
		CMP	R5, R0
		BR.Z	Incrementa

Cont:		INC	R1
		INC	R2
		CMP	R1, M[SP+8]
		BR.N	Rec
		
		MOV 	M[SP+9], R3
		POP	R5
		POP	R4
		POP	R3
		POP	R2
		POP	R1
		RETN	2 


Inicio:		MOV	R7, SP_INICIAL
		MOV	SP, R7
		PUSH	R0
		PUSH	M[Elementos]
		PUSH	Vetor
		CALL 	Counter
		POP	R7
		ADD	R7, '0'

		MOV	R1, 'N'
		MOV	M[FFFEh], R1
		MOV	R1, 'u'
		MOV	M[FFFEh], R1
		MOV	R1, 'm'
		MOV	M[FFFEh], R1
		MOV	R1, 'e'
		MOV	M[FFFEh], R1
		MOV	R1, 'r'
		MOV	M[FFFEh], R1
		MOV	R1, 'o'
		MOV	M[FFFEh], R1
		MOV	R1, ':'
		MOV	M[FFFEh], R1
		MOV	R1, ' '
		MOV	M[FFFEh], R1

		MOV	M[FFFEh], R7

Fim:		BR	Fim
		
