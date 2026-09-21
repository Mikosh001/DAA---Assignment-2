public final class LinkedList {
    private Node head;
    private Node tail;
    private int size;
    private long accesses;
    private long comparisons;

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
        Node added = new Node(value);
        if (index == 0) {
            added.next = head;
            head = added;
            if (size == 0) {
                tail = added;
            }
        } else if (index == size) {
            tail.next = added;
            tail = added;
        } else {
            Node previous = nodeAt(index - 1);
            added.next = previous.next;
            previous.next = added;
        }
        size++;
    }

    public int remove(int index) {
        checkElementIndex(index);
        Node removed;
        if (index == 0) {
            removed = head;
            accesses++;
            head = head.next;
            if (size == 1) {
                tail = null;
            }
        } else {
            Node previous = nodeAt(index - 1);
            removed = previous.next;
            accesses++;
            previous.next = removed.next;
            if (index == size - 1) {
                tail = previous;
            }
        }
        size--;
        return removed.value;
    }

    public int get(int index) {
        checkElementIndex(index);
        return nodeAt(index).value;
    }

    public boolean contains(int value) {
        Node current = head;
        while (current != null) {
            accesses++;
            comparisons++;
            if (current.value == value) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void resetMetrics() {
        accesses = 0;
        comparisons = 0;
    }

    public long getAccesses() {
        return accesses;
    }

    public long getComparisons() {
        return comparisons;
    }

    private Node nodeAt(int index) {
        Node current = head;
        accesses++;
        for (int i = 0; i < index; i++) {
            current = current.next;
            accesses++;
        }
        return current;
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

    private static final class Node {
        private final int value;
        private Node next;

        private Node(int value) {
            this.value = value;
        }
    }
}

