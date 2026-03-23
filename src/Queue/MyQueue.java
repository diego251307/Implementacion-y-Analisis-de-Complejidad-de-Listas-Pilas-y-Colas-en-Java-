package Queue;

public class MyQueue<T> {

    private Object[] arreglo;
    private int size = 0;
    private int head;
    private int tail;

    public MyQueue() {
        arreglo = new Object[2];
        head = 0;
        tail = 0;
    }

    // enqueue
    public void enqueue(T x) {
        if (size >= arreglo.length) {
            Object[] nArreglo = new Object[arreglo.length * 2];
            for (int i = 0; i < size; i++) {
                nArreglo[i] = arreglo[(head + i) % arreglo.length];
            }
            head = 0;
            tail = size;
            arreglo = nArreglo;
        }
        arreglo[tail] = x;
        tail = (tail + 1) % arreglo.length;
        size++;
    }

    // dequeue
    @SuppressWarnings("unchecked")
    public T dequeue() {
        T dq = (T) arreglo[head];
        arreglo[head] = null;
        head = (head + 1) % arreglo.length;
        size--;
        return dq;
    }

    // front
    @SuppressWarnings("unchecked")
    public T front() {
        if (!isEmpty()) return (T) arreglo[head];
        return null;
    }

    // isEmpty
    public boolean isEmpty() {
        return size <= 0;
    }

    // size
    public int size() {
        return size;
    }

    // delete
    public void delete(T n) {
        for (int i = 0; i < size; i++) {
            int indx = (head + i) % arreglo.length;
            if (n.equals(arreglo[indx])) {
                for (int j = i; j < size - 1; j++) {
                    arreglo[(head + j) % arreglo.length] = arreglo[(head +
                        j +
                        1) %
                    arreglo.length];
                }
                arreglo[(head + size - 1) % arreglo.length] = null;
                tail = (tail - 1 + arreglo.length) % arreglo.length;
                size--;
                return;
            }
        }
    }
}
