import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import org.junit.jupiter.api.Test;

class DataStructuresTest {
    @Test
    void dynamicArrayRejectsInvalidIndicesWhenEmpty() {
        DynamicArray array = new DynamicArray();
        assertTrue(array.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 5));
    }

    @Test
    void dynamicArraySupportsAllRequiredOperations() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        assertEquals(1, array.size());
        assertEquals(10, array.get(0));
        array.add(20);
        array.add(1, 15);
        array.add(15);
        assertEquals(4, array.size());
        assertEquals(10, array.get(0));
        assertEquals(15, array.get(1));
        assertEquals(20, array.get(2));
        assertTrue(array.contains(15));
        assertFalse(array.contains(99));
        assertEquals(15, array.remove(1));
        assertEquals(15, array.remove(2));
        assertEquals(2, array.size());
    }

    @Test
    void dynamicArrayMatchesArrayListForRandomOperations() {
        DynamicArray actual = new DynamicArray();
        ArrayList<Integer> expected = new ArrayList<Integer>();
        Random random = new Random(42);
        for (int operation = 0; operation < 5_000; operation++) {
            int choice = expected.isEmpty() ? 0 : random.nextInt(5);
            if (choice == 0) {
                int value = random.nextInt(21) - 10;
                actual.add(value);
                expected.add(value);
            } else if (choice == 1) {
                int index = random.nextInt(expected.size() + 1);
                int value = random.nextInt(21) - 10;
                actual.add(index, value);
                expected.add(index, value);
            } else if (choice == 2) {
                int index = random.nextInt(expected.size());
                assertEquals((int) expected.remove(index), actual.remove(index));
            } else if (choice == 3) {
                int index = random.nextInt(expected.size());
                assertEquals((int) expected.get(index), actual.get(index));
            } else {
                int value = random.nextInt(21) - 10;
                assertEquals(expected.contains(value), actual.contains(value));
            }
            assertEquals(expected.size(), actual.size());
        }
    }

    @Test
    void dynamicArrayHandlesLargeInputAndTracksMetrics() {
        DynamicArray array = new DynamicArray();
        for (int i = 0; i < 100_000; i++) {
            array.add(i);
        }
        array.resetMetrics();
        assertEquals(99_999, array.get(99_999));
        assertTrue(array.contains(99_999));
        assertEquals(1, array.getAccesses());
        assertEquals(100_000, array.getComparisons());
    }

    @Test
    void linkedListRejectsInvalidIndicesWhenEmpty() {
        LinkedList list = new LinkedList();
        assertTrue(list.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 5));
    }

    @Test
    void linkedListSupportsAllRequiredOperations() {
        LinkedList list = new LinkedList();
        list.add(10);
        assertEquals(1, list.size());
        assertEquals(10, list.get(0));
        list.add(20);
        list.add(1, 15);
        list.add(15);
        assertEquals(4, list.size());
        assertEquals(10, list.get(0));
        assertEquals(15, list.get(1));
        assertEquals(20, list.get(2));
        assertTrue(list.contains(15));
        assertFalse(list.contains(99));
        assertEquals(15, list.remove(1));
        assertEquals(15, list.remove(2));
        assertEquals(2, list.size());
    }

    @Test
    void linkedListMatchesJavaLinkedListForRandomOperations() {
        LinkedList actual = new LinkedList();
        java.util.LinkedList<Integer> expected = new java.util.LinkedList<Integer>();
        Random random = new Random(42);
        for (int operation = 0; operation < 3_000; operation++) {
            int choice = expected.isEmpty() ? 0 : random.nextInt(5);
            if (choice == 0) {
                int value = random.nextInt(21) - 10;
                actual.add(value);
                expected.add(value);
            } else if (choice == 1) {
                int index = random.nextInt(expected.size() + 1);
                int value = random.nextInt(21) - 10;
                actual.add(index, value);
                expected.add(index, value);
            } else if (choice == 2) {
                int index = random.nextInt(expected.size());
                assertEquals((int) expected.remove(index), actual.remove(index));
            } else if (choice == 3) {
                int index = random.nextInt(expected.size());
                assertEquals((int) expected.get(index), actual.get(index));
            } else {
                int value = random.nextInt(21) - 10;
                assertEquals(expected.contains(value), actual.contains(value));
            }
            assertEquals(expected.size(), actual.size());
        }
    }

    @Test
    void linkedListHandlesLargeInputAndTracksAccesses() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 100_000; i++) {
            list.add(i);
        }
        list.resetMetrics();
        assertEquals(99_999, list.get(99_999));
        assertEquals(100_000, list.getAccesses());
    }

    @Test
    void minHeapRejectsEmptyOperations() {
        MinHeap heap = new MinHeap();
        assertTrue(heap.isEmpty());
        assertThrows(NoSuchElementException.class, heap::peekMin);
        assertThrows(NoSuchElementException.class, heap::extractMin);
    }

    @Test
    void minHeapHandlesOneElementAndDuplicates() {
        MinHeap heap = new MinHeap();
        heap.insert(4);
        assertEquals(4, heap.peekMin());
        assertEquals(4, heap.extractMin());
        heap.insert(3);
        heap.insert(3);
        heap.insert(1);
        assertEquals(1, heap.extractMin());
        assertEquals(3, heap.extractMin());
        assertEquals(3, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void heapPropertyHoldsAfterEveryInsertionAndExtraction() {
        MinHeap heap = new MinHeap();
        Random random = new Random(42);
        for (int i = 0; i < 2_000; i++) {
            heap.insert(random.nextInt(101) - 50);
            assertTrue(heap.isValidHeap());
        }
        int previous = Integer.MIN_VALUE;
        while (!heap.isEmpty()) {
            int current = heap.extractMin();
            assertTrue(current >= previous);
            assertTrue(heap.isValidHeap());
            previous = current;
        }
    }

    @Test
    void minHeapMatchesPriorityQueueForLargeRandomInput() {
        MinHeap actual = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<Integer>();
        Random random = new Random(42);
        for (int i = 0; i < 100_000; i++) {
            int value = random.nextInt();
            actual.insert(value);
            expected.add(value);
        }
        while (!expected.isEmpty()) {
            assertEquals((int) expected.remove(), actual.extractMin());
        }
        assertTrue(actual.isEmpty());
    }

    @Test
    void boundaryIndicesRemainValidAfterChanges() {
        DynamicArray array = new DynamicArray();
        LinkedList list = new LinkedList();
        for (int value = 0; value < 20; value++) {
            array.add(value);
            list.add(value);
        }
        array.add(0, -1);
        list.add(0, -1);
        array.add(array.size(), 20);
        list.add(list.size(), 20);
        assertEquals(-1, array.remove(0));
        assertEquals(-1, list.remove(0));
        assertEquals(20, array.remove(array.size() - 1));
        assertEquals(20, list.remove(list.size() - 1));
    }
}
