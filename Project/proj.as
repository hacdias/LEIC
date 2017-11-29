SP_INICIAL          EQU     FDFFh
OUT_CTRL            EQU     FFFCh
OUT                 EQU     FFFEh
IO_DISPLAY          EQU     FFF0h

RAN_MASK            EQU     1000000000010110b
MAX_COLS            EQU     78
ATTEMPTS            EQU     12
CTRL_LCD            EQU     FFF4h
IO_LCD              EQU     FFF5h
INT_TEMP            EQU     FFF6h
INT_TEMP_CTRL       EQU     FFF7h
IO_LEDS             EQU     FFF8h
INT_MASK_ADDR       EQU     FFFAh
INT_MASK            EQU     1000010001111110b

                    ; Tabela de interrupcoes
                    ORIG    FE01h
INT_KEY_I1          WORD    INT_I1              ; Interruptor I1
INT_KEY_I2          WORD    INT_I2              ; Interruptor I2
INT_KEY_I3          WORD    INT_I3              ; Interruptor I3
INT_KEY_I4          WORD    INT_I4              ; Interruptor I4
INT_KEY_I5          WORD    INT_I5              ; Interruptor I5
INT_KEY_I6          WORD    INT_I6              ; Interruptor I6
                    ORIG    FE0Ah
INT_KEY_IA          WORD    INT_IA              ; Interruptor IA
                    ORIG    FE0Fh
INT_TEMPK           WORD    INT_TEMP_F          ; TEMP

                    ORIG    8000h
Logo00              STR     '/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/\|/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\'
Logo01              STR     '/|\                                                                        /|\'
Logo02              STR     '/|\           __  __           _            __  __ _           _           /|\'
Logo03              STR     '/|\          |  \/  |         | |          |  \/  (_)         | |          /|\'
Logo04              STR     '/|\          | \  / | __ _ ___| |_ ___ _ __| \  / |_ _ __   __| |          /|\'
Logo05              STR     '/|\          | |\/| |/ _  / __| __/ _ \  __| |\/| | |  _ \ / _  |          /|\'
Logo06              STR     '/|\          | |  | | (_| \__ \ ||  __/ |  | |  | | | | | | (_| |          /|\'
Logo07              STR     '/|\          |_|  |_|\__ _|___/\__\___|_|  |_|  |_|_|_| |_|\__ _|          /|\'
Logo08              STR     '/|\                                                                        /|\'
Logo09              STR     '/|\            Brought to you by Henrique Dias and Rodrigo Sousa           /|\'
Logo10              STR     '/|\                                                                        /|\'
Logo11              STR     '/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/\|/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\'
Logo12              STR     '/|\                                                                        /|\'
Logo13              STR     '/|\                   Carregue no botao IA para iniciar!                   /|\'
Logo14              STR     '/|\                                                                        /|\'
Logo15              STR     '/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/\|/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\/|\'
TipChars            STR     'x', 'o', '-'
YouLost             STR     '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ PERDESTE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
YouWon              STR     '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ GANHASTE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
EndGamePhrase       STR     '~~~~~~~~~~~~~~~~~ FIM DO JOGO! Clique em IA para recomecar! ~~~~~~~~~~~~~~~~~~'
NewGame             STR     '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ NOVO JOGO ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
MelhorPont          STR     'Melhor Pont: NA'
MelhorPontLen       WORD    1000000000001110b
PreviousSequence    WORD    1234h
CurrentSequence     WORD    0000h
PlayerSequence      WORD    0000h
Attempts            WORD    0000h
CounterTimer        WORD    FFFFh
BestGame            WORD    000Fh      
StartGame           WORD    0
TICK                WORD    0
Cursor              WORD    0
Digit               WORD    4
GuessInput          WORD    0000h
InfinityCounter     WORD    0

                    ORIG    0000h
                    JMP     Start

INT_I1:             PUSH    0001h
                    CALL    AppendDigit
                    RTI
INT_I2:             PUSH    0002h
                    CALL    AppendDigit
                    RTI
INT_I3:             PUSH    0003h
                    CALL    AppendDigit
                    RTI
INT_I4:             PUSH    0004h
                    CALL    AppendDigit
                    RTI
INT_I5:             PUSH    0005h
                    CALL    AppendDigit
                    RTI
