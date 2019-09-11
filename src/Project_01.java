import java.util.Arrays;

/**
 * <br>For CS 323 - Design and Analysis of Algorithms
 * <br>
 * <br>Implements a collection of array sorting methods contained in the {@link UsesSorter} interface.
 * @author Alex Feaser
 *
 */
public class Project_01 implements UsesSorter {

	private static <E extends Comparable<? super E>> void p(String msg, E[] a) {
		System.out.format("%-24s%s%n", msg, Arrays.toString(a));
	}

	private static <E extends Comparable<? super E>> void p(String msg, E a) {
		System.out.format("%-24s%s%n", msg, a);
	}
	
	private static final int _K() {
		return Sorter.NEW_ARRAY_LEN >> 1;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		
		final int n = 10;
		Sorter.NEW_ARRAY_LEN = n;
		final boolean VERBOSE = false, FAST = true;
		
		Comparable[] ints;
		ints = Sorter.randomize(new Integer[]{});
		p("Before sorting: ", ints); System.out.println();
		if (VERBOSE && !FAST) p("Using INSERTION sort: ", Sorter.insertionSort(ints.clone()));
		if (!FAST) p("Using INSERTION_K: ", Sorter.insertionSortK(ints.clone(), _K()));
		if (VERBOSE && !FAST) p("Using SELECTION sort: ", Sorter.selectionSort(ints.clone()));
		if (!FAST) p("Using SELECTION_K: ", Sorter.selectionSortK(ints.clone(), _K()));
		if (VERBOSE && !FAST) p("Using BUBBLE sort: ", Sorter.bubbleSort(ints.clone()));
		if (!FAST) p("Using BUBBLE_K: ", Sorter.bubbleSortK(ints.clone(), _K()));
		if (VERBOSE) p("Using QUICK sort: ", Sorter.quickSort(ints.clone()));
		p("Using QUICK_K: ", Sorter.quickSortK(ints.clone(), _K()));
		if (VERBOSE) p("Using MERGE sort: ", Sorter.mergeSort(ints.clone(), false));
		p("Using MERGE_K: ", Sorter.mergeSortK(ints.clone(), false, _K()));
		if (VERBOSE) p("Using MT_MERGE sort: ", Sorter.mergeSort(ints.clone(), true));
		p("Using MT_MERGE_K: ", Sorter.mergeSortK(ints.clone(), true, _K()));
		if (VERBOSE) p("Using HEAP sort: ", Sorter.heapSort(ints.clone()));
		p("Using HEAP_K: ", Sorter.heapSortK(ints.clone(), _K()));
		System.out.println();
		
		Sorter.NEW_ARRAY_LEN = n << 2;
		Comparable[] chars;
		chars = Sorter.randomize(new Character[]{});
		p("Before sorting: ", chars); System.out.println();
		if (VERBOSE && !FAST) p("Using INSERTION sort: ", Sorter.insertionSort(chars.clone()));
		if (!FAST) p("Using INSERTION_K: ", Sorter.insertionSortK(chars.clone(), _K()));
		if (VERBOSE && !FAST) p("Using SELECTION sort: ", Sorter.selectionSort(chars.clone()));
		if (!FAST) p("Using SELECTION_K: ", Sorter.selectionSortK(chars.clone(), _K()));
		if (VERBOSE && !FAST) p("Using BUBBLE sort: ", Sorter.bubbleSort(chars.clone()));
		if (!FAST) p("Using BUBBLE_K: ", Sorter.bubbleSortK(chars.clone(), _K()));
		if (VERBOSE) p("Using QUICK sort: ", Sorter.quickSort(chars.clone()));
		p("Using QUICK_K: ", Sorter.quickSortK(chars.clone(), _K()));
		if (VERBOSE) p("Using MERGE sort: ", Sorter.mergeSort(chars.clone(), false));
		p("Using MERGE_K: ", Sorter.mergeSortK(chars.clone(), false, _K()));
		if (VERBOSE) p("Using MT_MERGE sort: ", Sorter.mergeSort(chars.clone(), true));
		p("Using MT_MERGE_K: ", Sorter.mergeSortK(chars.clone(), true, _K()));
		if (VERBOSE) p("Using HEAP sort: ", Sorter.heapSort(chars.clone()));
		p("Using HEAP_K: ", Sorter.heapSortK(chars.clone(), _K()));
		System.out.println();
		
		Sorter.NEW_ARRAY_LEN = n;
		Comparable[] doubles;
		doubles = Sorter.randomize(new Double[]{});
		p("Before sorting: ", doubles); System.out.println();
		if (VERBOSE && !FAST) p("Using INSERTION sort: ", Sorter.insertionSort(doubles.clone()));
		if (!FAST) p("Using INSERTION_K: ", Sorter.insertionSortK(doubles.clone(), _K()));
		if (VERBOSE && !FAST) p("Using SELECTION sort: ", Sorter.selectionSort(doubles.clone()));
		if (!FAST) p("Using SELECTION_K: ", Sorter.selectionSortK(doubles.clone(), _K()));
		if (VERBOSE && !FAST) p("Using BUBBLE sort: ", Sorter.bubbleSort(doubles.clone()));
		if (!FAST) p("Using BUBBLE_K: ", Sorter.bubbleSortK(doubles.clone(), _K()));
		if (VERBOSE) p("Using QUICK sort: ", Sorter.quickSort(doubles.clone()));
		p("Using QUICK_K: ", Sorter.quickSortK(doubles.clone(), _K()));
		if (VERBOSE) p("Using MERGE sort: ", Sorter.mergeSort(doubles.clone(), false));
		p("Using MERGE_K: ", Sorter.mergeSortK(doubles.clone(), false, _K()));
		if (VERBOSE) p("Using MT_MERGE sort: ", Sorter.mergeSort(doubles.clone(), true));
		p("Using MT_MERGE_K: ", Sorter.mergeSortK(doubles.clone(), true, _K()));
		if (VERBOSE) p("Using HEAP sort: ", Sorter.heapSort(doubles.clone()));
		p("Using HEAP_K: ", Sorter.heapSortK(doubles.clone(), _K()));		
		System.out.println();
		
		Comparable[] strings;
		strings = Sorter.randomize(new String[]{});
		p("Before sorting: ", strings); System.out.println();
		if (VERBOSE && !FAST) p("Using INSERTION sort: ", Sorter.insertionSort(strings.clone()));
		if (!FAST) p("Using INSERTION_K: ", Sorter.insertionSortK(strings.clone(), _K()));
		if (VERBOSE && !FAST) p("Using SELECTION sort: ", Sorter.selectionSort(strings.clone()));
		if (!FAST) p("Using SELECTION_K: ", Sorter.selectionSortK(strings.clone(), _K()));
		if (VERBOSE && !FAST) p("Using BUBBLE sort: ", Sorter.bubbleSort(strings.clone()));
		if (!FAST) p("Using BUBBLE_K: ", Sorter.bubbleSortK(strings.clone(), _K()));
		if (VERBOSE) p("Using QUICK sort: ", Sorter.quickSort(strings.clone()));
		p("Using QUICK_K: ", Sorter.quickSortK(strings.clone(), _K()));
		if (VERBOSE) p("Using MERGE sort: ", Sorter.mergeSort(strings.clone(), false));
		p("Using MERGE_K: ", Sorter.mergeSortK(strings.clone(), false, _K()));
		if (VERBOSE) p("Using MT_MERGE sort: ", Sorter.mergeSort(strings.clone(), true));
		p("Using MT_MERGE_K: ", Sorter.mergeSortK(strings.clone(), true, _K()));
		if (VERBOSE) p("Using HEAP sort: ", Sorter.heapSort(strings.clone()));
		p("Using HEAP_K: ", Sorter.heapSortK(strings.clone(), _K()));
	}
}
