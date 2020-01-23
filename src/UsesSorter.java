import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Interface wrapper for easy inclusion of {@link Sorter}.
 */
public interface UsesSorter {

	/**
	 *  Static library of generic sorting algorithms.  Usable with any
	 *  array of {@link Comparable}.
	 * <br>
	 * <br>Includes:
	 * <br>- Insertion sort
	 * <br>- Selection sort
	 * <br>- Bubble sort
	 * <br>- Quick sort
	 * <br>- Merge sort
	 * <br>- Merge sort (multithreaded)
	 * <br>- Heap sort
	 * <br>
	 * <br>- Insertion sort k
	 * <br>- Selection sort k
	 * <br>- Bubble sort k
	 * <br>- Quick sort k
	 * <br>- Merge sort k
	 * <br>- Merge sort k (multithreaded)
	 * <br>- Heap sort k
	 * <br>- Median of medians
	 * 
	 * @author Alex Feaser
	 */
	final static class Sorter {

		enum Sort {
			INSERTION, 
			INSERTION_K,
			SELECTION, 
			SELECTION_K,
			BUBBLE, 
			BUBBLE_K,
			QUICK, 
			QUICK_K,
			MERGE,
			MERGE_K,
			MT_MERGE,
			MT_MERGE_K,
			HEAP,
			HEAP_K,
			MEDIAN_OF_MEDIANS,
		};
		
		enum Type {
			SHORTS,
			INTEGERS,
			LONGS,
			FLOATS,
			DOUBLES,
			CHARACTERS,
			STRINGS,
			BIGINTEGERS,
			LOCALDATETIMES,
			UUIDS,
		};
		
		private static final int STRING_LEN = 64;
		private static int NUM_TRIALS = 100;
		protected static int NEW_ARRAY_LEN;
		private static SortStats tracker = new SortStats();
		private static List<SortStats> trialResults = new ArrayList<>();
		private static HashMap<Type, HashMap<Integer, List<SortStats>>> totals = new HashMap<>();
		
		private static final Type[] usingTypes = {
			Type.SHORTS,
			Type.INTEGERS,
			Type.LONGS,
			Type.FLOATS,
			Type.DOUBLES,
			Type.CHARACTERS,
			Type.STRINGS,
			Type.BIGINTEGERS,
			Type.LOCALDATETIMES,
			Type.UUIDS,
		};
		