INT_I6:             PUSH    0006h
                    CALL    AppendDigit
                    RTI
INT_IA:             INC     M[StartGame]    ; Indicador de Início de Novo Jogo
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
                    MOV     R1, M[SP+7]             ; Valor aleatório anterior
                    CMP     R1, R0                  ; Se não existir um valor anterior (1º jogo)
                    BR.NZ   RandomContinue          ; utiliza um contador existente no loop inicial do jogo
                    MOV     R1, M[InfinityCounter]
RandomContinue:     MOV     R2, R1
                    AND     R2, 1                   ; Obtém o último dígito
                    BR.Z    RandomF
                    XOR     R1, RAN_MASK
RandomF:            ROR     R1, 1
                    MOV     R2, 0                   ; Sequência aleatória c/ digitos entre 1 e 6
                    MOV     R3, 4                   ; Contador de repetições
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
NextCol:            PUSH    R1
                    MOV     R1, M[Cursor]
                    INC     R1
                    MOV     M[Cursor], R1
                    MOV     M[OUT_CTRL], R1
                    POP     R1
                    RET

                    ; Imprime uma nova linha.
NextLine:           PUSH    R1
                    MOV     R1, M[Cursor]
                    AND     R1, FF00h
                    ADD     R1, 0100h
                    MOV     M[Cursor], R1
                    MOV     M[OUT_CTRL], R1
                    POP     R1
                    RET

PrintPhrase:        PUSH    R1
                    PUSH    R2
                    PUSH    R3
                    MOV     R1, R0
                    MOV     R2, M[SP+6]
PrintPhraseLoop:    MOV     R3, M[R2]
                    PUSH    R3
                    PUSH    1
                    CALL    PrintChar
                    INC     R1
                    INC     R2
                    CMP     R1, M[SP+5]
                    BR.N    PrintPhraseLoop
                    POP     R3
                    POP     R2
                    POP     R1
                    RETN    2

CleanWindow:        PUSH    R1
                    PUSH    R2
                    PUSH    R3
                    MOV     R1, 0               ; Contagem Linhas
                    MOV     R3, ' '             ; Espaço Branco
                    MOV     M[Cursor], R0       ; Reset Cursor
                    MOV     M[OUT_CTRL], R0     ; Reset Cursor
CleanWindowLiLoop:  MOV     R2, 0               ; Contagem Colunas
CleanWindowColLoop: MOV     M[OUT], R3
                    CALL    NextCol
                    INC     R2
                    CMP     R2, 80
                    BR.N    CleanWindowColLoop
                    CALL    NextLine
                    INC     R1
                    CMP     R1, 24
                    BR.N    CleanWindowLiLoop
                    MOV     M[Cursor], R0       ; Reset Cursor
                    MOV     M[OUT_CTRL], R0     ; Reset Cursor
                    POP     R3
                    POP     R2
                    POP     R1
                    RET

; ------------------------------------------------------------------------------------------------------------
; Imprime um caractere um determinado número de vezes.
; ------------------------------------------------------------------------------------------------------------
PrintChar:          PUSH    R1
                    PUSH    R2
                    MOV     R1, M[SP+5]         ; R1 = caractere a imprimir
                    MOV     R2, M[SP+4]         ; R2 = número de vezes a imprimir
                    CMP     R2, R0
                    BR.Z    PrintCharEnd        ; Se for 0, não imprime nada.
PrintCharLoop:      MOV     M[OUT], R1
                    CALL    NextCol
                    DEC     R2
                    BR.P    PrintCharLoop       ; Se continuar positivo, imprime.
PrintCharEnd:       POP     R2
                    POP     R1
                    RETN    2

; ------------------------------------------------------------------------------------------------------------
; Imprime o logótipo.
; ------------------------------------------------------------------------------------------------------------
PrintLogo:          PUSH    R1
                    PUSH    R2
                    MOV     R1, Logo00
                    MOV     R2, R0
PrintLogoLoop:      PUSH    R1
                    PUSH    MAX_COLS
                    CALL    PrintPhrase
                    CALL    NextLine
                    ADD     R1, MAX_COLS
                    INC     R2
                    CMP     R2, 15
                    BR.NP   PrintLogoLoop
                    CALL    NextLine
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
                    CALL    NextLine
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

