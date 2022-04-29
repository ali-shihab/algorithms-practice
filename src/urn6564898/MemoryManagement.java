/**
 *  MemoryManagement
 */
package urn6564898;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author as02795
 *
 *         Defines memory management functions.
 * 
 */
public class MemoryManagement {

	/**
	 * LinkedList simulating physical memory, segment table and TLB.
	 */
	private LinkedList<MemoryAllocation> memory = null;
	private SegmentTable table = null;
	private static final int TLBsize = 5;

	/**
	 * Constructor.
	 * 
	 * Initialises memory with a 1000 byte block of space, 100 if which is occupied by the OS.
	 * Creates segment table/TLB.
	 * 
	 */
	public MemoryManagement() {
		this.memory = new LinkedList<MemoryAllocation>();
		this.memory.addFirst(new MemoryAllocation(0, 0, 100, 999));
		this.table = new SegmentTable();
	}

	// Getters
	public LinkedList<MemoryAllocation> getMemory() {
		return this.memory;
	}

	public SegmentTable getTable() {
		return this.table;
	}

	// TLB - linkedHashMap LRU cache - will keep used entries, and will discard
	// least recently used/created entries.
	// constructor args give a fixed size, the load factor and that it should use
	// access order rather than insertion order (access order includes insertion
	// order).
	// Maps the segment to its base address
	private Map<Segment, Integer> TLB = new LinkedHashMap<Segment, Integer>(TLBsize, 0.75f, true) {
		protected boolean removeEldestEntry(Map.Entry<Segment, Integer> eldest) {
			return size() > TLBsize;
		}
	};

