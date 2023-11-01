public class fibiter {
	public static void main(String[]args) {
		int a = 0, b = 1, n = 42, i = n;

		if (args.length > 0)
			i = n = Integer.parseInt(args[0]);

		while (--i > 0) {
			int t = a + b;
			a = b;
			b = t;
		}

		System.out.println("fib(" + n + ")=" + b);
	}
}
