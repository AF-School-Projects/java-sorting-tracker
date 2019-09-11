import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
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
	 * 
	 * @author Alex Feaser
	 */
	final static class Sorter {

		private static final int STRING_LEN = 12;
		protected static int NEW_ARRAY_LEN;

		public static <E extends Comparable<? super E>> E[] shuffle(E[] array) {
			Collections.shuffle(Arrays.asList(array));
			return array;
		}
		
		@SuppressWarnings("unchecked")
		public static <E extends Comparable<? super E>> E[] randomize(E[] array) {
			if (array instanceof String[]) {
				array = (E[]) new String[NEW_ARRAY_LEN];
				String[] a = (String[]) array;
				for (int n = 0; n < array.length; ++n) {
					char[] arr = new char[STRING_LEN];
					for (int i = 0 ; i < STRING_LEN; ++i)
						arr[i] = Character.valueOf((char) (new Random().nextInt(26) + 97));
					a[n] = new String(arr);
				}
			} else if (array instanceof Integer[]) {
				array = (E[]) new Integer[NEW_ARRAY_LEN];
				Integer[] a = (Integer[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = new Random().nextInt();
			} else if (array instanceof Double[]) {
				array = (E[]) new Double[NEW_ARRAY_LEN];
				Double[] a = (Double[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = new Random().nextDouble();
			} else if (array instanceof Character[]) {
				array = (E[]) new Character[NEW_ARRAY_LEN];
				Character[] a = (Character[]) array;
				for(int i = 0; i < array.length; ++i)
					a[i] = Character.valueOf((char) (new Random().nextInt(26) + 97));
			}
			return array;
		}

		/*
		 * Swaps elements at argument indices within argument array
		 */
		private static <E> void swap(E[] array, int a, int b) {
			int len = array.length;
			if (a >= len || b >= len || a < 0 || b < 0)
				throw new NullPointerException();
			E c = array[a];
			array[a] = array[b];
			array[b] = c;		
		}

		/**
		 * Perform an insertion sort on argument array 
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E[] insertionSort(E[] array) {
			for (int i = 1, j; i < array.length; i++) {  
				E key = array[i];
				for (j = i - 1; j >= 0 && array[j].compareTo(key) > 0; --j)
					array[j + 1] = array[j];
				array[j + 1] = key;  
			}
			return array;
		}

		/**
		 * Perform an insertion sort on argument array and return the kth smallest element
		 * 
		 * @param <E>
		 * @param array
		 * @return kth smallest element of argument array
		 */
		public static <E extends Comparable<? super E>> E insertionSortK(E[] array, int k) {
			return insertionSort(array)[k - 1];
		}

		/**
		 * Perform a selection sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E[] selectionSort(E[] array) {
			int len = array.length - 1, indexMin = 0;
			E minVal;
			for (int i = 0; i < len; i++) { 
				minVal = array[indexMin = i];
				for (int j = i; j <= len; j++) 
					if (minVal.compareTo(array[j]) > 0) 
						minVal = array[indexMin = j];
				if (indexMin != i)
					swap(array, i, indexMin);
			}
			return array;
		}

		/**
		 * Perform a selection sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E selectionSortK(E[] array, int k) {
			int len = array.length - 1, indexMin = 0;
			E minVal;
			for (int i = 0; i < k; i++) { 
				minVal = array[indexMin = i];
				for (int j = i; j <= len; j++) 
					if (minVal.compareTo(array[j]) > 0) 
						minVal = array[indexMin = j];
				if (indexMin != i)
					swap(array, i, indexMin);
			}
			return array[k - 1];
		}

		/**
		 * Perform a bubble sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E[] bubbleSort(E[] array) {
			int len = array.length - 1, swaps = 0;
			for (int i = -1; i < len; len--) { 
				swaps = 0;
				while(++i < len)
					if (array[i].compareTo(array[i + 1]) > 0) 
						swap(array, (swaps++ & 0) + i, i + 1);
				i = -1;
				if (swaps == 0)
					return array;
			}
			return array;
		}

		/**
		 * Perform a bubble sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E bubbleSortK(E[] array, int k) {
			int len = array.length - 1, swaps = 0;
			for (int i = -1; i < len && len >= k - 1; len--) { 
				swaps = 0;
				while(++i < len)
					if (array[i].compareTo(array[i + 1]) > 0) 
						swap(array, (swaps++ & 0) + i, i + 1);
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
			quickSorter(array, 0, array.length - 1);
			return array;
		}

		/**
		 * Perform a quick sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E quickSortK(E[] array, int k) {
			quickSorterK(array, 0, array.length - 1, k);
			return array[k - 1];
		}

		/**
		 * Perform a quick sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @param lIndex
		 * @param rIndex
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] quickSorter(E[] array, int lIndex, int rIndex) {
			if (lIndex >= rIndex)
				return array;
			int pivot = partition(array, lIndex, rIndex);
			quickSorter(array, lIndex, pivot - 1);
			quickSorter(array, pivot + 1, rIndex);
			return array;
		}

		/**
		 * Perform a quick selection on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @param lIndex
		 * @param rIndex
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] quickSorterK(E[] array, int lIndex, int rIndex, int k) {
			if (k > 0 && k <= rIndex - lIndex + 1) {
				int pivot = partition(array, lIndex, rIndex);
				if (pivot - lIndex == k - 1)
					return array;
				if (pivot - lIndex > k - 1)
					return quickSorterK(array, lIndex, pivot - 1, k);
				return quickSorterK(array, pivot + 1, rIndex, k - pivot + lIndex - 1);
			}
			return null;
		}

		 /**
		  * Used by quick sorts
		  * 
		  * @param <E>
		  * @param array
		  * @param sIndex
		  * @param eIndex
		  * @return
		  */
		private static <E extends Comparable<? super E>> int partition(E[] array, int sIndex, int eIndex) {
			E pivot = array[eIndex];
			int pIndex = sIndex;
			for (int i = sIndex; i < eIndex; i++)
				if (array[i].compareTo(pivot) <= 0)
					swap(array, i, pIndex++);
			swap(array, pIndex, eIndex);
			return pIndex;
		}

		/**
		 * Perform a merge sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E[] mergeSort(E[] array, boolean multiThread) {
			return multiThread ? multithreadedMergeSort(array) : mergeSorter(array);
		}

		/**
		 * Perform a merge sort up to k on argument array and return k
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E mergeSortK(E[] array, boolean multiThread, int k) {
			return multiThread ? multithreadedMergeSortK(array, k)[k - 1] : mergeSorterK(array, k, true)[k - 1];
		}
		
		/**
		 * Used by merge sort
		 * @param <E>
		 * @param array
		 * @return
		 */
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
			mergeSorter(tempLeftArray);
			mergeSorter(tempRightArray);
			return merge(tempLeftArray, tempRightArray, array);
//			return array;
		}
		
		/**
		 * Used by merge sort k
		 * @param <E>
		 * @param array
		 * @return
		 */
		private static <E extends Comparable<? super E>> E[] mergeSorterK(E[] array, int k, boolean firstRun) {
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
			mergeSorterK(tempL, k, false);
			mergeSorterK(tempR, k, false);
			return firstRun ? mergeK(tempL, tempR, array, k) : merge(tempL, tempR, array);
		}

		/**
		 * Used by merge sort
		 * 
		 * @param <E>
		 * @param tempL
		 * @param tempR
		 * @param array
		 */
		private static <E extends Comparable<? super E>> E[] merge(E[] tempL, E[] tempR, E[] array) {
			int leftlen = tempL.length, rightlen = tempR.length;
			int leftIndex = 0, rightIndex = 0, index = 0;
			while (leftIndex < leftlen && rightIndex < rightlen)
				if (tempL[leftIndex].compareTo(tempR[rightIndex]) <= 0)
					array[index++] = tempL[leftIndex++];
				else
					array[index++] = tempR[rightIndex++];
			while (leftIndex < leftlen)
				array[index++] = tempL[leftIndex++];
			while (rightIndex < rightlen)
				array[index++] = tempR[rightIndex++];
			return array;
		}

		/**
		 * Used by merge sort k
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
				if (index == k)
					return array;
			}
			while (leftIndex < leftlen) {
				array[index++] = tempL[leftIndex++];
				if (index == k)
					return array;
			}
			while (rightIndex < rightlen) {
				array[index++] = tempR[rightIndex++];
				if (index == k)
					return array;
			}
			return array;
		}
		
		/**
		 * Perform a multithreaded merge sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] multithreadedMergeSort(E[] array) {
			ForkJoinPool.commonPool().invoke(new ForkJoinMergeSort<E>(array));
			return array;
		}
		
		/**
		 * Perform a multithreaded merge sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		private static <E extends Comparable<? super E>> E[] multithreadedMergeSortK(E[] array, int k) {
			ForkJoinPool.commonPool().invoke(new ForkJoinMergeSort<E>(array, k));
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
		 * Perform a heap sort on argument array
		 * 
		 * @param <E>
		 * @param array
		 * @return reference to the sorted array
		 */
		public static <E extends Comparable<? super E>> E heapSortK(E[] array, int k) {
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
		 * Used by heap sort
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
	        if (l < n && array[l].compareTo(array[largest]) > 0)
	            largest = l;
	        if (r < n && array[r].compareTo(array[largest])  > 0)
	            largest = r;
	        if (largest != i) {
	        	swap(array, i, largest);
	            heapify(array, n, largest); 
	        }
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
			private int thread_id;
			private final int k;
		 
			public ForkJoinMergeSort(T[] v) {
				this(v, 0);
			}
			
			public ForkJoinMergeSort(T[] v, int k) {
				this.v = v;
				this.k = k;
				thread_id = 0;
			}

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
					return k != 0 && ++thread_id == 1 ? mergeK(leftSort.join(), rightSort.join(), v, k) : merge(leftSort.join(), rightSort.join(), v);
				}
			}

			private T[] merge(T[] left, T[] right, T[] arr) {		
				int iLeft = 0, iRight = 0;
				for (int i = 0; i < arr.length; i++) {
					if (iRight >= right.length || 
							(iLeft < left.length && left[iLeft].compareTo(right[iRight]) < 0))
						arr[i] = left[iLeft++];
					else
						arr[i] = right[iRight++];
				}
				return arr;
			}

			private T[] mergeK(T[] left, T[] right, T[] arr, int k) {		
				int iLeft = 0, iRight = 0;
				for (int i = 0; i < k; i++) {
					if (iRight >= right.length || 
							(iLeft < left.length && left[iLeft].compareTo(right[iRight]) < 0))
						arr[i] = left[iLeft++];
					else
						arr[i] = right[iRight++];
				}
				return arr;
			}
		}
		
		private class SortStats {
			protected long arrayAccesses;
			protected long swaps;
			protected long comparisons;
			protected Date startTime;
			protected Date elapsedTime;
		}
	}
}
