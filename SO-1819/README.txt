## Estrutura da Diretoria

| CircuitRouter-ParSolver   - implementação paralela do algoritmo de Lee
| CircuitRouter-SeqSolver   - implementação sequencial do algoritmo de Lee
| CircuitRouter-SimpleShell - implementação da shell
| lib                       - recursos utilizados
| results                   - resultados do speed-up

## Execução

```
# Compilar
make

# Executar diretamente 
./CircuitRouter-ParSolver/CircuitRouter-ParSolver filename -t tasksNumber

# Executar o doTests
./doTest.sh tasksNumber filename
```

## CPU Info

Architecture:          x86_64
Core(s) per socket:    4
Model name:            Intel(R) Core(TM) i7-6700HQ CPU @ 2.60GHz
CPU MHz:               2592.000

## OS Info

Linux SOMEBODY 4.4.0-17763-Microsoft #55-Microsoft Sat Oct 06 18:05:00 PST 2018 x86_64 x86_64 x86_64 GNU/Linux