	/**
	 * 
	 * Integrated allocation method.
	 * 
	 * Creates and allocates segments to memory, allocates more or less bytes in
	 * memory to existing segments, and automatically deallocates and deletes
	 * segments from memory, as well as their segment table mappings, if they reach
	 * 0 bytes in memory. Updates on whether TLB access was a hit or miss.
	 * 
	 * @param pID   the id of the process (acts as half of the logical address)
	 * @param segID the id of the segment (the other half of the logical address)
	 * @param size  the amount of bytes to be allocated or deallocated to a segment
	 * 
	 */
	public boolean allocate(int pID, int segID, int size) {
		if (pID < 1 || segID < 1) {
			throw new IllegalArgumentException("Process/Segment number cannot be less than 1");
		}
		// base variable to store the base address of the vacancy in memory
		int base = 0;
		// variable to indicate whether segment was allocated or deallocated
		boolean procedure = true;
		// if the TLB contains the entry, remove it's mapping (it will reload it later)
		Iterator<Segment> iteratorA = TLB.keySet().iterator();
		int k = 0;
		Segment entry;
		while (!TLB.keySet().isEmpty() && k == 0) {
			if (iteratorA.hasNext()) {
				entry = iteratorA.next();
				if (entry.getProcess() == pID && entry.getNum() == segID) {
					System.out.println("TLB hit.");
					TLB.remove(entry);
					k = 1;
				}
			} else {
				System.out.println("TLB miss.");
				k = 1;
			}
		}
		// if the process has not been allocated any segments before...
		if (!getTable().getProcessMap().containsKey(pID)) {
			// ...create the segment object and store in array of associated segments
			Segment seg = new Segment(pID, segID, size);
			ArrayList<Segment> segments = new ArrayList<Segment>();
			segments.add(seg);
			// create a mapping to this array from the inputed process ID
			getTable().getProcessMap().put(pID, segments);

			// if this segment has already been allocated to memory WITHOUT first being
			// added to an array in a pID-segmentArray mapping, throw exception...
			if (getTable().getBaseMap().containsKey(seg)) {
				throw new IllegalArgumentException("Somehow, this segment has already been allocated...");
			} else {
				// ...otherwise, call to assignBase function to look for vacancy in memory,
				// store
				// the base address of the vacancy, and map the segment to the address in memory
				base = assignBase(pID, segID, size);
				getTable().getBaseMap().put(seg, base);
				TLB.put(seg, (Integer) base);
			}
			// ...otherwise, if the process has already got a mapping to some segments...
		} else {
			// ...create copies of the array of segments of the process map and the
			// individual
			// segments in the key set of the base address map so as to avoid iterating over
			// a
			// changing collection/set.
			Set<Segment> copy1 = new HashSet<>(getTable().getProcessMap().get(pID));
			Set<Segment> copy2 = new HashSet<>(getTable().getBaseMap().keySet());
			List<MemoryAllocation> copy3 = new LinkedList<>(getMemory());
			// if the segment already exists in the process Id-segmentArray mapping...
			boolean match = false;
			int index = 0;
			while (index < copy1.size() && match == false) {
				Segment segment = (Segment) copy1.toArray()[index];
				if (segment.getProcess() == pID && segment.getNum() == segID) {
					match = true;
					// ...and if the same segment has already been allocated to memory...
					if (copy2.contains(segment)) {
						// ...iterate over the physical memory and find the segment in question...
						Iterator<MemoryAllocation> iterator1 = copy3.iterator();
						MemoryAllocation block;
						boolean assigned = false;
						int i = 0;
						while ((iterator1.hasNext() || i == getMemory().indexOf(getMemory().getLast()))
								&& assigned == false) {
							block = iterator1.next();
							if (block.getProcess() == pID && block.getSegment() == segID) {
								// ... and if the resulting size after modification is > 0, modify
								// the addresses, update the base address mapping and then adjust
								// the memory for any extra space left or extra space taken...
								if (block.getLimit() - block.getBase() + 1 + size > 0) {
									segment.setSize(segment.getSize() + size);
									// if deallocating bytes from segment...
									if (size < 0) {
										// form a new memory 'hole' with the remaining bytes...
										int nextLimit = getMemory().get(i).getLimit();
										getMemory().get(i).setLimit(getMemory().get(i).getLimit() + size);
										int nextBase = getMemory().get(i).getLimit() + 1;
										MemoryAllocation newSegment = new MemoryAllocation(0, 0, nextBase, nextLimit);
										getMemory().add(i + 1, newSegment);
										// update TLB
										TLB.put(segment, (Integer) newSegment.getBase());
										// if allocating bytes to existing segment...
									} else if (size > 0) {
										// if there is a big enough hole on the right, occupy those bytes..
										if (getMemory().get(i + 1).getProcess() == 0
												&& getMemory().get(i + 1).getSegment() == 0) {
											if (getMemory().get(i + 1).getLimit()
													- getMemory().get(i + 1).getBase() >= size) {
												getMemory().get(i + 1).setBase(getMemory().get(i + 1).getBase() + size);
												getMemory().get(i).setLimit(getMemory().get(i).getLimit() + size);
												// update TLB
												TLB.put(segment, (Integer) getMemory().get(i).getBase());
											}
											
											// otherwise, if there is a big enough hole on the left, occupy those
											// bytes...
										} else if (i != 0 && getMemory().get(i - 1).getProcess() == 0
												&& getMemory().get(i - 1).getSegment() == 0
												&& (getMemory().get(i - 1).getLimit()
														- getMemory().get(i - 1).getBase() >= size)) {
											getMemory().get(i - 1).setLimit(getMemory().get(i - 1).getBase() - size);
											getMemory().get(i).setBase(getMemory().get(i).getBase() - size);
											// update TLB
											TLB.put(segment, (Integer) getMemory().get(i).getBase());
											// otherwise, compact the memory and shift enough of those free bytes to the
											// right
											// of the segment in question
										} else {
											compact();
											Iterator<MemoryAllocation> iteratorX = getMemory().descendingIterator();
											boolean done = false;
											while (done == false) {
												MemoryAllocation node = iteratorX.next();
												if (node == getMemory().getLast()) {
													node.setBase(node.getBase() + size);
												} else if (!(node.getSegment() == segID && node.getProcess() == pID)) {
													node.setBase(node.getBase() + size);
													node.setLimit(node.getLimit() + size);
												} else {
													node.setLimit(node.getLimit() + size);
													//update TLB
													TLB.put(segment, (Integer) node.getBase());
													done = true;
												}
											}
											if (getMemory().getLast().getBase() > 999) {
												getMemory().remove(getMemory().getLast());
											}
										}
									}

									assigned = true;
									// ... otherwise, if the resulting size after the modification
									// is 0, remove the segment from memory, update both the process-segmentArray
									// mapping
									// and the segment-baseAddress mapping
								} else if (block.getLimit() - block.getBase() + 1 + size == 0) {
									deallocate(segment, getMemory().get(getMemory().indexOf(block)));
									assigned = true;
									procedure = false;
									// ... otherwise, if the the resulting size after modification would be less
									// than 0,
									// throw IllegalArgumentException.
								} else {
									throw new IllegalArgumentException(
											"Cannot deallocate more bytes than are already allocated");
								}
							}
							i++;
						}
						// ... otherwise, if this segment has not already been allocated to memory...
					} else {
						// ... and the size of the allocation is not a positive number of byes, throw
						// IllegalArgumentException..
						if (size <= 0) {
							throw new IllegalArgumentException(
									"Cannot assign less than 1 byte to an unassigned segment.");
							// ... otherwise, allocate to memory and update the base address mapping.
						} else {
							base = assignBase(pID, segID, size);
							getTable().getBaseMap().put(segment, base);
							// update TLB
							TLB.put(segment, (Integer) base);
						}
					}
				}
				index++;
			}
			// ...otherwise, if the segment hasn't been mapped in a process ID-segmentArray
			// mapping, create this mapping...
			if (match == false) {

				Segment seg = new Segment(pID, segID, size);
				getTable().getProcessMap().get(pID).add(seg);
				// ...and if somehow a mapping in the base table already exists for this
				// segment,
				// throw exception...
				if (copy2.contains(seg)) {
					throw new IllegalArgumentException(
							"Somehow, this segment has already been allocated without first being instantiated...");
					// otherwise, allocate to memory and update base address mapping.
				} else {
					base = assignBase(pID, segID, size);
					getTable().getBaseMap().put(seg, base);
					// update TLB
					TLB.put(seg, (Integer) base);
				}
			}
		}
		return procedure;
	}

