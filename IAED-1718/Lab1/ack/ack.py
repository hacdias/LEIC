import sys
cnt = 0
def ackermann (m, n):
	global cnt
	cnt = cnt + 1
	if m == 0:
		ret = n+1
	else:
		if n == 0:
			ret = ackermann(m-1, 1)
		else:
			ret = ackermann(m-1, ackermann(m, n-1))
	return ret

sys.setrecursionlimit(100000)
print(ackermann(int(sys.argv[1]), int(sys.argv[2])))
print(" #")
print(cnt)