		public static <E extends Comparable <? super E>> void demoAll() {
			StringBuilder sb = new StringBuilder();
			try {
				Files.write(Paths.get("output.txt"), "".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int n : new int[] {
					10, 
					100, 
					1000, 
					10000,
//					100000,
//					1000000,
//					10000000,
					}) {
				Sorter.NEW_ARRAY_LEN = n;
				final boolean VERBOSE = (n <= 100), SLOW = (n <= 100000);
				for (Type type : usingTypes) {
					if (n >= 1000000 && type == Type.CHARACTERS)
						continue;	// Characters >= 1 million goes infinite on median of medians?
					E[] a = randomize(newArray(type));
					tracker.currentType = type;
					sb.append(String.format("%n%n  ~~~~~~~~  %8s:  %-12s  ~~~~~~~~%n%n", 
							type, String.format("n = %d", n)));
					if (VERBOSE) sb.append(String.format("%-12s%s%n", "Unsorted: ", Arrays.deepToString(a)));
					if (VERBOSE) sb.append(p(Sorter::insertionSort, a.clone()));
					if (SLOW) sb.append(p(Sorter::insertionSortK, a.clone()));
					if (VERBOSE) sb.append(p(Sorter::selectionSort, a.clone()));
					if (SLOW) sb.append(p(Sorter::selectionSortK, a.clone()));
					if (VERBOSE) sb.append(p(Sorter::bubbleSort, a.clone()));
					if (VERBOSE) sb.append(p(Sorter::bubbleSortK, a.clone()));
					if (VERBOSE) sb.append(p(Sorter::quickSort, a.clone()));
					sb.append(p(Sorter::quickSelectK, a.clone()));
					if (VERBOSE) sb.append(p(Sorter::mergeSort, a.clone()));
					sb.append(p(Sorter::mergeSortK, a.clone()));
					if (VERBOSE) sb.append(p(Sorter::mergeSortMulti, a.clone()));
					sb.append(p(Sorter::mergeSortMultiK, a.clone()));
					if (VERBOSE) sb.append(p(Sorter::heapSort, a.clone()));
					sb.append(p(Sorter::heapSortK, a.clone()));
					sb.append(p(Sorter::medianOfMedians, a.clone()));
					sb.append("\n");
					System.out.println(sb.toString());
					try {
						Files.write(Paths.get("output.txt"), sb.toString().getBytes(), StandardOpenOption.APPEND);
					} catch (IOException e) {
						e.printStackTrace();
					}
					sb.setLength(0);
				}
			}
			sb.append(outputSummary());
			System.out.println(sb.toString());
			try {
				Files.write(Paths.get("output.txt"), sb.toString().getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Display formatted results from all trials including averages
		 */
		private static String outputSummary() {
			StringBuilder sb = new StringBuilder();
			List<Type> allKeys = totals.keySet().stream().collect(Collectors.toList());
			Collections.sort(allKeys);
			for (Type type : allKeys) {
				List<Integer> typeKeys = totals.get(type).keySet().stream().collect(Collectors.toList());
				Collections.sort(typeKeys);
				for (Integer n : typeKeys) {
					List<SortStats> list = totals.get(type).get(n);
					final int size = list.size();
					sb.append(String.format("\n  ~~~~~~~~  Average of %d trials:  %s,  n = %d  ~~~~~~~~\n\n", NUM_TRIALS, list.get(0).currentType.toString(), n));
					sb.append(String.format("%6s%-10s%2s", "", "", ""));
					for (int i = 0; i < size; ++i)
						sb.append(String.format("%6s%-18s", "", list.get(i).currentSort.toString(), ""));
					sb.append(String.format("\n\n%16s%6s%-16d%2s", "Array accesses:", "", 
							list.get(0).arrayAccesses, ""));
					for (int i = 1; i < size; ++i)
						sb.append(String.format("%6s%-16d%2s", "", list.get(i).arrayAccesses, ""));
					sb.append(String.format("\n%16s%6s%-16d%2s", "Swaps:", "", 
							list.get(0).swaps, ""));
					for (int i = 1; i < size; ++i)
						sb.append(String.format("%6s%-16d%2s", "", list.get(i).swaps, ""));
					sb.append(String.format("\n%16s%6s%-16d%2s", "Comparisons:", "", 
							list.get(0).comparisons, ""));
					for (int i = 1; i < size; ++i)
						sb.append(String.format("%6s%-16d%2s", "", list.get(i).comparisons, ""));
					sb.append(String.format("\n%16s%6s%-16s%2s", "Elapsed time:", "", 
							SortStats.formatElapsed(list.get(0).elapsedTime), ""));
					for (int i = 1; i < size; ++i)
						sb.append(String.format("%6s%-16s%2s", "", SortStats.formatElapsed(list.get(i).elapsedTime), ""));
					sb.append("\n\n").toString();
				}					
			}
			return sb.toString();
		}

		private static <E extends Comparable<? super E>> StringBuilder p(Function<E[], E[]> f, E[] a) {
			StringBuilder sb = new StringBuilder();
			E[] res = f.apply(a);
			return sb.append(String.format("%n%-20s%s%n%-12s%s%n%n", tracker.currentSort.toString(), 
					tracker.outputTrialResults(), "Result: ", Arrays.toString(res)));
		}

		private static <E extends Comparable<? super E>> StringBuilder p(BiFunction<E[], Integer, E> f, E[] a) {
			StringBuilder sb = new StringBuilder();
			E res = f.apply(a, medianPosition());
			return sb.append(String.format("%n%-20s%s%n%-12s%s%n%n", tracker.currentSort.toString(), 
					tracker.outputTrialResults(), "Result: ", res));
		}

		private static int medianPosition() { 
			return (Sorter.NEW_ARRAY_LEN + 1) >> 1; 
		}		

		private static <E extends Comparable<? super E>> E[] newArray(Type type) {
			switch (type) {
			case SHORTS:
				return (E[]) new Short[]{};
			case INTEGERS:
				return (E[]) new Integer[]{};
			case LONGS:
				return (E[]) new Long[]{};
			case FLOATS:
				return (E[]) new Float[]{};
			case DOUBLES:
				return (E[]) new Double[]{};
			case CHARACTERS:
				return (E[]) new Character[]{};
			case STRINGS:
				return (E[]) new String[]{};
			case BIGINTEGERS:
				return (E[]) new BigInteger[]{};
			case LOCALDATETIMES:
				return (E[]) new LocalDateTime[]{};
			case UUIDS:
				return (E[]) new UUID[]{};
			}
			return null;
		}

		/**
		 * Shuffle the existing elements within argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return
		 */
		public static <E extends Comparable<? super E>> E[] shuffle(E[] array) {
			Collections.shuffle(Arrays.asList(array));
			return array;
		}
		
		/**
		 * Return the maximum value of the generic type at runtime
		 * 
		 * @param <E>
		 * @param array
		 * @return The maximum value the can be held in argument array at runtime
		 */
		private static <E extends Comparable<? super E>> E maxE(Type type) {
			switch (type) {
				case SHORTS:
					return (E) (Short) (Short.MAX_VALUE);
				case INTEGERS:
					return (E) (Integer) (Integer.MAX_VALUE);
				case LONGS:
					return (E) (Long) (Long.MAX_VALUE);
				case FLOATS:
					return (E) (Float) (Float.MAX_VALUE);
				case DOUBLES:
					return (E) (Double) (Double.MAX_VALUE);
				case CHARACTERS:
					return (E) (Character) (Character.MAX_VALUE);
				case STRINGS:
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < STRING_LEN; ++i)
						sb.append(Character.MAX_VALUE);
					return (E) sb.toString();
				case BIGINTEGERS:
					return (E) new BigInteger(2000, new Random());
				case LOCALDATETIMES:
					return (E) LocalDateTime.MAX;
				case UUIDS:
					return (E) new UUID(Long.MAX_VALUE, Long.MAX_VALUE);
			}
			return null;
		}
		
		/**
		 * Populates argument array with new and randomized data.
		 * 
		 * @param <E>
		 * @param array
		 * @return Newly populated argument array
		 */
		@SuppressWarnings("unchecked")
		public static <E extends Comparable<? super E>> E[] randomize(E[] array) {
			Random rand = new Random();
			if (array instanceof String[]) {
				array = (E[]) new String[NEW_ARRAY_LEN];
				String[] a = (String[]) array;
				for (int n = 0; n < array.length; ++n) {
					char[] arr = new char[STRING_LEN];
					for (int i = 0 ; i < STRING_LEN; ++i)
						arr[i] = Character.valueOf((char) (rand.nextInt(26) + 97));
					a[n] = new String(arr);
				}
			} 
			else if (array instanceof Integer[]) {
				array = (E[]) new Integer[NEW_ARRAY_LEN];
				Integer[] a = (Integer[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = rand.nextInt();
			} 
			else if (array instanceof Short[]) {
				array = (E[]) new Short[NEW_ARRAY_LEN];
				Short[] a = (Short[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = (short) (rand.nextInt(Short.MAX_VALUE << 1) - Short.MAX_VALUE);
			} 
			else if (array instanceof UUID[]) {
				array = (E[]) new UUID[NEW_ARRAY_LEN];
				UUID[] a = (UUID[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = UUID.randomUUID();
			} 
			else if (array instanceof LocalDateTime[]) {
				array = (E[]) new LocalDateTime[NEW_ARRAY_LEN];
				LocalDateTime[] a = (LocalDateTime[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = LocalDateTime.of(
							LocalDate.of(rand.nextInt(60) + 1980, rand.nextInt(12) + 1, rand.nextInt(28) + 1)
							, LocalTime.of(rand.nextInt(24), rand.nextInt(60), rand.nextInt(60), rand.nextInt(1000000000)));
			} 
			else if (array instanceof Long[]) {
				array = (E[]) new Long[NEW_ARRAY_LEN];
				Long[] a = (Long[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = rand.nextLong();
			} 
			else if (array instanceof Float[]) {
				array = (E[]) new Float[NEW_ARRAY_LEN];
				Float[] a = (Float[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = rand.nextFloat();
			}  
			else if (array instanceof Double[]) {
				array = (E[]) new Double[NEW_ARRAY_LEN];
				Double[] a = (Double[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = rand.nextDouble();
			} 
			else if (array instanceof Character[]) {
				array = (E[]) new Character[NEW_ARRAY_LEN];
				Character[] a = (Character[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = Character.valueOf((char) (rand.nextInt(93) + 33));
			}
			else if (array instanceof BigInteger[]) {
				array = (E[]) new BigInteger[NEW_ARRAY_LEN];
				BigInteger[] a = (BigInteger[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = new BigInteger(1000, rand);
			}
			return array;
		}

		/**
		 * Swaps elements at argument indices within argument array
		 * 
		 * @param <E>
		 * @param array
		 * @param a
		 * @param b
		 */
		private static <E> void swap(E[] array, int a, int b) {
			int len = array.length;
			if (a >= len || b >= len || a < 0 || b < 0) {
				System.err.println(String.format("len: %d, a: %d, b: %d", len, a, b));
				throw new IllegalArgumentException();
			}
			E c = array[a];
			array[a] = array[b];
			array[b] = c;
			tracker.swaps++;
			tracker.arrayAccesses += 4;
		}

		/**
		 * Perform an insertion sort on argument array using a binary search on the sorted portion
		 * 
		 * @param <E>
		 * @param array
		 * @return sorted argument array
		 */
		public static <E extends Comparable<? super E>> E[] insertionSort(E[] array) {
			tracker.currentSort = Sort.INSERTION;
			return tracker.track(Sorter::insertionSorter, array);
		}

		/**
		 * Perform an insertion sort on argument array using a binary search 
		 * on the sorted portion and return the kth smallest element
		 * 
		 * @param <E>
		 * @param array
		 * @return kth smallest element of argument array
		 */
		public static <E extends Comparable<? super E>> E insertionSortK(E[] array, int k) {
			tracker.currentSort = Sort.INSERTION_K;
			return tracker.track(Sorter::insertionSorter, array)[k - 1];
		}

		/**
		 * Algorithm for insertion sort 
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] insertionSorter(E[] array) {
			for (int i = 1; i < array.length; i++) {  
				E key = array[i];
				int pos = Math.abs(binarySearch(array, 0, i, key) + 1);
				System.arraycopy(array, pos, array, pos + 1, i - pos);
				array[pos] = key;
				tracker.arrayAccesses += (i - pos + 2) << 2;
				tracker.swaps += i - pos;
			}
			return array;
		}
		
		/**
		 * Perform a binary search for target argument key within the range of 
		 * left (inclusive) to right (inclusive) in argument array
		 * 
		 * @param <E>
		 * @param array
		 * @param left
		 * @param right
		 * @param key
		 * @return
		 */
		public static <E extends Comparable<? super E>> int binarySearch(E[] array, int left, int right, E key) {
			int mid = 0, l = left, r = right - 1;
			while (r >= l) {
				mid = (r + l) >>> 1;
				int res = array[mid].compareTo(key);
				tracker.arrayAccesses++;
				tracker.comparisons++;
				if (res > 0) 
					r = mid - 1;
				else if (res < 0)
					l = mid + 1;
				else 
					return mid;
			}
			return -(l + 1);
		}

		/**
		 * Perform a selection sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return sorted argument array
		 */
		public static <E extends Comparable<? super E>> E[] selectionSort(E[] array) {
			int trials = NUM_TRIALS;
			NUM_TRIALS = 10;
			tracker.currentSort = Sort.SELECTION;
			E[] res = tracker.track(Sorter::selectionSortHelper, array);
			NUM_TRIALS = trials;
			return res;
		}

		/**
		 * Perform a selection sort on argument array up to the kth smallest element
		 * 
		 * @param <E>
		 * @param array
		 * @return kth smallest element of argument array
		 */
		public static <E extends Comparable<? super E>> E selectionSortK(E[] array, int k) {
			int trials = NUM_TRIALS;
			NUM_TRIALS = 10;
			tracker.currentSort = Sort.SELECTION_K;
			E res = tracker.trackK(Sorter::selectionSortHelperK, array, k);
			NUM_TRIALS = trials;
			return res;
		}
		
		/**
		 * Used by selection sort
		 * 
		 * @param <E>
		 * @param array
		 * @return
		 */
		public static <E extends Comparable<? super E>> E[] selectionSortHelper(E[] array) {
			return selectionSorterK(array, array.length - 1);
		}
		
		/**
		 * Used by selection sort k
		 * 
		 * @param <E>
		 * @param array
		 * @param k
		 * @return
		 */
		public static <E extends Comparable<? super E>> E selectionSortHelperK(E[] array, int k) {
			return selectionSorterK(array, k)[k - 1];
		}

		/**
		 * Algorithm for selection sort
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] selectionSorterK(E[] array, int k) {
			int len = array.length - 1, indexMin = 0;
			E minVal;
			for (int i = 0; i < k; i++) { 
				minVal = array[indexMin = i];
				tracker.arrayAccesses++;
				for (int j = i; j <= len; j++) {
					if (minVal.compareTo(array[j]) > 0) { 
						minVal = array[indexMin = j];
						tracker.arrayAccesses++;
					}
					tracker.comparisons++;
					tracker.arrayAccesses++;
				}
				if (indexMin != i)
					swap(array, i, indexMin);
			}
			return array;
		}

		/**
		 * Perform a bubble sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return sorted argument array
		 */
		public static <E extends Comparable<? super E>> E[] bubbleSort(E[] array) {
			tracker.currentSort = Sort.BUBBLE;
			return tracker.track(Sorter::bubbleSorter, array);
		}

		/**
		 * Perform a bubble sort on argument array down to the kth smallest element
		 * 
		 * @param <E>
		 * @param array
		 * @return kth smallest element of argument array
		 */
		public static <E extends Comparable<? super E>> E bubbleSortK(E[] array, int k) {
			tracker.currentSort = Sort.BUBBLE_K;
			return tracker.trackK(Sorter::bubbleSorterK, array, k);
		}

		/**
		 * Algorithm for bubble sort
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] bubbleSorter(E[] array) {
			int len = array.length - 1;
			for (int i = -1; i < len; len--) { 
				int swaps = 0;
				while(++i < len) {
					if (array[i].compareTo(array[i + 1]) > 0) 
						swap(array, (swaps++ & 0) + i, i + 1);
					tracker.comparisons++;
					tracker.arrayAccesses++;
				}
				i = -1;
				if (swaps == 0)
					return array;
			}
			return array;
		}

		/**
		 * Algorithm for bubble sort k
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E bubbleSorterK(E[] array, int k) {
			int len = array.length - 1, swaps = 0;
			for (int i = -1; i < len && len >= k - 1; len--) { 
				swaps = 0;
				while(++i < len) {
					if (array[i].compareTo(array[i + 1]) > 0) 
						swap(array, (swaps++ & 0) + i, i + 1);
					tracker.comparisons++;
					tracker.arrayAccesses++;
				}
				i = -1;
				if (swaps == 0)
					return array[k - 1];
			}
			return array[k - 1];
		}

		/**
		 * Perform a quick sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E[] quickSort(E[] array) {
			tracker.currentSort = Sort.QUICK;
			return tracker.track(Sorter::quickSorter, array);
		}

		/**
		 * Perform a quick select on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E quickSelectK(E[] array, int k) {
			tracker.currentSort = Sort.QUICK_K;
			return tracker.trackK(Sorter::quickSelectSorterK, array, k);
		}

		/**
		 * Used by quick sort
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] quickSorter(E[] array) {	
			return quickSortHelper(array, 0, array.length - 1);
		}

		/**
		 * Used by quick select
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E quickSelectSorterK(E[] array, int k) {
			return quickSelectHelperK(array, 0, array.length - 1, k - 1)[k - 1];
		}

		/**
		 * Algorithm for quick sort
		 * 
		 * @param <E>
		 * @param array
		 * @param lIndex
		 * @param rIndex
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] quickSortHelper(E[] array, int l, int h) {
			if (h - l <= 1)
				return array;
			int stack[] = new int[h - l + 1];
		    int top = -1; 
		    stack[++top] = l; 
		    stack[++top] = h; 
		    tracker.arrayAccesses += 2;
		    while (top >= 0) { 
		        h = stack[top--]; 
		        l = stack[top--]; 
			    tracker.arrayAccesses += 2;
		        int p = partition(array, l, h); 
		        if (p - 1 > l) { 
		            stack[++top] = l; 
		            stack[++top] = p - 1; 
				    tracker.arrayAccesses += 2;
		        } 
		        if (p + 1 < h) { 
		            stack[++top] = p + 1; 
		            stack[++top] = h; 
				    tracker.arrayAccesses += 2;
		        } 
		    } 
		    return array;
		}

		/**
		 * Algorithm for quick select
		 * 
		 * @param <E>
		 * @param array
		 * @param lIndex
		 * @param rIndex
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] quickSelectHelperK(E[] array, int l, int h, int k) {
			while(true) {
				int pivotIndex = partition(array, l, h);
				if (k == pivotIndex)
					return array;
				else if(k < pivotIndex)
					h = pivotIndex - 1;
				else
					l = pivotIndex + 1;
			}
		}

		 /**
		  * Partition the argument array around the value at index argument r
		  * 
		  * @param <E>
		  * @param array
		  * @param l
		  * @param r
		  * @return
		  */
		private static <E extends Comparable<? super E>> int partition(E[] array, int l, int r) {
//			E pivot = findMedianOfMedians(array, l, r);
//			E pivot = array[l + ((r - l) >> 1)];
			E pivot = array[r];
			tracker.arrayAccesses++;
			int pIndex = l;
			for (int i = l; i < r; i++) {
				if (array[i].compareTo(pivot) <= 0)
					swap(array, i, pIndex++);
				tracker.comparisons++;
				tracker.arrayAccesses++;
			}
			swap(array, pIndex, r);
			return pIndex;
		}

		/**
		 * Perform a merge sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E[] mergeSort(E[] array) {
			tracker.currentSort = Sort.MERGE;
			return tracker.track(Sorter::mergeSorter, array);
		}

		/**
		 * Perform a merge sort on argument array up to the kth smallest element
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E mergeSortK(E[] array, int k) {
			tracker.currentSort = Sort.MERGE_K;
			return tracker.trackK(Sorter::mergeSortHelperK, array, k);
		}

		/**
		 * Perform a multithreaded merge sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E[] mergeSortMulti(E[] array) {
			tracker.currentSort = Sort.MT_MERGE;
			return tracker.track(Sorter::multithreadedMergeSort, array);
		}

		/**
		 * Perform a multithreaded merge sort on argument array up to the kth smallest element
		 * 
		 * @param <E>
		 * @param array
		 * @return kth smallest element of the array
		 */
		public static <E extends Comparable<? super E>> E mergeSortMultiK(E[] array, int k) {
			tracker.currentSort = Sort.MT_MERGE_K;
			return tracker.trackK(Sorter::multithreadedMergeSortK, array, k);
		}
		
		/**
		 * Invoke an instance of {@link ForkJoinMergeSort} on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] multithreadedMergeSort(E[] array) {
			ForkJoinMergeSort<E> sort = new ForkJoinMergeSort<>(array);
			ForkJoinPool.commonPool().invoke(sort);
			return sort.join();
		}
		
		/**
		 * Invoke an instance of {@link ForkJoinMergeSort} on argument array up to the kth smallest element
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E multithreadedMergeSortK(E[] array, int k) {
			ForkJoinMergeSort<E> sort = new ForkJoinMergeSort<>(array, k);
			ForkJoinPool.commonPool().invoke(sort);
			return sort.join()[k - 1];
		}
		
		/**
		 * Used by merge sort k
		 * @param <E>
		 * @param array
		 * @param k
		 * @return
		 */
		private static <E extends Comparable<? super E>> E mergeSortHelperK(E[] array, int k) {
			return mergeSorterK(array, k)[k - 1];
		}
		
		/**
		 * Algorithm for merge sort
		 * @param <E>
		 * @param array
		 * @return
		 */
		@SuppressWarnings("unchecked")
		private static <E extends Comparable<? super E>> E[] mergeSorter(E[] array) {
			int len = array.length;
			if (len < 2)
				return array;
			int mid = len >> 1;
			E[] tempLeftArray = (E[]) new Comparable<?>[mid];
			E[] tempRightArray = (E[]) new Comparable[len - mid];
			int index = 0;
			for (int i = 0; i < mid; i++)
				tempLeftArray[index++]  = array[i];
			index = 0;
			for (int i = mid; i < len; i++)
				tempRightArray[index++]  = array[i];
			tracker.swaps += len - 2;
			tracker.arrayAccesses += (len - 1) << 1;
			mergeSorter(tempLeftArray);
			mergeSorter(tempRightArray);
			return merge(tempLeftArray, tempRightArray, array);
		}
		
		/**
		 * Algorithm for merge sort k
		 * @param <E>
		 * @param array
		 * @return
		 */
		@SuppressWarnings("unchecked")
		private static <E extends Comparable<? super E>> E[] mergeSorterK(E[] array, int k) {
			int len = array.length;
			if (len < 2)
				return array;
			int mid = len >> 1;
			E[] tempL = (E[]) new Comparable[mid];
			E[] tempR = (E[]) new Comparable<?>[len - mid];
			int index = 0;
			for (int i = 0; i < mid; i++)
				tempL[index++]  = array[i];
			index = 0;
			for (int i = mid; i < len; i++)
				tempR[index++]  = array[i];
			tracker.swaps += len - 2;
			tracker.arrayAccesses += (len - 1) << 1;
			mergeSorter(tempL);
			mergeSorter(tempR);
			return mergeK(tempL, tempR, array, k);
		}

		/**
		 * Combine two sorted arrays
		 * 
		 * @param <E>
		 * @param tempL
		 * @param tempR
		 * @param array
		 */
		private static <E extends Comparable<? super E>> E[] merge(E[] tempL, E[] tempR, E[] array) {
			int leftlen = tempL.length, rightlen = tempR.length;
			int leftIndex = 0, rightIndex = 0, index = 0;
			while (leftIndex < leftlen && rightIndex < rightlen) {
				if (tempL[leftIndex].compareTo(tempR[rightIndex]) <= 0)
					array[index++] = tempL[leftIndex++];
				else
					array[index++] = tempR[rightIndex++];
				tracker.comparisons++;
				tracker.arrayAccesses += 4;
			}
			while (leftIndex < leftlen)
				array[index++] = tempL[leftIndex++];
			tracker.swaps += leftlen = leftlen - leftIndex + 1;
			tracker.arrayAccesses += leftlen << 1;
			while (rightIndex < rightlen)
				array[index++] = tempR[rightIndex++];
			tracker.swaps += rightlen = rightlen - rightIndex + 1;
			tracker.arrayAccesses += rightlen << 1;
			return array;
		}

		/**
		 * Combine two sorted arrays up to the kth smallest value
		 * 
		 * @param <E>
		 * @param tempL
		 * @param tempR
		 * @param array
		 */
		private static <E extends Comparable<? super E>> E[] mergeK(E[] tempL, E[] tempR, E[] array, int k) {
			int leftlen = tempL.length, rightlen = tempR.length;
			int leftIndex = 0, rightIndex = 0, index = 0;
			while (leftIndex < leftlen && rightIndex < rightlen) {
				if (tempL[leftIndex].compareTo(tempR[rightIndex]) <= 0)
					array[index++] = tempL[leftIndex++];
				else
					array[index++] = tempR[rightIndex++];
				tracker.comparisons++;
				tracker.arrayAccesses += 4;
				if (index == k)
					return array;
			}
			while (leftIndex < leftlen) {
				array[index++] = tempL[leftIndex++];
				tracker.swaps++;
				tracker.arrayAccesses += 2;
				if (index == k)
					return array;
			}
			while (rightIndex < rightlen) {
				array[index++] = tempR[rightIndex++];
				tracker.swaps++;
				tracker.arrayAccesses += 2;
				if (index == k)
					return array;
			}
			return array;
		}

		/**
		 * Perform a heap sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E[] heapSort(E[] array) {
			tracker.currentSort = Sort.HEAP;	
			return tracker.track(Sorter::heapSorter, array);
		}

		/**
		 * Perform a heap sort on argument array up to the kth smallest element
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E heapSortK(E[] array, int k) {
			tracker.currentSort = Sort.HEAP_K;	
			return tracker.trackK(Sorter::heapSorterK, array, k);
		}
		
		/**
		 * Algorithm for heap sort.  Modeled after https://www.geeksforgeeks.org/heap-sort/
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] heapSorter(E[] array) {
			int n = array.length;
	        for (int i = (n >> 1) - 1; i >= 0; i--)
	            heapify(array, n, i);
	        for (int i = n - 1; i >= 0; i--) {
	            swap(array, 0, i);
	            heapify(array, i, 0);
	        }
	        return array;
		}
		
		/**
		 * Algorithm for heap sort k.  Modeled after https://www.geeksforgeeks.org/heap-sort/
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E heapSorterK(E[] array, int k) {
			int n = array.length;
	        for (int i = (n >> 1) - 1; i >= 0; i--)
	            heapify(array, n, i);
	        for (int i = n - 1; i >= k - 1; i--) {
	            swap(array, 0, i);
	            heapify(array, i, 0);
	        }
	        return array[k - 1];
		}
		
		/**
		 * Algorithm used in heap sort.  Modeled after https://www.geeksforgeeks.org/heap-sort/
		 * 
		 * @param <E>
		 * @param array
		 * @param n
		 * @param i
		 */
		private static <E extends Comparable<? super E>> void heapify(E[] array, int n, int i) {
	        int largest = i;
	        int l = (i << 1) + 1;
	        int r = (i << 1) + 2;
	        if (l < n) {
	        	if (array[l].compareTo(array[largest]) > 0)
	        		largest = l;
	        	tracker.comparisons++;
	        	tracker.arrayAccesses+= 2;
	        }
	        if (r < n) {
	        	if (array[r].compareTo(array[largest])  > 0)
	        		largest = r;
	        	tracker.comparisons++;
	        	tracker.arrayAccesses+= 2;
	        }
	        if (largest != i) {
	        	swap(array, i, largest);
	            heapify(array, n, largest); 
	        }
	    }

		/**
		 * Finds the column of 5 median of medians of the argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return Median value of the array
		 */
		public static <E extends Comparable<? super E>> E medianOfMedians(E[] array, int k) {
			tracker.currentSort = Sort.MEDIAN_OF_MEDIANS;
			return tracker.trackK(Sorter::medianOfMediansHelper, array, k);
		}
		
		/**
		 * Used by meadian of meadians
		 * 
		 * @param <E>
		 * @param array
		 * @param k
		 * @return
		 */
		public static <E extends Comparable<? super E>> E medianOfMediansHelper(E[] array, int k) {
			return medianOfMediansSorterK(array, 0, array.length - 1, k);
		}
		
		/**
		 * Partition the argument array around the argument value.  Used by median of medians.
		 *
		 * @param <E>
		 * @param list
		 * @param left
		 * @param right
		 * @param val
		 * @return
		 */
		private static <E extends Comparable<? super E>> int partition(E[] list, int left, int right, E val) {
	        int i;
	        for (i = left; i < right; i++) {
	            if (list[i].compareTo(val) == 0) {
	            	tracker.arrayAccesses++;
	            	tracker.comparisons++;
	                break;
	            }
            	tracker.arrayAccesses++;
            	tracker.comparisons++;
	        }
	        swap(list, i, right);
	        E pivotValue = list[right];
	        tracker.arrayAccesses++;
	        int storeIndex = left;
	        for (i = left; i <= right; i++) {
	            if (list[i].compareTo(pivotValue) < 0) {
	            	tracker.arrayAccesses++;
	            	tracker.comparisons++;
	                swap(list, storeIndex, i);
	                storeIndex++;
	            }
            	tracker.arrayAccesses++;
            	tracker.comparisons++;
	        }
	        swap(list, right, storeIndex);
	        return storeIndex;
	    }
		
		/**
		 * Find the median of argument array by pre-sorting.
		 * 
		 * @param <E>
		 * @param arr
		 * @param l
		 * @param len
		 * @return
		 */
	    private static <E extends Comparable<? super E>> E findMedian(E arr[], int l, int len) {
	        Arrays.sort(arr, l, l + len);
	        tracker.arrayAccesses += (len >> 1) + 1;
	        tracker.comparisons += len;
	        return arr[l + ((len - 1) >> 1)];
	    }
		
		/**
		 * Find the median of argument array by pre-sorting.
		 * 
		 * @param <E>
		 * @param arr
		 * @param l
		 * @param len
		 * @return
		 */
	    private static <E extends Comparable<? super E>> E findMedianOfMedians(E arr[], int l, int r) {
	    	int n = r - l + 1, i;
	    	E median[] = (E[]) new Comparable[(n + 4) / 5];
	    	for (i = 0; i < (n - 1) / 5; i++)
	    		median[i] = findMedian(arr, l + (i * 5), 5);
	    	if (i * 5 < n) {
	    		median[i] = findMedian(arr, l + (i * 5), (n - 1) % 5);
	    		++i;
	    	}
	    	return (i == 1) ? median[0] : findMedianOfMedians(median, 0, i - 1);
	    }
	    
	    /**
	     * Algorithm for finding the kth smallest element using the medians of medians strategy.
		 * Modeled after https://www.geeksforgeeks.org/kth-smallestlargest-element-unsorted-array-set-3-worst-case-linear-time/
	     * @param <E>
	     * @param arr
	     * @param l
	     * @param r
	     * @param k
	     * @return
	     */
	    public static <E extends Comparable <? super E>> E medianOfMediansSorterK(E arr[], int l, int r, int k) {
	        if (k > 0 && k <= r - l + 1) {
	        	E medOfMed = findMedianOfMedians(arr, l, r);
	            int pos = partition(arr, l, r, medOfMed);
	            if (pos - l == k - 1)
	                return arr[pos];
	            else if (pos - l > k - 1)
	                return medianOfMediansSorterK(arr, l, pos - 1, k);
	            return medianOfMediansSorterK(arr, pos + 1, r, k - pos + l - 1);
	        }
	        return maxE(tracker.currentType);
	    }

		/**
		 * Uses {@link ForkJoinPool} paradigm to perform a multithreaded merge sort
		 * on argument array
		 * 
		 * @author Alex Feaser
		 */
		@SuppressWarnings("serial")
		private static class ForkJoinMergeSort<T extends Comparable<? super T>> extends RecursiveTask<T[]> {
			private T[] v;
			private static int thread = 0;
			private int thread_id = 0;
			private final int k;
			
			/**
			 * Initiate a ForkJoin merge sort on argument array v
			 *  
			 * @param v
			 */
			public ForkJoinMergeSort(T[] v) { this(v, -1); }
			
			/**
			 * Initiate a ForkJoin merge sort on argument array v up to the kth smallest element
			 * @param v
			 * @param k
			 */
			public ForkJoinMergeSort(T[] v, int k) {
				this.v = v;
				this.k = k;
				thread_id = (k == -1 ? 0 : ++thread);
			}

			/**
			 * Algorithm for ForkJoin merge sort
			 */
			@Override
			protected T[] compute() {
				if (v.length <= 1)
					return v;
				else {
					final int pos = v.length >> 1;
					T[] leftPartition = Arrays.copyOfRange(v, 0, pos);
					T[] rightPartition = Arrays.copyOfRange(v, pos, v.length);
					ForkJoinMergeSort<T> leftSort = new ForkJoinMergeSort<>(leftPartition);
					ForkJoinMergeSort<T> rightSort = new ForkJoinMergeSort<>(rightPartition);
					invokeAll(leftSort, rightSort);
					return (k != -1 && thread_id == 1) ? 
							mergeK(leftSort.join(), rightSort.join(), v, k) : 
								merge(leftSort.join(), rightSort.join(), v);
				}
			}
			
			/**
			 * Merge argument arrays left and right into argument array arr
			 * 
			 * @param left
			 * @param right
			 * @param arr
			 * @return
			 */
			private T[] merge(T[] left, T[] right, T[] arr) {
				return Sorter.merge(left, right, arr);
			}

			/**
			 * Merge argument arrays left and right into argument array arr up to the kth smallest element
			 * 
			 * @param left
			 * @param right
			 * @param arr
			 * @param k
			 * @return
			 */
			private T[] mergeK(T[] left, T[] right, T[] arr, int k) {
				thread = 0;
				return Sorter.mergeK(left, right, arr, k);
			}
			
		}

		/**
		 * Responsible for tracking, storing, and displaying statistics of algorithms found in {@link Sorter}
		 * 
		 * @author AF
		 *
		 */
		private static class SortStats {
			protected long arrayAccesses;
			protected long swaps;
			protected long comparisons;
			protected long startTime;
			protected long elapsedTime;
			protected int n;
			protected Comparable<?> resultVal;
			private Sort currentSort;
			private Type currentType;
			
			public SortStats() {}
			
			private SortStats(SortStats s) {
				arrayAccesses = s.arrayAccesses;
				swaps = s.swaps;
				comparisons = s.comparisons;
				startTime = s.startTime;
				elapsedTime = s.elapsedTime;
				resultVal = s.resultVal;
				currentSort = s.currentSort;
				currentType = s.currentType;
				n = s.n;
			}
			
			private void startTimer() { startTime = System.nanoTime(); }
			private void endTimer() { elapsedTime = System.nanoTime() - startTime; }
			
			/**
			 * Prepare tracker prior to each sort
			 */
			private void prep() {
				arrayAccesses = 0;
				swaps = 0;
				comparisons = 0;
				elapsedTime = 0;
				n = NEW_ARRAY_LEN;
				resultVal = null;
				startTimer();
			}
			
			/**
			 * Display, then clear results
			 */
			private void end(Type type) {
//				System.out.println(outputTrialResults());
				HashMap<Integer, List<SortStats>> map = totals.getOrDefault(type, new HashMap<>());
				List<SortStats> list = map.getOrDefault(NEW_ARRAY_LEN, new ArrayList<>());
				list.add(trialResults.get(NUM_TRIALS));
				map.put(NEW_ARRAY_LEN, list);
				totals.put(type, map);
//				trialResults.clear();
			}
			
			/**
			 * Display formatted results from all trials including averages
			 */
			public String outputTrialResults() {
				StringBuilder sb = new StringBuilder();
				int len = trialResults.size() - 1;
				int x = (int)Math.floor(Math.log10(len)) + 1;
				sb.append(String.format("%6s%-16s%2s", "", "Average", ""));
				for (int i = 1; i <= len; ++i)
					sb.append(String.format("%6sTrial %0" + x + "d%" + (10 - x + 2) + "s", "", i, ""));
				sb.append(String.format("\n\n%16s%6s%-16d%2s", "Array accesses:", "", 
						trialResults.get(len).arrayAccesses, ""));
				for (int i = 0; i < len; ++i)
					sb.append(String.format("%6s%-16d%2s", "", trialResults.get(i).arrayAccesses, ""));
				sb.append(String.format("\n%16s%6s%-16d%2s", "Swaps:", "", 
						trialResults.get(len).swaps, ""));
				for (int i = 0; i < len; ++i)
					sb.append(String.format("%6s%-16d%2s", "", trialResults.get(i).swaps, ""));
				sb.append(String.format("\n%16s%6s%-16d%2s", "Comparisons:", "", 
						trialResults.get(len).comparisons, ""));
				for (int i = 0; i < len; ++i)
					sb.append(String.format("%6s%-16d%2s", "", trialResults.get(i).comparisons, ""));
				sb.append(String.format("\n%16s%6s%-16s%2s", "Elapsed time:", "", 
						formatElapsed(trialResults.get(len).elapsedTime), ""));
				for (int i = 0; i < len; ++i)
					sb.append(String.format("%6s%-16s%2s", "", formatElapsed(trialResults.get(i).elapsedTime), ""));
				return sb.append("\n").toString();
			}
			
			/**
			 * Format the elapsed time into a readable string
			 * 
			 * @param elapsed
			 * @return
			 */
			public static String formatElapsed(long elapsed) {
				long seconds = elapsed / 1000000000;
				long nanos = elapsed % 1000000000;
				return String.format("%d.%09ds", seconds, nanos);
			}

			/**
			 * Track argument sorting function across a number of trials
			 * 
			 * @param <E>
			 * @param f
			 * @param array
			 * @return sorted array
			 */
			private <E extends Comparable<? super E>> E[] track(Function<E[], E[]> f, E[] array) {
				trialResults.clear();
				E[] res = null;
				for (int i = 0; i < NUM_TRIALS; ++i) {
					E[] a = array.clone();
					prep();
					res = f.apply(a);
					endTimer();
					trialResults.add(new SortStats(tracker));
					shuffle(array);
				}
				trialResults.add(averageOfTrials());
				end(currentType);
				return res;
			}
			
			/**
			 * Return a SortStats object containing the averages of the results
			 * 
			 * @return
			 */
			private SortStats averageOfTrials() {
				tracker.arrayAccesses = Math.round(trialResults.stream().mapToLong(s -> s.arrayAccesses).average().getAsDouble());
				tracker.swaps = Math.round(trialResults.stream().mapToLong(s -> s.swaps).average().getAsDouble());
				tracker.comparisons = Math.round(trialResults.stream().mapToLong(s -> s.comparisons).average().getAsDouble());
				tracker.elapsedTime = Math.round(trialResults.stream().mapToLong(s -> s.elapsedTime).average().getAsDouble());
				return new SortStats(tracker);
			}

			/**
			 * Track argument k-sorting function across a number of trials
			 * 
			 * @param <E>
			 * @param f
			 * @param array
			 * @param k
			 * @return kth smallest element
			 */
			private <E extends Comparable<? super E>> E trackK(BiFunction<E[], Integer, E> f, E[] array, int k) {
				trialResults.clear();
				E val = null;
				for (int i = 0; i < NUM_TRIALS; ++i) {
					E[] a = array.clone();
					prep();
					val = (E) (resultVal = f.apply(a, k));
					endTimer();
					trialResults.add(new SortStats(tracker));
					shuffle(array);
				}
				trialResults.add(averageOfTrials());
				end(currentType);
				return val;
			}
			
			public String toString() {
				return String.format("%s:  %s", currentSort.toString(), currentType.toString());
			}
		}
	}
}
