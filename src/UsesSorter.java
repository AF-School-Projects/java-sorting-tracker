import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;
import java.util.function.Function;

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

		private static final int STRING_LEN = 64;
		private static final int NUM_TRIALS = 10;
		protected static int NEW_ARRAY_LEN;
		private static SortStats tracker = new SortStats();
		private static List<SortStats> results = new ArrayList<>();

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
		private static <E extends Comparable<? super E>> E maxE(E[] array) {
			if (array instanceof Integer[])
				return (E) (Integer) (Integer.MAX_VALUE);
			else if (array instanceof Short[])
				return (E) (Short) (Short.MAX_VALUE);
			else if (array instanceof UUID[])
				return (E) (UUID) (new UUID(Long.MAX_VALUE, Long.MAX_VALUE));
			else if (array instanceof LocalDateTime[])
				return (E) (LocalDateTime) LocalDateTime.MAX;
			else if (array instanceof Long[])
				return (E) (Long) (Long.MAX_VALUE);
			else if (array instanceof Float[])
				return (E) (Float) (Float.MAX_VALUE);
			else if (array instanceof Double[])
				return (E) (Double) (Double.MAX_VALUE);
			else if (array instanceof Character[])
				return (E) (Character) (Character.MAX_VALUE);
			else if (array instanceof String[]) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < STRING_LEN; ++i)
					sb.append(Character.MAX_VALUE);
				return (E) sb.toString();
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
			for (int i = 1, j; i < array.length; i++) {  
				E key = array[i];
				int pos = Math.abs(binarySearch(array, 0, i, key) + 1);
				System.arraycopy(array, pos, array, pos + 1, i - pos);
				array[pos] = key;
				tracker.arrayAccesses += 2;
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
			return tracker.track(Sorter::selectionSortHelper, array);
		}

		/**
		 * Perform a selection sort on argument array up to the kth smallest element
		 * 
		 * @param <E>
		 * @param array
		 * @return kth smallest element of argument array
		 */
		public static <E extends Comparable<? super E>> E selectionSortK(E[] array, int k) {
			return tracker.trackK(Sorter::selectionSortHelperK, array, k);
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
			return tracker.trackK(Sorter::heapSorterK, array, k);
		}
		
		/**
		 * Algorithm for heap sort
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
		 * Algorithm for heap sort k
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
		 * Algorithm used in heap sort
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
			return medianOfMediansSorter(array, 0, array.length - 1, k);
		}
		
		/**
		 * Partition the argument array around the argument value
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
		 * Find the median of argument array by pre-sorting
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
	        return arr[l + (len >> 1)];
	    }
	    
	    /**
	     * Algorithm for finding the kth smallest element using the medians of medians strategy
	     * @param <E>
	     * @param arr
	     * @param l
	     * @param r
	     * @param k
	     * @return
	     */
	    public static <E extends Comparable <? super E>> E medianOfMediansSorter(E arr[], int l, int r, int k) {
	        if (k > 0 && k <= r - l + 1) {
	            int n = r - l + 1, i;
	            E median[] = (E[]) new Comparable[(n + 4) / 5];
	            for (i = 0; i < n / 5; i++) {
	                median[i] = findMedian(arr, l + i * 5, 5);
	            }
	            tracker.arrayAccesses += n / 5;
	            if (i * 5 < n) {
	                median[i] = findMedian(arr, l + i * 5, n % 5);
	                i++;
		            tracker.arrayAccesses++;
	            }
	            E medOfMed = (i == 1) ? median[0] : medianOfMediansSorter(median, 0, i - 1, i >> 1);

	            int pos = partition(arr, l, r, medOfMed);
	            if (pos - l == k - 1)
	                return arr[pos];
	            else if (pos - l > k - 1)
	                return medianOfMediansSorter(arr, l, pos - 1, k);
	            return medianOfMediansSorter(arr, pos + 1, r, k - pos + l - 1);
	        }
	        return maxE(arr);
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
			protected Comparable<?> resultVal;
			
			public SortStats() {}
			
			private SortStats(SortStats s) {
				arrayAccesses = s.arrayAccesses;
				swaps = s.swaps;
				comparisons = s.comparisons;
				startTime = s.startTime;
				elapsedTime = s.elapsedTime;
				resultVal = s.resultVal;
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
				resultVal = null;
				startTimer();
			}
			
			/**
			 * Display, then clear results
			 */
			private void end() {
				displayResults();
				results.clear();
			}
			
			/**
			 * Display formatted results from all trials including averages
			 */
			private void displayResults() {
				StringBuilder sb = new StringBuilder();
				int x = (int)Math.floor(Math.log10(NUM_TRIALS)) + 1;
				sb.append(String.format("%6s%-16s%2s", "", "Average", ""));
				for (int i = 1; i <= NUM_TRIALS; ++i)
					sb.append(String.format("%6sTrial %0" + x + "d%10s", "", i, ""));
				System.out.println(sb.append("\n").toString());
				sb.setLength(0);
				sb.append(String.format("%16s", "Array accesses:"));
				sb.append(String.format("%6s%-16d%2s", "", 
						Math.round(results.stream().mapToLong(s -> s.arrayAccesses).average().getAsDouble()), ""));
				for (int i = 0; i < NUM_TRIALS; ++i)
					sb.append(String.format("%6s%-16d%2s", "", results.get(i).arrayAccesses, ""));
				System.out.println(sb.toString());
				sb.setLength(0);
				sb.append(String.format("%16s", "Swaps:"));
				sb.append(String.format("%6s%-16d%2s", "", 
						Math.round(results.stream().mapToLong(s -> s.swaps).average().getAsDouble()), ""));
				for (int i = 0; i < NUM_TRIALS; ++i)
					sb.append(String.format("%6s%-16d%2s", "", results.get(i).swaps, ""));
				System.out.println(sb.toString());
				sb.setLength(0);
				sb.append(String.format("%16s", "Comparisons:"));
				sb.append(String.format("%6s%-16d%2s", "", 
						Math.round(results.stream().mapToLong(s -> s.comparisons).average().getAsDouble()), ""));
				for (int i = 0; i < NUM_TRIALS; ++i)
					sb.append(String.format("%6s%-16d%2s", "", results.get(i).comparisons, ""));
				System.out.println(sb.toString());
				sb.setLength(0);
				sb.append(String.format("%16s", "Elapsed time:"));
				long elapsed = Math.round(results.stream().mapToLong(s -> s.elapsedTime).average().getAsDouble());
				long seconds = elapsed / 1000000000;
				long nanos = elapsed % 1000000000;
				sb.append(String.format("%6s%-16s%2s", "", 
						String.format("%d.%09ds", seconds, nanos), ""));
				for (int i = 0; i < NUM_TRIALS; ++i) {
					elapsed = results.get(i).elapsedTime;
					seconds = elapsed / 1000000000;
					nanos = elapsed % 1000000000;
					sb.append(String.format("%6s%-16s%2s", "", String.format("%d.%09ds", seconds, nanos), ""));
				}
				System.out.println(sb.append("\n").toString());
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
				E[] res = null;
				for (int i = 0; i < NUM_TRIALS; ++i) {
					E[] a = array.clone();
					prep();
					res = f.apply(a);
					endTimer();
					results.add(new SortStats(tracker));
					shuffle(array);
				}
				end();
				return res;
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
				E val = null;
				for (int i = 0; i < NUM_TRIALS; ++i) {
					E[] a = array.clone();
					prep();
					val = (E) (resultVal = f.apply(a, k));
					endTimer();
					results.add(new SortStats(tracker));
					shuffle(array);
				}
				end();
				return val;
			}
		}
	}
}
