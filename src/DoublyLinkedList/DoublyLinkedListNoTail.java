package DoublyLinkedList;

public class DoublyLinkedListNoTail<T> {

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
    private int size;

    public DoublyLinkedListNoTail() {
        this.head = null;
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

    // topBack O(n)
    public T topBack() {
        if (isEmpty())
            throw new RuntimeException("Lista vacía");
        Node<T> current = head;
        while (current.next != null)
            current = current.next;
        return current.data;
    }

    // pushFront O(1)
    public void pushFront(T data) {
        Node<T> newNode = new Node<>(data);
        if (!isEmpty()) {
            newNode.next = head;
            head.prev = newNode;
        }
        head = newNode;
        size++;
    }

    // pushBack O(n)
    public void pushBack(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null)
                current = current.next;
            current.next = newNode;
            newNode.prev = current;
        }
        size++;
    }

    // popFront O(1)
    public T popFront() {
        if (isEmpty())
            throw new RuntimeException("Lista vacía");
        T data = head.data;
        head = head.next;
        if (head != null)
            head.prev = null;
        size--;
        return data;
    }

    // popBack O(n)
    public T popBack() {
        if (isEmpty())
            throw new RuntimeException("Lista vacía");
        if (head.next == null) {
            T data = head.data;
            head = null;
            size--;
            return data;
        }
        Node<T> current = head;
        while (current.next.next != null)
            current = current.next;
        T data = current.next.data;
        current.next = null;
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

    // eraseNode O(1). Llamado dentro de erase para esta aplicacion
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
        }
        size--;
    }

    // addBefore O(n)
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

    // addAfter O(n)
    public void addAfter(T reference, T newData) {
        Node<T> target = find(reference);
        if (target == null)
            throw new RuntimeException("Referencia no encontrada");
        Node<T> newNode = new Node<>(newData);
        newNode.prev = target;
        newNode.next = target.next;
        if (target.next != null) {
            target.next.prev = newNode;
        }
        target.next = newNode;
        size++;
    }

    // print O(n). No probado en main pero de utilidad como extra
    public void print() {
        Node<T> current = head;
        System.out.print("null <-> ");
        while (current != null) {
            System.out.print(current.data + " <-> ");
            current = current.next;
        }
        System.out.println("null");
    }
}
