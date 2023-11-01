package arabiannights;
 
/**
 * General genie. Grants a number of wishes under certain conditions.
 */
public abstract class Genie {
 
	/**
	 * Maximum number of wishes (does not mean that the genie will grant them all).
	 */
	private int _limit;
 
	/** Number of wishes granted. */
	private int _granted;
 
	/**
	 * @param limit the maximum number of wishes to grant.
	 */
	protected Genie(int limit) {
		_limit = limit;
	}
 
	/**
	 * @return the maximum number of wishes.
	 */
	public int getLimit() {
		return _limit;
	}
 
	/**
	 * @return the number of wishes already granted.
	 */
	public int getGrantedWishes() {
		return _granted;
	}
 
	/**
	 * Increment wish counter.
	 */
	public void incrementGranted() {
		_granted++;
	}
 
	/**
	 * Ask the genie to grant a wish. Never allow more grants than the initial limit.
	 * 
	 * @return true if the wish is granted; false otherwise.
	 */
	public boolean grantWish() {
		if (canGrantWish()) {
			incrementGranted();
			doGrantWish();
			return true;
		}
		return false;
	}
 
	/**
	 * Decide if genie can grant a wish.
	 * 
	 * @return true or false (depends on special circumstances).
	 */
	public boolean canGrantWish() {
		return _granted < _limit;
	}
 
	/**
	 * Do actual wish granting. By default, does not do anything. Subclasses
	 * will do something specific.
	 */
	public void doGrantWish() {
		// NOTHING TO DO
	}
 
}