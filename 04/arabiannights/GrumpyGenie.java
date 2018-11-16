package arabiannights;
 
/**
 * Behaves like any other genie, but with some differences (only one wish).
 */
public class GrumpyGenie extends Genie {
 
	/**
	 * @param limit
	 *            the number of wishes to grant (ignored: always grants only one wish).
	 */
	public GrumpyGenie(int limit) {
		super(1);
	}
 
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (getGrantedWishes() == 1) ? "Grumpy genie has granted a wish."
				: "Grumpy genie has a wish to grant.";
	}
 
}
