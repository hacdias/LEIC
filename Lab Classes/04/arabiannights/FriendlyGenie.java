package arabiannights;
 
/**
 * A friendly genie is actually a normal genie, but with special descriptive text.
 */
public class FriendlyGenie extends Genie {
 
	/**
	 * @param limit
	 *            the maximum number of wishes to grant.
	 */
	public FriendlyGenie(int limit) {
		super(limit);
	}
 
	/**
	 * @return the number of wishes left to grant.
	 */
	public int getRemainingWishes() {
		return getLimit() - getGrantedWishes();
	}
 
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Friendly genie has granted " + getGrantedWishes() + " wishes and still has "
				+ getRemainingWishes() + " to grant.";
	}
 
}
