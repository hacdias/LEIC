SP_INICIAL          EQU     FDFFh
OUT                 EQU     FFFEh
IO_DISPLAY          EQU     FFF0h

CRLF                EQU     000Ah               ; Line Feed
RAN_MASK            EQU     1000000000010110b
MAX_COLS            EQU     80
ATTEMPTS            EQU     12
INT_TEMP            EQU     FFF6h
INT_TEMP_CTRL       EQU     FFF7h
IO_LEDS				EQU		FFF8h
INT_MASK_ADDR       EQU     FFFAh
INT_MASK            EQU     1000010000000000b

                    ; Tabela de interrupcoes
                    ORIG    FE0Ah
INT_KEY_IA          WORD    INT_IA      		; Interruptor IA
					ORIG    FE0Fh
INT_TEMPK           WORD    INT_TEMP_F          ; TEMP


                    ORIG    8000h
Logo00              STR     '\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/'
Logo01              STR     '/|\                                                                          /|\'
Logo02              STR     '/|\            __  __           _            __  __ _           _            /|\'
Logo03              STR     '/|\           |  \/  |         | |          |  \/  (_)         | |           /|\'
Logo04              STR     '/|\           | \  / | __ _ ___| |_ ___ _ __| \  / |_ _ __   __| |           /|\'
Logo05              STR     '/|\           | |\/| |/ _  / __| __/ _ \  __| |\/| | |  _ \ / _  |           /|\'
Logo06              STR     '/|\           | |  | | (_| \__ \ ||  __/ |  | |  | | | | | | (_| |           /|\'
Logo07              STR     '/|\           |_|  |_|\__ _|___/\__\___|_|  |_|  |_|_|_| |_|\__ _|           /|\'
Logo08              STR     '/|\                                                                          /|\'
Logo09              STR     '/|\             Brought to you by Henrique Dias and Rodrigo Sousa            /|\'
Logo10              STR     '/|\                                                                          /|\'
Logo11              STR     '\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/'
TipChars            STR     'x', 'o', '-'
YouLost             STR     '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ GAME OVER! YOU LOST! ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
YouWon              STR     '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ YOU WON! ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
NewGame             STR     'New game!'
NewGameLen          WORD    9
PreviousSequence    WORD    1234h
CurrentSequence     WORD    0000h
PlayerSequence      WORD    0000h
Attempts            WORD    0000h
CounterTimer        WORD	FFFFh
TICK                WORD    0

                    ORIG    0000h
                    JMP     Start

INT_IA:             CALL    PrintNewLine        ; TODO: COmeçar novo jogo?
                    RTI

; ------------------------------------------------------------------------------------------------------------
; Gera um número pseudoaleatório através do algoritmo
; indicado no enunciado. O valor anteriormente gerado
; é enviado através do Stack.
; ------------------------------------------------------------------------------------------------------------
Random:             PUSH    R1
                    PUSH    R2
                    PUSH    R3
                    PUSH    R4
                    PUSH    R5
                    MOV     R1, M[SP+7]     ; Valor aleatório anterior
                    MOV     R2, R1
                    AND     R2, 1           ; Obtém o último dígito
                    BR.Z    RandomF
                    XOR     R1, RAN_MASK
RandomF:            ROR     R1, 1
                    MOV     R2, 0           ; Sequência aleatória c/ digitos entre 1 e 6
                    MOV     R3, 4           ; Contador de repetições
DivideLoop:         ROR     R1, 4
                    MOV     R4, R1
                    AND     R4, 000Fh
                    MOV     R5, 6
                    DIV     R4, R5
                    ADD     R2, R5
                    INC     R2
                    ROR     R2, 4
                    DEC     R3
                    BR.NZ   DivideLoop
                    MOV     M[SP+8], R2
                    MOV     M[SP+9], R1
                    POP     R5
                    POP     R4
                    POP     R3
                    POP     R2
                    POP     R1
                    RETN    1

; ------------------------------------------------------------------------------------------------------------
; Imprime uma frase.
;   Arg 1: endereço de memória da Frase
;   Arg 2: comprimento da frase
; ------------------------------------------------------------------------------------------------------------
PrintPhrase:        PUSH    R1
                    PUSH    R2
                    PUSH    R3
                    MOV     R1, R0
                    MOV     R2, M[SP+6]
PrintPhraseLoop:    MOV     R3, M[R2]
                    MOV     M[FFFEh], R3
                    INC     R1
                    INC     R2
                    CMP     R1, M[SP+5]
                    BR.N    PrintPhraseLoop
                    POP     R3
                    POP     R2
                    POP     R1
                    RETN    2

                    ; Imprime um caractere um determinado número de vezes.
PrintChar:          PUSH    R1
                    PUSH    R2
                    MOV     R1, M[SP+5]         ; R1 = caractere a imprimir
                    MOV     R2, M[SP+4]         ; R2 = número de vezes a imprimir
                    CMP     R2, R0
                    BR.Z    PrintCharEnd        ; Se for 0, não imprime nada.
