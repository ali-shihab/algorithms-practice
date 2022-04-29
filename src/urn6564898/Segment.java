/**
 * Segment
 */
package urn6564898;

/**
 * Defines the basic properties of a segment.
 */
public class Segment {

	/** Process number. */
	private int pID = 0;
	/** Segment number. */
	private int sID = 0;
	/** Size of the segment in bytes. */
	private int size = 0;

	/**
	 * 
	 * Constructor.
	 *
	 * @param pID  Associated process number.
	 * @param sID  Segment number.
	 * @param size Size of segment.
	 *
	 */
	public Segment(int pID, int sID, int size) {

		if (pID < 1 || sID < 1) {
			throw new IllegalArgumentException("Process/Segment number cannot be less than 1");
		}
		this.pID = pID;
		this.sID = sID;
		this.size = size;
	}

	// Getters
	public int getNum() {
		return this.sID;
	}

	public int getSize() {
		return this.size;
	}

	public int getProcess() {
		return this.pID;
	}

	// Setters
	public void setNum(int sID) {
		if (sID < 1) {
			throw new IllegalArgumentException("Segment number cannot be less than 1");
		}
		this.sID = sID;
	}

	public void setProcess(int pID) {
		if (pID < 1) {
			throw new IllegalArgumentException("Process number cannot be less than 1");
		}
		this.pID = pID;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
