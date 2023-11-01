package arabiannights;
 
/**
 * Recyclable demon.
 */
public class RecyclableDemon extends Genie {
 
	/** Has been recycled? */
	private boolean _recycled = false;
 
	/**
	 * @param limit
	 *            the number of wishes to grant (ignored: always grants wishes).
	 */
	public RecyclableDemon(int limit) {
		super(limit);
	}
 
	/**
	 * Recyclable demons always grant wishes (unless they have been recycled).
	 * 
	 * @return true if not yet recycled; false, otherwise.
	 */
	@Override
	public boolean canGrantWish() {
		return !_recycled;
	}
 
	/**
	 * @return whether a demon has been recycled.
	 */
	public boolean recycled() {
		return _recycled;
	}
 
	/**
	 * Recycle a demon (called by a magic lamp).
	 */
	public void recycle() {
		_recycled = true;
	}
 
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _recycled ? "Demon has been recycled." : "Recyclable demon has granted "
				+ getGrantedWishes() + " wishes.";
	}
 
}