PrintCharLoop:      MOV     M[OUT], R1
                    DEC     R2
                    BR.P    PrintCharLoop       ; Se continuar positivo, imprime.
PrintCharEnd:       POP     R2
                    POP     R1
                    RETN    2

                    ; Imprime uma nova linha.
PrintNewLine:       PUSH    CRLF
                    PUSH    1
                    CALL    PrintChar
                    RET

                    ; Imprime o logótipo.
PrintLogo:          PUSH    R1
                    PUSH    R2
                    MOV     R1, Logo00
                    MOV     R2, R0
PrintLogoLoop:      PUSH    R1
                    PUSH    MAX_COLS
                    CALL    PrintPhrase
                    CALL    PrintNewLine
                    ADD     R1, MAX_COLS
                    INC     R2
                    CMP     R2, 11
                    BR.NP   PrintLogoLoop
                    CALL    PrintNewLine
                    POP     R2
                    POP     R1
                    RET

; ------------------------------------------------------------------------------------------------------------
; Imprime a "dica" a dar ao jogador de acordo com a comparação
; entre a sua jogada e a sequência secreta
;
; R3  armazena a comparação e está no formato
;           0000 ---- oooo xxxx
; binário, onde:
;
;   ---- -> Dígitos não encontrados
;   oooo -> Dígitos certos na posição errada
;   xxxx -> Dígitos certos na posição certa
;
; Os dígitos não encontrados são armazenados de forma a simplificar
; a impressão, recorrendo à variável TipChars.
; ------------------------------------------------------------------------------------------------------------
PrintTip:           PUSH    R1
                    PUSH    R2
                    PUSH    R3
                    PUSH    R4
                    MOV     R1, M[SP+6]
                    MOV     R3, 2           ; Número de iterações
                    MOV     R4, TipChars    ; Localização dos caracteres
PrintTipLoop:       MOV     R2, R1
                    AND     R2, 000Fh
                    PUSH    M[R4]
                    PUSH    R2
                    CALL    PrintChar
                    ROR     R1, 4
                    INC     R4
                    DEC     R3
                    BR.NN   PrintTipLoop
                    CALL    PrintNewLine
                    POP     R4
                    POP     R3
                    POP     R2
                    POP     R1
                    RET

; ------------------------------------------------------------------------------------------------------------
; Compara a jogada com a sequência secreta.
; ------------------------------------------------------------------------------------------------------------
Compare:            PUSH    R1
                    PUSH    R2
                    PUSH    R3
                    PUSH    R4
                    PUSH    R5
                    PUSH    R6
                    PUSH    R7
                    MOV     R1, M[SP+10]     ; Seq Secreta
                    MOV     R2, M[SP+9]	     ; Seq Jogador
                    MOV     R3, R0     	     ; Semelhança

                    ; Averigua quais os dígitos que estão na posição
                    ; correta.
RightPos:           MOV     R4, 4            ; Repetições
RightPosLoop:       CMP     R4, 0
                    BR.Z    WrongPos
                    ROR     R1, 4
                    ROR     R2, 4
                    MOV     R5, R1
                    MOV     R6, R2
                    AND     R5, 000Fh
                    AND     R6, 000Fh
                    CMP     R5, R6
                    BR.NZ   RightPosEnd
IncRighPos:         INC     R3
                    AND     R1, FFF0h
                    AND     R2, FFF0h
RightPosEnd:        DEC     R4
                    BR.NZ   RightPosLoop

                    ; Averigua quais os dígitos que estão na posição
                    ; errada.
WrongPos:           MOV     R5, 4           ; Repetições do ciclo exterior
WrongPosLoop:       DEC     R5
                    CMP     R5, R0
                    BR.N    CompareEnd
                    MOV     R4, 4           ; Repetições do ciclo interior
                    ROR     R2, 4
WrongPosInnerLoop:  ROR     R1, 4
                    MOV     R6, R1
                    MOV     R7, R2
                    AND     R6, 000Fh
                    AND     R7, 000Fh
                    CMP     R6, R7
                    BR.Z    IncWrongPos
                    BR      WrongPosEnd
IncWrongPos:        CMP     R6, R0
                    BR.Z    WrongPosEnd
                    CMP     R7, R0
                    BR.Z    WrongPosEnd
                    ADD     R3, 0010h
                    AND     R1, FFF0h
                    AND     R2, FFF0h
                    BR      WrongPosEnd
WrongPosEnd:        DEC     R4
                    CMP     R4, R0
                    BR.Z    WrongPosLoop
                    BR      WrongPosInnerLoop

