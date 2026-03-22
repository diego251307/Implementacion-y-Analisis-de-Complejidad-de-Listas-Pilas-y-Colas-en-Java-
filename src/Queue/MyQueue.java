package Queue;

public class MyQueue<T> {

    private Object[] arreglo;
    private int size = 0;

    public MyQueue() {
        arreglo = new Object[2];
    }

    // enqueue
    public void enqueue(T x) {
        if (size >= arreglo.length) {
            Object[] nArreglo = new Object[arreglo.length * 2];
            for (int i = 0; i < arreglo.length; i++) {
                nArreglo[i] = arreglo[i];
            }
            arreglo = nArreglo;
        }
        arreglo[size] = x;
        size++;
    }

    // dequeue
    @SuppressWarnings("unchecked")
    public T dequeue() {
        T dq = (T) arreglo[0];
        for (int i = 0; i < size - 1; i++) {
            arreglo[i] = arreglo[i + 1];
        }
        arreglo[size - 1] = null;
        size--;
        return dq;
    }

    // front
    @SuppressWarnings("unchecked")
    public T front() {
        if (!isEmpty())
            return (T) arreglo[0];
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
            if (n.equals(arreglo[i])) {
                for (int j = i; j < size - 1; j++) {
                    arreglo[j] = arreglo[j + 1];
                }
                arreglo[size - 1] = null;
                size--;
                return;
            }
        }
    }

}