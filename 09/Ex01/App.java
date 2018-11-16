/**
 * A simple application.
 */
public class App {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s1 = "batata";
		String s2 = "oo";
		String s3 = "xyz";
		Validator v = new Or(new And(new LengthGreaterThan(5),
				new LengthLesserThan(8)), new LengthEqualTo(2));
		boolean b1 = v.ok(s1); // true
		boolean b2 = v.ok(s2); // true
		boolean b3 = v.ok(s3); // false
		System.out.println("b1=" + b1 + " b2=" + b2 + " b3=" + b3);
	}

}