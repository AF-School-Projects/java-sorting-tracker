import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <br>For CS 323 - Design and Analysis of Algorithms
 * <br>
 * <br>Implements a collection of array sorting methods contained in the {@link UsesSorter} interface.
 * @author Alex Feaser
 *
 */
public class Project_01 implements UsesSorter {
	
	private static <E extends Comparable <? super E>> void execute(String type, E[] a, final int n) {
		final boolean VERBOSE = (n <= 100), FAST = (n >= 10000);
		System.out.format("%n%n  ~~~~~~~~  %8s:  %-12s  ~~~~~~~~%n%n", type, String.format("n = %d", n));
		if (VERBOSE) System.out.format("%-12s%s%n", "Unsorted: ", Arrays.deepToString(a));
		if (VERBOSE && !FAST) p("INSERTION sort: ", Sorter::insertionSort, a.clone());
		if (!FAST) p("INSERTION_K: ", Sorter::insertionSortK, a.clone(), _K());
		if (VERBOSE && !FAST) p("SELECTION sort: ", Sorter::selectionSort, a.clone());
		if (!FAST) p("SELECTION_K: ", Sorter::selectionSortK, a.clone(), _K());
		if (VERBOSE && !FAST) p("BUBBLE sort: ", Sorter::bubbleSort, a.clone());
		if (!FAST) p("BUBBLE_K: ", Sorter::bubbleSortK, a.clone(), _K());
		if (VERBOSE) p("QUICK sort: ", Sorter::quickSort, a.clone());
		p("QUICK_K: ", Sorter::quickSelectK, a.clone(), _K());
		if (VERBOSE) p("MERGE sort: ", Sorter::mergeSort, a.clone());
		p("MERGE_K: ", Sorter::mergeSortK, a.clone(), _K());
		if (VERBOSE) p("MT_MERGE sort: ", Sorter::mergeSortMulti, a.clone());
		p("MT_MERGE_K: ", Sorter::mergeSortMultiK, a.clone(), _K());
		if (VERBOSE) p("HEAP sort: ", Sorter::heapSort, a.clone());
		p("HEAP_K: ", Sorter::heapSortK, a.clone(), _K());
		p("MEDIAN_OF_MEDIANS: ", Sorter::medianOfMedians, a.clone(), _K());
		System.out.println();
	}

	private static <E extends Comparable<? super E>> void p(String msg, Function<E[], E[]> f, E[] a) {
		System.out.print(String.format("%n%-20s", msg));
		System.out.format("%-12s%s%n%n", "Result: ", Arrays.toString(f.apply(a)));
	}

	private static <E extends Comparable<? super E>> void p(String msg, BiFunction<E[], Integer, E> f, E[] a, int k) {
		System.out.print(String.format("%n%-20s", msg));
		System.out.format("%-12s%s%n%n", "Result: ", f.apply(a, k));
	}

	private static final int _K() { return Sorter.NEW_ARRAY_LEN >> 1; }

	public static void main(String[] args) {
		for (int n : new int[] {
				10, 
				100, 
				1000, 
				10000,
				100000,
				1000000,
//				10000000,
				}) {
			Sorter.NEW_ARRAY_LEN = n;
			execute("Integers", Sorter.randomize(new Integer[]{}), n);
			execute("Floats", Sorter.randomize(new Float[]{}), n);
			execute("Longs", Sorter.randomize(new Long[]{}), n);
			execute("Doubles", Sorter.randomize(new Double[]{}), n);
			execute("BigIntegers", Sorter.randomize(new BigInteger[]{}), n);
			execute("Strings", Sorter.randomize(new String[]{}), n);
			if (n < 1000000) 
				execute("Characters", Sorter.randomize(new Character[]{}), n);
		}
	}
}