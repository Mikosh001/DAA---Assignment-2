import java.util.NoSuchElementException;

public final class MinHeap {
    private static final int DEFAULT_CAPACITY = 10;

    private int[] elements;
    private int size;
    private long comparisons;

    public MinHeap() {
        elements = new int[DEFAULT_CAPACITY];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void insert(int value) {
        ensureCapacity(size + 1);
        elements[size] = value;
        siftUp(size);
        size++;
    }

    public int peekMin() {
        checkNotEmpty();
        return elements[0];
    }

    public int extractMin() {
        checkNotEmpty();
        int minimum = elements[0];
        size--;
        if (size > 0) {
            elements[0] = elements[size];
            siftDown(0);
        }
        return minimum;
    }

    public void resetMetrics() {
        comparisons = 0;
    }

    public long getComparisons() {
        return comparisons;
    }

    boolean isValidHeap() {
        for (int child = 1; child < size; child++) {
            int parent = (child - 1) / 2;
            if (elements[parent] > elements[child]) {
                return false;
            }
        }
        return true;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            comparisons++;
            if (elements[parent] <= elements[index]) {
                break;
            }
            swap(parent, index);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = index * 2 + 1;
            if (left >= size) {
                return;
            }
            int right = left + 1;
            int smallerChild = left;
            if (right < size) {
                comparisons++;
                if (elements[right] < elements[left]) {
                    smallerChild = right;
                }
            }
            comparisons++;
            if (elements[index] <= elements[smallerChild]) {
                return;
            }
            swap(index, smallerChild);
            index = smallerChild;
        }
    }

    private void swap(int first, int second) {
        int temporary = elements[first];
        elements[first] = elements[second];
        elements[second] = temporary;
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity <= elements.length) {
            return;
        }
        int[] expanded = new int[Math.max(requiredCapacity, elements.length * 2)];
        System.arraycopy(elements, 0, expanded, 0, size);
        elements = expanded;
    }

    private void checkNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("heap is empty");
        }
    }
}

