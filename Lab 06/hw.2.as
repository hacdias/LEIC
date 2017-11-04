; Programa lab6-1.as
SP_INICIAL    EQU FDFFh
IO_READ       EQU FFFFh
IO_WRITE      EQU FFFEh
IO_STATUS     EQU FFFDh
IO_CONTROL    EQU FFFCh
IO_SW         EQU FFF9h
IO_LEDS       EQU FFF8h
LCD_WRITE     EQU FFF5h
LCD_CONTROL   EQU FFF4h
IO_DISPLAY    EQU FFF0h
; Codigo
              ORIG 0000h
Inicio:       MOV R7, SP_INICIAL
              MOV SP, R7
Ciclo:        MOV R2, M[IO_SW]
              ADD R2, 1
              MOV M[IO_LEDS], R2
              BR Ciclo
