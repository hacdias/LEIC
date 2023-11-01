SP_INICIAL      EQU FDFFh
IO_DISPLAY      EQU FFF0h
INT_MASK_ADDR   EQU FFFAh
INT_MASK        EQU 1000000000000111b
; Tabela de interrupcoes
        ORIG FE00h
INT0    WORD INT0F ; key0
INT1    WORD INT1F ; key1
INT2    WORD INT2F ; key2
; Codigo
        ORIG 0000h
        JMP Inicio
INT0F:  ADD R2, 1
        RTI
INT1F:  ADD R2, 2
        RTI
INT2F:  ADD R2, 3
        RTI
Inicio: MOV R7, SP_INICIAL
        MOV SP, R7
        MOV R7, INT_MASK
        MOV M[INT_MASK_ADDR], R7
        ENI
        MOV R2, 0
Ciclo:  MOV M[IO_DISPLAY], R2
        BR Ciclo
