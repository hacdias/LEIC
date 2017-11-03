print('Insira tres numeros:')

a = eval(input())
b = eval(input())
c = eval(input())

if a > b and a > c:
    print('A e o maior')
elif b > a and b > c:
    print('B e o maior')
else:
    print('C e o maior')

# OU ENTAO:
# print('Insira tres numeros:')
# 
# nums = []
# 
# for i in range(0, 3):
#     nums.append(float(input()))
# 
# print(max(nums), 'e o maior numero')
