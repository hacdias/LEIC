#include <stdio.h>

int cnt = 0;
int ackermann (int m, int n) {
	int ret;
	cnt = cnt + 1;
	if (m == 0) ret = n+1;
	else if (n == 0) ret = ackermann(m-1, 1);
	else ret = ackermann(m-1, ackermann(m, n-1));
	return ret;
}

int main(int argc, char* *argv, char* *envp) {
	if (argc > 2)
		printf("%d #%d\n", ackermann(atoi(argv[1]), atoi(argv[2])), cnt);
	return 0;
}
