package Stack;

public class DoublyLinkedListStack<T> implements MyStack<T> {

    private static class Node<T> {
        T data;
        Node<T> next;
        Node<T> prev;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public DoublyLinkedListStack() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    @Override
    public void push(T x) {
        Node<T> newNode = new Node<>(x);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    @Override
    public T pop() {
        if (isEmpty()) throw new RuntimeException("Pila vacia");

        T value = tail.data;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
        return value;
    }

    @Override
    public T peek() {
        if (isEmpty()) throw new RuntimeException("Pila vacia");
        return tail.data;
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
    public void delete(T n) {
        Node<T> current = tail;
        while (current != null) {
            if (equalsValue(current.data, n)) {
                unlink(current);
                return;
            }
            current = current.prev;
        }
    }

    private boolean equalsValue(T a, T b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    private void unlink(Node<T> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        size--;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Pila vacia []";

        StringBuilder sb = new StringBuilder("Cima -> [");
        Node<T> current = tail;
        while (current != null) {
            sb.append(current.data);
            if (current.prev != null) sb.append(", ");
            current = current.prev;
        }
        sb.append("] <- Fondo");
        return sb.toString();
    }
}
