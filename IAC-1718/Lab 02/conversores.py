def decimal_to_binary(num):
    num = int(num)
    val = ''

    while num != 0:
        val = str(num % 2) + val
        num //= 2

    return val

def binary_to_decimal(num):
    num = str(num)
    val = 0

    for i, char in enumerate(reversed(num)):
        val += int(char) * (2 ** i)

    return val

while True:
    print('What do you want to do?\n' +
          '1 - Convert Decimal to Binary\n' +
          '2 - Convert Binary to Decimal\n' +
          'C - Leave\n')

    opt = str(input("Option: "))

    if opt == 'C' or opt == 'c':
        exit()
    elif opt == '1':
        print(decimal_to_binary(input('Enter a decimal number: ')))
    elif opt == '2':
        print(binary_to_decimal(input('Enter a binary number: ')))
