/* Test differencies between library, declaration, include:
   - an include file uses declarations to instruct the compiler to generate correct code.
   - a library file contains the actual code to be execute as a response to library function.
 */
#include <stdio.h> /* for printf() and scanf() */
int main()
{
	double base, expon;

	printf("base = ");
	scanf("%lf", &base);

	printf("exponent = ");
	scanf("%lf", &expon);

	printf("%g^%g = %g\n", base, expon, pot(base, expon));
	return 0;
}
/*
 1) compile with:
        gcc power.c
    Error: undefined reference to `pot'
 2) add pot.c:
 	gcc power.c pot.c
    Error: undefined reference to `pow'
 3) use libm (-lm) [math library] the include the 'pow' code:
 	gcc power.c pot.c -lm
 4) run program with 2^5:
 	./a.out
	base = 2
	exponent = 5
	2^5 = (some crazy result)
 5) declare 'pot' as 'double':
    [uncomment above] double pot(double, double);
 	gcc power.c pot.c -lm
	./a.out
	base = 2
	exponent = 5
	2^5 = 32
    OK: the declaration instructed the compiler to treat arguments as 'double'
 6) replace declaration with include:
    [comment above] double pot(double, double);
    [uncomment above]: #include "pot.h"
	./a.out
	base = 2
	exponent = 5
	2^5 = 32
    OK: include files replace one or more declarations
*/