; ------------------------------------------------------------------------------------------------------------
; Limpa o contador de tentativas
; ------------------------------------------------------------------------------------------------------------
CleanCounter:       PUSH    R1
                    MOV     M[Attempts], R0
                    MOV     R1, IO_DISPLAY
                    MOV     M[R1], R0
                    MOV     M[R1+1], R0
                    POP     R1
                    RET

; ------------------------------------------------------------------------------------------------------------
; Atualiza o contador de tentativas
; ------------------------------------------------------------------------------------------------------------
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
; Faz reset aos LEDS de forma a estarem todos ligados
; ------------------------------------------------------------------------------------------------------------
ResetLEDS:          PUSH    R1
                    MOV     R1, FFFFh
                    MOV     M[CounterTimer], R1
                    POP     R1
                    RET

; ------------------------------------------------------------------------------------------------------------
; Faz reset ao Temporizador
; ------------------------------------------------------------------------------------------------------------
ResetTemp:          PUSH    R1
                    MOV     R1, 5
                    MOV     M[INT_TEMP], R1
                    MOV     R1, 1
                    MOV     M[INT_TEMP_CTRL], R1
                    POP     R1
                    RET

; ------------------------------------------------------------------------------------------------------------
; Rotina de Tratamento da Interrupção do Temporizador
; ------------------------------------------------------------------------------------------------------------
INT_TEMP_F:         CALL    ResetTemp
                    PUSH    R1
                    SHL     M[CounterTimer], 1
                    INC     M[TICK]
                    POP     R1
                    RTI

; ------------------------------------------------------------------------------------------------------------
; Atualiza os LEDS de acordo com o temporizador
; ------------------------------------------------------------------------------------------------------------
UpdateLEDS:         PUSH    R1
                    DEC     M[TICK]
                    MOV     R1, M[CounterTimer]
                    MOV     M[IO_LEDS], R1
                    POP     R1
                    RET

; ------------------------------------------------------------------------------------------------------------
; Imprime a frase "Melhor Pont:" para a placa de texto
; ------------------------------------------------------------------------------------------------------------
PrintBestGame:      PUSH    R1
                    PUSH    R2
                    PUSH    R3
                    MOV     R1, MelhorPont
                    MOV     R2, 1000000000000000b
PrintBestGameL:     MOV     M[CTRL_LCD], R2
                    MOV     R3, M[R1]
                    MOV     M[IO_LCD], R3
                    INC     R1
                    INC     R2
                    CMP     R2, M[MelhorPontLen]
                    BR.NP   PrintBestGameL
                    POP     R3
                    POP     R2
                    POP     R1
                    RET

; ------------------------------------------------------------------------------------------------------------
; Atualiza a melhor pontuação na placa de texto
; ------------------------------------------------------------------------------------------------------------
UpdateBestGame:     PUSH    R1
                    PUSH    R2
                    PUSH    R3
                    MOV     R1, M[Attempts]
                    INC     R1
                    MOV     R2, M[BestGame]
                    CMP     R1, R2
                    BR.P    UpdateBestGameEnd  
                    MOV     M[BestGame], R1
                    MOV     R2, R1
                    CMP     R1, 10                      
                    BR.NN   UpdateBestGameBig       ; Caso seja uma melhor pontuacao e esta seja constituida por 2 digitos
                    MOV     R1, R0                  ; Caso esta seja constituida por 1 digito o primeiro digito é zero
                    BR      UpdateBestGameDo
UpdateBestGameBig:  MOV     R1, 1                   ; Caso esta seja constituida por 2 digitos o primeiro digito é 1
                    SUB     R2, 10                  ; Subtraimos 10 a pontuacao de forma a obter o segundo digito
UpdateBestGameDo:   ADD     R1, 48                  ; Conversão do primeiro digito para ASCII
                    ADD     R2, 48                  ; Conversão do segundo digito para ASCII        
                    MOV     R3, 1000000000001101b
                    MOV     M[CTRL_LCD], R3
                    MOV     M[IO_LCD], R1           ; Dígito da esquerda
                    MOV     R3, 1000000000001110b
                    MOV     M[CTRL_LCD], R3
                    MOV     M[IO_LCD], R2           ; Dígito da direita
UpdateBestGameEnd:  POP     R3
                    POP     R2
                    POP     R1
                    RET

