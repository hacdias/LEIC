SP_INICIAL	EQU	FDFFh

		ORIG	8000h
Vetor		STR	2d, 6d, 7d, 89d, 45d, 12d, 45d, 78d, 45d, 96d, 51d, 52d, 45d, 12d, 12d, 10d, 8d, 7d, 5d, 3d
Elementos	WORD	20d

		ORIG	0000h
		JMP	Inicio

Counter:	PUSH	R1
		PUSH	R2
		PUSh	R3
		PUSH	R4
		MOV	R1, R0
		MOV	R2, M[SP+6]
		MOV	R3, R0

Rec:		MOV	R4, M[R2]
		NOT	R4
		AND	R4, 1
		ADD	R3, R4
		INC	R1
		INC	R2
		CMP	R1, M[SP+7]
		BR.N	Rec
		
		MOV 	M[SP+8], R3
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

Fim:		BR	Fim
		
