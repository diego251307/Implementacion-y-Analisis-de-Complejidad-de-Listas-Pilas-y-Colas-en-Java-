package Stack;

public class DynamicArrayStack<T> implements MyStack<T> {

    private Object[] data;
    private int size;
    private int capacity;

    private static final int INITIAL_CAPACITY = 8;

    public DynamicArrayStack() {
        this.capacity = INITIAL_CAPACITY;
        this.data = new Object[capacity];
        this.size = 0;
    }

    private void resize() {
        capacity = capacity * 2;
        Object[] newData = new Object[capacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    @Override
    public void push(T x) {
        if (size == capacity) {
            resize();
        }
        data[size] = x;
        size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) throw new RuntimeException("Pila vacía");
        T top = (T) data[size - 1];
        data[size - 1] = null;
        size--;
        return top;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new RuntimeException("Pila vacía");
        return (T) data[size - 1];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void delete(T n) {
        for (int i = size - 1; i >= 0; i--) {
            if (((T) data[i]).equals(n)) {
                for (int j = i; j < size - 1; j++) {
                    data[j] = data[j + 1];
                }
                data[size - 1] = null;
                size--;
                return;
            }
        }
    }

    public int capacity() {
        return capacity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        if (isEmpty()) return "Pila vacía []";
        StringBuilder sb = new StringBuilder("Cima → [");
        for (int i = size - 1; i >= 0; i--) {
            sb.append(data[i]);
            if (i > 0) sb.append(", ");
        }
        sb.append("] ← Fondo");
        return sb.toString();
    }
}