	/**
	 * 
	 * Method to assign the specified segment to physical memory.
	 * 
	 * @param pID   Process ID
	 * @param segID Segment ID
	 * @param size  Size of allocation
	 * @return The base address of the physical segment.
	 * 
	 */
	public int assignBase(int pID, int segID, int size) {
		if (pID < 1 || segID < 1) {
			throw new IllegalArgumentException("Process/Segment number cannot be less than 1");
		}
		// variable to store the base address to be returned
		int address = 0;
		// iterate through physical memory to find a vacancy
		Iterator<MemoryAllocation> iterator1 = getMemory().iterator();
		boolean assigned = false;
		while (iterator1.hasNext() && assigned == false) {
			MemoryAllocation block = iterator1.next();
			// if a vacancy is found...
			if (block.getProcess() == 0 && block.getSegment() == 0 && ((block.getLimit() - block.getBase()) >= size)) {
				// store address of the vacancy
				address = block.getBase();
				// assign the pID and sID to that vacancy
				block.setProcess(pID);
				block.setSegment(segID);
				// store the limit to shift the resulting vacancy along the segment chain
				int nextLimit = block.getLimit();
				// set the limit of the current block to the appropriate number
				block.setLimit(block.getBase() + size - 1);
				// the base address of the vacancy (once it has been shifted) is 1 more
				// than the limit of the allocated segment
				int nextBase = block.getLimit() + 1;
				// create the vacancy at the end of the segment chain and assign the
				// appropriate values for addresses
				if (nextBase < 1000) {
				MemoryAllocation newSegment = new MemoryAllocation(0, 0, nextBase, nextLimit);
				getMemory().add(getMemory().indexOf(block) + 1, newSegment);
				assigned = true;
				}
			}
		}
		// If still not allocated to memory, compact the memory and allocate to the end.
		// Shift the remaining hole to the last point in memory.
		if (assigned == false) {
			compact();
			getMemory().getLast().setProcess(pID);
			getMemory().getLast().setSegment(segID);
			int previousLimit = getMemory().getLast().getLimit();
			int previousBase = getMemory().getLast().getBase() + size - 1;
			getMemory().getLast().setLimit(getMemory().getLast().getBase() + size - 1);
			if (previousBase < 1023) {
			MemoryAllocation newSegment = new MemoryAllocation(0, 0, previousBase + 1, previousLimit);
			getMemory().addLast(newSegment);
			}
		}
		return address;
	}

