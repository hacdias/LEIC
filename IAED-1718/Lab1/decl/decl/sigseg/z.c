#include <stdio.h>

int main() {
	extern char *a;
	printf("%s\n", a);
	return 0;
}