; ------------------------------------------------------------------------------------------------------------
; Atualiza a tentativa do jogador quando carrega nos botoes
; ------------------------------------------------------------------------------------------------------------
AppendDigit:        PUSH    R1
                    PUSH    R2
                    MOV     R1, M[SP+4]
                    MOV     R2, M[GuessInput]
                    ROL     R2, 4
                    ADD     R2, R1                  ; Adicionado o valor associado ao botao a variavel GuessInput
                    MOV     M[GuessInput], R2       ; onde é guardada a informação sobre a tentativa que o jogador está a criar
                    DEC     M[Digit]                ; Decrementar o numero de digitos por introduzir
                    POP     R2
                    POP     R1
                    RETN    1
                    
TransferGuess:      PUSH    R1
                    MOV     R1, M[GuessInput]       ; Cópia do valor apos o jogador ter clicado nos 4 botões para
                    MOV     M[PlayerSequence], R1   ; o endereço de memória associado a sequência do jogador
                    MOV     R1, 4
                    MOV     M[Digit], R1
                    MOV     M[GuessInput], R0
                    POP     R1
                    RET
                    
; ------------------------------------------------------------------------------------------------------------
; Lógica principal do jogo. Gera uma sequência
; aleatória sempre que começa um novo jogo e lê
; repetidamente a jogada do jogador.
; ------------------------------------------------------------------------------------------------------------
Game:               MOV     M[StartGame], R0
                    CALL    ResetLEDS
                    CALL    CleanWindow
                    PUSH    NewGame
                    PUSH    MAX_COLS
                    CALL    PrintPhrase
                    CALL    NextLine
                    PUSH    R0
                    PUSH    R0
                    PUSH    M[PreviousSequence]     ; Sequência anterior
                    CALL    Random
                    POP     M[CurrentSequence]      ; Sequência aleatória
                    POP     M[PreviousSequence]     ; Sequência aleatória raw
                    MOV     R1, 4
                    MOV     M[Digit], R1
                    MOV     M[GuessInput], R0       ; Guess Input = 0
                    MOV     M[PlayerSequence], R0   ; Jogada do Jogador = 0
                    CALL    CleanCounter
                    CALL    ResetTemp

GameLoop:           CMP     M[StartGame], R0
                    JMP.P   Game
                    CMP     M[TICK], R0             ; Temporizador foi chamado
                    CALL.NZ UpdateLEDS              ; Update dos Leds caso haja um update to tick do temporizador  
                    CMP     M[CounterTimer], R0     ; Se o tempo chegar a zero, o jogador perde o jogo
                    JMP.Z   Lost
                    CMP     M[Digit], R0
                    CALL.Z  TransferGuess
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
                    CALL    ResetLEDS
                    CALL    ResetTemp
                    JMP     GameLoop

; ------------------------------------------------------------------------------------------------------------
; Imprime mensagem de vitória ou derrota, atualiza o melhor jogo
; e limpa o contador de jogadas atual.
; ------------------------------------------------------------------------------------------------------------
Won:                CALL    UpdateBestGame
                    PUSH    YouWon
                    BR      GameEnd
Lost:               PUSH    YouLost
GameEnd:            PUSH    MAX_COLS
                    CALL    PrintPhrase
                    CALL    NextLine
                    MOV     M[Attempts], R0
                    CALL    CleanCounter
                    PUSH    EndGamePhrase
                    PUSH    MAX_COLS
                    CALL    PrintPhrase
                    JMP     Infinity

; ------------------------------------------------------------------------------------------------------------
; Início do programa.
; ------------------------------------------------------------------------------------------------------------
Start:              MOV     R7, SP_INICIAL
                    MOV     SP, R7
                    MOV     R7, INT_MASK
                    MOV     M[INT_MASK_ADDR], R7
                    MOV     R7, FFFFh
                    MOV     M[OUT_CTRL], R7
                    ENI
                    CALL    CleanWindow
                    CALL    PrintLogo
                    MOV     M[CTRL_LCD], R0
                    MOV     R7, 1000000000100000b
                    MOV     M[CTRL_LCD], R7
                    CALL    PrintBestGame

Infinity:           MOV     M[IO_LEDS], R0
                    INC     M[InfinityCounter]
                    CMP     M[StartGame], R0
                    JMP.P   Game
                    BR      Infinity
