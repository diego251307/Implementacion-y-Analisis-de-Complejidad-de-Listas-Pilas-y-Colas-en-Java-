package DoublyLinkedList;

public class DoublyLinkedListNoTail<T> {

    private Node<T> head;
    private int size;

    public DoublyLinkedListNoTail() {
        this.head = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    public T topFront() {
        if (isEmpty()) throw new RuntimeException("Lista vacía");
        return head.data;
    }

    public T topBack() {
        if (isEmpty()) throw new RuntimeException("Lista vacía");
        Node<T> current = head;
        while (current.next != null) current = current.next;
        return current.data;
    }

    public void pushFront(T data) {
        Node<T> newNode = new Node<>(data);
        if (!isEmpty()) {
            newNode.next = head;
            head.prev = newNode;
        }
        head = newNode;
        size++;
    }

    public void pushBack(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
            newNode.prev = current;
        }
        size++;
    }

    public T popFront() {
        if (isEmpty()) throw new RuntimeException("Lista vacía");
        T data = head.data;
        head = head.next;
        if (head != null) head.prev = null;
        size--;
        return data;
    }

    public T popBack() {
        if (isEmpty()) throw new RuntimeException("Lista vacía");
        if (head.next == null) {
            T data = head.data;
            head = null;
            size--;
            return data;
        }
        Node<T> current = head;
        while (current.next.next != null) current = current.next;
        T data = current.next.data;
        current.next = null;
        size--;
        return data;
    }

    public Node<T> find(T data) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(data)) return current;
            current = current.next;
        }
        return null;
    }

    public void erase(T data) {
        Node<T> target = find(data);
        if (target == null) return;
        eraseNode(target);
    }

    public void eraseNode(Node<T> target) {
        if (target == null) return;
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

    public void addBefore(T reference, T newData) {
        Node<T> target = find(reference);
        if (target == null) throw new RuntimeException("Referencia no encontrada");
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

    public void addAfter(T reference, T newData) {
        Node<T> target = find(reference);
        if (target == null) throw new RuntimeException("Referencia no encontrada");
        Node<T> newNode = new Node<>(newData);
        newNode.prev = target;
        newNode.next = target.next;
        if (target.next != null) {
            target.next.prev = newNode;
        }
        target.next = newNode;
        size++;
    }

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
