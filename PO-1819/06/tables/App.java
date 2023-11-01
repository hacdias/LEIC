public class App {
 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Table t = new Table(3); // table with 3 integers
 
    t.insert(0, 1);
    t.insert(1, 1);
    t.insert(2, 1);
 
		SelectionPredicate p1 = new GreaterThan(0);
 
		if (t.contains(p1))
			System.out.println("YES");
		else
			System.out.println("NO");
 
		SelectionPredicate p2 = new EqualTo(1);
 
		if (t.contains(p2))
			System.out.println("YES");
		else
			System.out.println("NO");
	}
 
}