import java.util.Iterator;

public class CustomArrayList<A> implements CustomList<A>, Iterable<A> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final double EXPANSION_FACTOR = 1.5;

    private Object[] elements;
    private int size;

    public CustomArrayList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public CustomArrayList(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    @Override
    public void add(A element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }

        if (size == elements.length) {
            int newCapacity = (int) (elements.length * EXPANSION_FACTOR) + 1;
            Object[] newArray = new Object[newCapacity];
            System.arraycopy(elements, 0, newArray, 0, size);
            elements = newArray;
        }

        elements[size] = element;
        size++;
    }

    @Override
    public A get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(String.format("Index: %d, Size: %d", index, size));
        }
        return (A) elements[index];
    }

    @Override
    public A remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(String.format("Index: %d, Size: %d", index, size));
        }

        A removedElement = (A) elements[index];

        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        elements[size - 1] = null;
        size--;

        return removedElement;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<A> iterator() {
        return new Iterator<A>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public A next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                return (A) elements[currentIndex++];
            }
        };
    }
}