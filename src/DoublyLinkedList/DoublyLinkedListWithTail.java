package DoublyLinkedList;

public class DoublyLinkedListWithTail<T> {

    private class Node<T> {
        private T data;
        private Node<T> next;
        private Node<T> prev;

        private Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public DoublyLinkedListWithTail() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // isEmpty O(1)
    public boolean isEmpty() {
        return head == null;
    }

    // size O(1)
    public int size() {
        return size;
    }

    // topFront O(1)
    public T topFront() {
        if (isEmpty())
            throw new RuntimeException("Lista vacía");
        return head.data;
    }

    // topBack O(1)
    public T topBack() {
        if (isEmpty())
            throw new RuntimeException("Lista vacía");
        return tail.data;
    }

    // pushFront O(1)
    public void pushFront(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    // pushBack O(1)
    public void pushBack(T data) {
        Node<T> newNode = new Node<>(data);
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

    // popFront O(1)
    public T popFront() {
        if (isEmpty())
            throw new RuntimeException("Lista vacía");
        T data = head.data;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        size--;
        return data;
    }

    // popBack O(1)
    public T popBack() {
        if (isEmpty())
            throw new RuntimeException("Lista vacía");
        T data = tail.data;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
        return data;
    }

    // find O(n)
    public Node<T> find(T data) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(data))
                return current;
            current = current.next;
        }
        return null;
    }

    // erase O(n)
    public void erase(T data) {
        Node<T> target = find(data);
        if (target == null)
            return;
        eraseNode(target);
    }

    // eraseNode O(1) Se llama después de encontrar el nodo, se asume que el nodo
    // existe
    public void eraseNode(Node<T> target) {
        if (target == null)
            return;
        if (target.prev != null) {
            target.prev.next = target.next;
        } else {
            head = target.next;
        }
        if (target.next != null) {
            target.next.prev = target.prev;
        } else {
            tail = target.prev;
        }
        size--;
    }

    // addBefore O(n) utiliza find O(n) + O(1) para insertar
    public void addBefore(T reference, T newData) {
        Node<T> target = find(reference);
        if (target == null)
            throw new RuntimeException("Referencia no encontrada");
        Node<T> newNode = new Node<>(newData);
        newNode.next = target;
        newNode.prev = target.prev;
        if (target.prev != null) {
            target.prev.next = newNode;
        } else {
            head = newNode;
        }
        target.prev = newNode;
        size++;
    }

    // addAfter O(n) se utiliza find O(n) + O(1) para insertar
    public void addAfter(T reference, T newData) {
        Node<T> target = find(reference);
        if (target == null)
            throw new RuntimeException("Referencia no encontrada");
        Node<T> newNode = new Node<>(newData);
        newNode.prev = target;
        newNode.next = target.next;
        if (target.next != null) {
            target.next.prev = newNode;
        } else {
            tail = newNode;
        }
        target.next = newNode;
        size++;
    }

    // print O(n). No probado pero de utilidad como extra
    public void print() {
        Node<T> current = head;
        System.out.print("null <-> ");
        while (current != null) {
            System.out.print(current.data + " <-> ");
            current = current.next;
        }
        System.out.println("null   [tail=" + (tail != null ? tail.data : "null") + "]");
    }
}
