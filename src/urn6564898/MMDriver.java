// MMDRiver
package urn6564898;

/**
* @author as02795
*
*         Main method for memory management. 
*         
*         NOTE: Examples of allocation/deallocation,
*         		 compaction, and the TLB working are
*         		 integrated because they are not coded
*         		 independently, i.e. they can only be
*         		 executed when all other elements are
*         		 executed, and when necessary. 
*/
public class MMDriver {

	private final static int MAIN = 1000;
	private final static int OS = 100;
	private final static int TOTAL = MAIN - OS;
	private static int memoryUsed = 0;

	private static void allocate(int[] a, MemoryManagement memory) {
		int count = 0;
		boolean procedure = true;
		for (int i = 1; i < a.length; i++) {
			if (procedure == false) {
				count++;
			}
			if (a.length < 2) {
				throw new IllegalArgumentException(
						"Input must be an array in the format [pID, size1, size2, ...]");
			}
			if (a[0] < 1) {
				throw new IllegalArgumentException("pID must be greator than 0");
			}
			if (a[i] > (TOTAL - memoryUsed)) {
				throw new OutOfMemoryError(
						"Not enough memory for segment SID " + (i - count) + " of PID " + a[0]);
			}
			procedure = memory.allocate(a[0], i - count, a[i]);
			memoryUsed += a[i];
			System.out.println(memory.toString());
		}
	}

	// Examples
	public static void main(String[] args) {

		MemoryManagement memory = new MemoryManagement();
		
		/**
		 
		// Start Example A.1
		
		System.out.println("Start Example A.1");
		System.out.println("Main Memory: " + MAIN + " OS Memory: " + OS + " Used: " + memoryUsed + "\n");
		int[] p1a = { 1, 100, 200, 300 };
		allocate(p1a, memory);
		int[] p1b = { 1, -100, 200, -300 };
		allocate(p1b, memory);
		int[] p2a = { 2, 25, 100 };
		allocate(p2a, memory);
		int[] p1c = { 1, -50 };
		allocate(p1c, memory);
		int[] p3a = { 3, 100, 200 };
		allocate(p3a, memory);
		System.out.println("Main Memory: " + MAIN + " OS Memory: " + OS + " Used: " + memoryUsed + "\n");
		System.out.println("End Example A.1\n");
		
		/**
		 
		// Start Example A.2.5
		
		System.out.println("Start Example A.2.5");
		System.out.println("TLB code A.2.5 File: MemoryManagement, Lines: 86 to 101\n");
		System.out.println("Main Memory: " + MAIN + " OS Memory: " + OS + " Used: " + memoryUsed + "\n");
		int[] p2b = { 2, 0, -100, 50 };
		allocate(p2b, memory);
		int[] p4a = { 4, 50, 80, 40 };
		allocate(p4a, memory);
		int[] p1d = { 1, -325 };
		allocate(p1d, memory);
		int[] p3b = { 3, -75, -175, 10, 15 };
		allocate(p3b, memory);
		int[] p5a = { 5, 1, 2, 3, 4, 5 };
		allocate(p5a, memory);
		System.out.println("Main Memory: " + MAIN + " OS Memory: " + OS + " Used: " + memoryUsed + "\n");
		System.out.println("End Example A.2.5\n");
		
		**/
		
		/**
		 
		// Start Example A.2.6 - Personal Example
		 
		System.out.println("Start Example A.2.6 - Personal Example");
		System.out.println("Compaction code A.2.6 File: MemoryManagement, Lines: 374 to 414\n");
		System.out.println("Main Memory: " + MAIN + " OS Memory: " + OS + " Used: " + memoryUsed + "\n");
		int[] p5b = { 5, 29, 18, 7, 16, 15 };
		allocate(p5b, memory);
		int[] p4b = { 4, -25, -70, -35 };
		allocate(p4b, memory);
		int[] p1e = { 1, -20, 10, 20 };
		allocate(p1e, memory);
		int[] p5c = { 5, -5 };
		allocate(p5c, memory);
		int[] p6a = { 6, 30, 60, 20, 40} ;
		allocate(p6a, memory);
		System.out.println("Main Memory: " + MAIN + " OS Memory: " + OS + " Used: " + memoryUsed + "\n");
		System.out.println("End Example A.2.6 - Personal Example\n");
		
		**/
		
		
		 
		//  Start Example A.2.6 - The Given Example *PLEASE COMMENT OUT THE PREVIOUS EXAMPLES WHEN USING THIS
		
		System.out.println("Start Example A.2.6 - The Given Example");
		System.out.println("Compaction code A.2.6 File: MemoryManagement, Lines: 374 to 414\n");
		System.out.println("Main Memory: " + MAIN + " OS Memory: " + OS + " Used: " + memoryUsed + "\n");
		int[] p1a = { 1, 100, 200, 300 };
		allocate(p1a, memory);
		int[] p2a = { 2, 100 };
		allocate(p2a, memory);
		int[] p1b = { 1, -100, 0, -100 };
		allocate(p1b, memory);
		int[] p3a = { 150, 250 };
		allocate(p3a, memory);
		System.out.println("Main Memory: " + MAIN + " OS Memory: " + OS + " Used: " + memoryUsed + "\n");
		System.out.println("End Example A.2.6\n");
		
		
		
	}
}