	/**
	 * 
	 * Method to deallocate and delete a segment once it's size reaches 0 (so it no
	 * longer exists) and update the segment numbers of those that are left.
	 * 
	 * @param segment The segment in logical memory
	 * @param block   The segment in physical memory
	 * 
	 */
	public void deallocate(Segment segment, MemoryAllocation block) {
		if (segment == null || block == null) {
			throw new IllegalArgumentException("Cannot deallocate a null segment/block.");
		}
		// Update all associated segments in the maps (so shift the segment
		// ID of any segments after the one being removed by -1).
		for (Segment seg : getTable().getProcessMap().get(segment.getProcess())) {
			if (getTable().getProcessMap().get(segment.getProcess()).indexOf(seg) > getTable().getProcessMap()
					.get(segment.getProcess()).indexOf(segment)) {
				seg.setNum(seg.getNum() - 1);
			}
		}

		// Update all associated segments with their new segment number
		// (so shift the segment ID of any segments after the one being
		// removed by -1).
		for (MemoryAllocation alloc : getMemory()) {
			if (alloc.getProcess() == block.getProcess() && alloc.getSegment() > block.getSegment()) {
				alloc.setSegment(alloc.getSegment() - 1);
			}
		}

		// Proceed with removal of the segment.
		block.setProcess(0);
		block.setSegment(0);
		getTable().getBaseMap().remove(segment);
		getTable().getProcessMap().get(segment.getProcess()).remove(segment);
		if (TLB.containsKey(segment)) {
			TLB.remove(segment);
		}
		segment = null;

	}

	/**
	 * Method for compaction of physical memory.
	 */
	public void compact() {

		// Create copies of physical memory to avoid concurrent
		// modification.
		List<MemoryAllocation> copy = new LinkedList<>(getMemory());
		// Iterate through the copy
		Iterator<MemoryAllocation> iterator1 = copy.iterator();
		MemoryAllocation block = iterator1.next();
		int size = 0;
		int cumulativeSize = 0;
		while (iterator1.hasNext()) {
			// remove the vacancy and store the size of it
			if (block.getProcess() == 0 && block.getSegment() == 0) {
				size = block.getLimit() - block.getBase() + 1;
				// shift the address of every block after the vacancy
				// by the appropriate amount
				for (MemoryAllocation next : getMemory()) {
					if (getMemory().indexOf(next) > getMemory().indexOf(block)) {
						next.setBase(next.getBase() - size);
						next.setLimit(next.getLimit() - size);
					}
				}
				getMemory().remove(block);
				// add it to the total size of all vacancies
				cumulativeSize += size;
			}
			block = iterator1.next();
		}
		// at the last segment, if it's a vacancy, increase it's size
		// by the accumulated amount (by shifting it's limit address)
		if (block.getProcess() == 0 && block.getSegment() == 0) {
			block.setLimit(block.getLimit() + cumulativeSize);
			// otherwise, create a vacancy with that size
		} else {
			MemoryAllocation newSpace = new MemoryAllocation(0, 0, getMemory().getLast().getLimit() + 1,
					getMemory().getLast().getLimit() + cumulativeSize);
			getMemory().add(newSpace);
		}
	}

	// toString displays the physical memory.
	@Override
	public String toString() {
		StringBuffer memory = new StringBuffer("Physical Memory:\n");
		int i = 0;
		for (MemoryAllocation segment : getMemory()) {
			i++;
			int base = segment.getBase();
			int limit = segment.getLimit();
			if (segment.getProcess() == 0 && segment.getSegment() == 0) {
				memory.append("Empty Segment " + ": Base = " + base + " Limit = " + limit + " Size = "
						+ (segment.getLimit() - segment.getBase() + 1) + "\n");
			} else {
				memory.append("Segment " + i + " - PID " + segment.getProcess() + " SID " + segment.getSegment()
						+ ": Base = " + base + " Limit = " + limit + " Size = "
						+ (segment.getLimit() - segment.getBase() + 1) + "\n");
			}
		}
		return memory.toString();
	}
}