CompareEnd:         MOV     R6, 4
                    MOV     R7, R3
                    AND     R7, 000Fh
                    SUB     R6, R7
                    MOV     R7, R3
                    ROR     R7, 4
                    AND     R7, 000Fh
                    SUB     R6, R7
                    ROL     R6, 8
                    ADD     R3, R6
                    MOV     M[SP+11], R3
                    POP     R7
                    POP     R6
                    POP     R5
                    POP     R4
                    POP     R3
                    POP     R2
                    POP     R1
                    RETN    2

CleanCounter:       PUSH    R1
                    MOV     M[Attempts], R0
                    MOV     R1, IO_DISPLAY
                    MOV     M[R1], R0
                    MOV     M[R1+1], R0
                    POP     R1
                    RET

UpdateCounter:      PUSH    R1
                    PUSH    R2
                    PUSH    R3
                    MOV     R1, M[Attempts]
                    CMP     R1, 10
                    BR.N    UpdateCounterEnd
                    SUB     R1, 10
                    MOV     R2, 1
                    MOV     R3, IO_DISPLAY
                    MOV     M[R3+1], R2
UpdateCounterEnd:   MOV     M[IO_DISPLAY], R1
                    POP     R3
                    POP     R2
                    POP     R1
                    RET

; ------------------------------------------------------------------------------------------------------------
; Interrupções controlando o temporizador e introdução
; das tentativas.
; ------------------------------------------------------------------------------------------------------------
Reset_LEDS:		PUSH	R1
				MOV		R1, FFFFh
				MOV		M[CounterTimer], R1
				POP		R1
				RET

Reset_Temp:     PUSH    R1
                MOV     R1, 5
                MOV     M[INT_TEMP], R1
                MOV     R1, 1
                MOV     M[INT_TEMP_CTRL], R1
                POP     R1
                RET

INT_TEMP_F:     CALL    Reset_Temp
                PUSH    R1
                SHL     M[CounterTimer], 1
                INC     M[TICK]
                POP     R1
                RTI

Update_LEDS:    PUSH    R1
                DEC     M[TICK]
				MOV		R1, M[CounterTimer]
				MOV		M[IO_LEDS], R1
                POP     R1
                RET


; ------------------------------------------------------------------------------------------------------------
; Lógica principal do jogo. Gera uma sequência
; aleatória sempre que começa um novo jogo e lê
; repetidamente a jogada do jogador.
; ------------------------------------------------------------------------------------------------------------
Game:               CALL    PrintNewLine
                    PUSH    NewGame
                    PUSH    M[NewGameLen]
                    CALL    PrintPhrase
                    CALL    PrintNewLine
                    PUSH    R0
                    PUSH    R0
                    PUSH    M[PreviousSequence]     ; Sequência anterior
                    CALL    Random
                    POP     M[CurrentSequence]      ; Sequência aleatória
                    POP     M[PreviousSequence]     ; Sequência aleatória raw
                    MOV     M[PlayerSequence], R0   ; Jogada do Jogador = 0
                    CALL    CleanCounter
					CALL	Reset_LEDS
					CALL	Reset_Temp

GameLoop:           CMP     M[TICK], R0				; Temporizador foi chamada
					CALL.NZ Update_LEDS				; Update dos Leds
					MOV		R1, M[CounterTimer]
					CMP		R1, R0
					JMP.Z	Lost
					CMP     R2, R0
                    BR.Z    GameLoop
					MOV     M[PlayerSequence], R2
                    MOV     R2, R0
                    CMP     M[PlayerSequence], R0
                    BR.Z    GameLoop                ; Esperar pela introdução da jogada
                    MOV     R1, M[CurrentSequence]
                    CMP     R1, M[PlayerSequence]
                    BR.Z    Won
                    PUSH    R0
                    PUSH    M[CurrentSequence]      ; Seq Secreta
                    PUSH    M[PlayerSequence]       ; Seq do Jogador
                    CALL    Compare                 ; Compara números
                    CALL    PrintTip
                    INC     M[Attempts]
                    CALL    UpdateCounter
                    MOV     R1, M[Attempts]
                    CMP     R1, ATTEMPTS
                    JMP.Z   Lost
                    MOV     M[PlayerSequence], R0
					CALL	Reset_LEDS
                    JMP     GameLoop

                    ; Imprimir mensagem de vitória.
Won:                PUSH    YouWon
                    PUSH    MAX_COLS
                    CALL    PrintPhrase
                    CALL    PrintNewLine
                    JMP     Game

                    ; Imprimir mensagem de derrota.
Lost:               PUSH    YouLost
                    PUSH    MAX_COLS
                    CALL    PrintPhrase
                    CALL    PrintNewLine
                    JMP     Game

; ------------------------------------------------------------------------------------------------------------
; Início do programa.
; ------------------------------------------------------------------------------------------------------------
Start:              MOV     R7, SP_INICIAL
                    MOV     SP, R7
					MOV		R7, INT_MASK
					MOV		M[INT_MASK_ADDR], R7
					ENI
                    CALL    PrintLogo
                    POP     R6
                    POP     R7
                    JMP     Game