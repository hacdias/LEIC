from math import sqrt

nums = []
media = 0
desvio = 0

print('Insira 5 numeros reais:')

for i in range(0, 5):
    nums.append(float(input()))

media = sum(nums) / len(nums)

for num in nums:
    desvio += (num - media) ** 2

desvio *= 1/4
desvio = sqrt(desvio)

print('A media e de', media)
print('O desvio padrao e de', desvio)
