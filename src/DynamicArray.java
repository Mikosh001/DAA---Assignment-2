public final class DynamicArray {
    private static final int DEFAULT_CAPACITY = 10;

    private int[] elements;
    private int size;
    private long accesses;
    private long comparisons;
    private long movements;

    public DynamicArray() {
        elements = new int[DEFAULT_CAPACITY];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(int value) {
        add(size, value);
    }

    public void add(int index, int value) {
        checkPositionIndex(index);
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
            movements++;
        }
        elements[index] = value;
        movements++;
        size++;
    }

    public int remove(int index) {
        checkElementIndex(index);
        int removed = elements[index];
        accesses++;
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
            movements++;
        }
        size--;
        return removed;
    }

    public int get(int index) {
        checkElementIndex(index);
        accesses++;
        return elements[index];
    }

    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            comparisons++;
            if (elements[i] == value) {
                return true;
            }
        }
        return false;
    }

    public void resetMetrics() {
        accesses = 0;
        comparisons = 0;
        movements = 0;
    }

    public long getAccesses() {
        return accesses;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getMovements() {
        return movements;
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity <= elements.length) {
            return;
        }
        int newCapacity = Math.max(requiredCapacity, elements.length * 2);
        int[] expanded = new int[newCapacity];
        for (int i = 0; i < size; i++) {
            expanded[i] = elements[i];
            movements++;
        }
        elements = expanded;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }
}
