/**
 * SegmentTable
 */
package urn6564898;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author as02795
 *
 *         Basic model of a segment table - the segment logical address is
 *         comprised of the process ID and segment ID. The base address is
 *         mapped to in a separate map, and the limit is implicit.
 * 
 */
public class SegmentTable {

	/**
	 * @param processToSeg Maps a process to an array of its segments
	 * @param segToBase    Maps a segment to its base address in memory
	 * 
	 */
	private Map<Integer, ArrayList<Segment>> processToSeg = null;
	private Map<Segment, Integer> segToBase = null;

	/**
	 * Constructor. Forms a composite of the two maps, so as to form a 'table' with
	 * a process ID column, segments column and a base address column - the limit
	 * column is implicitly worked out using the segment's 'size' field and base
	 * address.
	 * 
	 */
	public SegmentTable() {
		this.processToSeg = new HashMap<Integer, ArrayList<Segment>>();
		this.segToBase = new HashMap<Segment, Integer>();
	}

	// Getters
	public Map<Integer, ArrayList<Segment>> getProcessMap() {
		return this.processToSeg;
	}

	public Map<Segment, Integer> getBaseMap() {
		return this.segToBase;
	}
}
