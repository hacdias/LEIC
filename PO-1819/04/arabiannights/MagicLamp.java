package arabiannights;
 
public class MagicLamp {
 
	/**
	 * Maximum number of wishes (does not mean that the genie will grant them all).
	 */
	private int _limit;
 
	/** Total number of rubs. */
	private int _totalRubs;
 
	/** Number of rubs since last charge. */
	private int _rubs;
 
	/** Number of recharges. */
	private int _demons;
 
	/**
	 * @param limit
	 *            how many genies can come out.
	 */
	public MagicLamp(int limit) {
		_limit = limit;
	}
 
	/**
	 * @return the maximum number of genies.
	 */
	public int getLimit() {
		return _limit;
	}
 
	/**
	 * @return how many times it was rubbed since last change.
	 */
	public int getRubs() {
		return _rubs;
	}
 
	/**
	 * @return total number of rubs.
	 */
	public int getTotalRubs() {
		return _totalRubs;
	}
 
	/**
	 * @return number of available genies.
	 */
	public int getGenies() {
		return getLimit() - getRubs();
	}
 
	/**
	 * @return number of demons.
	 */
	public int getDemons() {
		return _demons;
	}
 
	/**
	 * Ask the lamp for a genie. If the number of rubs is even, return a grumpy
	 * genie. If the number of rubs is more than the limit, return a demon.
	 * Otherwise, return a normal friendly genie.
	 * 
	 * @param wishes
	 *            maximum number of wishes the genie is able to grant (may not
	 *            be the actual number of granted wishes).
	 * 
	 * @return a genie.
	 */
	public Genie rub(int wishes) {
		if (_rubs < _limit) {
			_rubs++;
			_totalRubs++;
			if (_totalRubs % 2 == 0)
				return new FriendlyGenie(wishes);
			return new GrumpyGenie(wishes);
		}
		return new RecyclableDemon(wishes);
	}
 
	/**
	 * Recharge the magic lamp by feeding it a demon. A demon can only be used
	 * if not previously recycled.
	 * 
	 * @param demon
	 *            the recyclable demon.
	 */
	public void feedDemon(RecyclableDemon demon) {
		if (!demon.recycled()) {
			demon.recycle();
			_rubs = 0;
			_demons++;
		}
	}
 
	/**
	 * This is the default equality method inherited from class Object.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return o instanceof MagicLamp && equals((MagicLamp) o);
	}
 
	/**
	 * This is a local method (i.e., NOT INHERITED) that is only able to compare
	 * this lamp with another.
	 * 
	 * @return true if, in addition to the same capacity, the number of
	 *         available genies and the number of recharges are the same; false,
	 *         otherwise.
	 */
	public boolean equals(MagicLamp l) {
		return getLimit() == l.getLimit() && getGenies() == l.getGenies()
				&& getDemons() == l.getDemons();
	}
 
}
