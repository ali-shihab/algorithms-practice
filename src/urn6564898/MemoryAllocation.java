// Memory.
package urn6564898;

// Defines properties and behaviour of the physical memory. OS Memory takes
// addresses 0 to 99, so user space can only begin from base address 100.

public class MemoryAllocation {

	/** The process. */
	private int proc = 0;
	/** The current segment physical memory. */
	private int segment = 0;
	/** Base of the segment. */
	private int base = 100;
	/** Limit of the segment. */
	private int limit = 100;

	/**
	 * Constructor for a segment/node in the linked list
	 * 
	 * @param proc    Associated process number. 0 if vacant.
	 * @param current The occupying segment. 0 if vacant.
	 * @param base    Base address of segment.
	 * @param limit   Limit address of segment.
	 * 
	 */
	public MemoryAllocation(int proc, int segment, int base, int limit) {
		if (segment < 0 || proc < 0 || base < 100 || limit < 100 || limit > 999) {
			throw new IllegalArgumentException("Invalid arguments.");
		}
		this.proc = proc;
		this.segment = segment;
		this.base = base;
		this.limit = limit;
	}

	public int getProcess() {
		return this.proc;
	}

	public int getSegment() {
		return this.segment;
	}

	public int getBase() {
		return this.base;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setProcess(int proc) {
		if (proc < 0) {
			throw new IllegalArgumentException(
					"Cannot allocate a process number less than 1 if occupied, or 0 if empty.");
		}
		this.proc = proc;
	}

	public void setSegment(int segment) {
		if (segment < 0) {
			throw new IllegalArgumentException(
					"Cannot allocate a segment number less than 1 if occupied, or 0 if empty.");
		}
		this.segment = segment;
	}

	public void setBase(int base) {
		if (base < 100) {
			throw new IllegalArgumentException("Cannot allocate a base address at this address - this is OS Memory.");
		}
		this.base = base;
	}

	public void setLimit(int limit) {
		if (limit < 100 || limit > 999) {
			throw new IllegalArgumentException("Cannot allocate a limit less than 100 or more than 999");
		}
		this.limit = limit;
	}
}