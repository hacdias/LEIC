public class ack {
	private static int cnt = 0;
	private static int ackermann (int m, int n) {
		int ret;
		cnt = cnt + 1;
		if (m == 0) ret = n+1;
		else if (n == 0) ret = ackermann(m-1, 1);
		else ret = ackermann(m-1, ackermann(m, n-1));
		return ret;
	}
	
	public static void main(String[] args) {
		if (args.length > 1)
			System.out.println(""+ackermann(Integer.parseInt(args[0]), Integer.parseInt(args[1]))+" #"+cnt);
	}
}
