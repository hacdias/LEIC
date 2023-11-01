public class fibrec {
	static int fib(int n) {
		if (n < 2)
			return n;
		return fib(n - 2) + fib(n - 1);
	}
	public static void main(String[]args) {
		int n = 42;

		if (args.length > 0)
			n = Integer.parseInt(args[0]);

		System.out.println("fib(" + n + ")=" + fib(n));
	}
}
