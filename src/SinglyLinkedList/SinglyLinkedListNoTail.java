package SinglyLinkedList;

class Node<T> {
    T data;
    Node<T> next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}

public class SinglyLinkedListNoTail<T> {
    private Node<T> head;
    private int size;

    public SinglyLinkedListNoTail() {
        this.head = null;
        this.size = 0;
    }

    // Insertar al inicio: O(1)
    public void pushFront(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = head;
        head = newNode;
        size++;
    }

    // Insertar al final: O(n)
    public void pushBack(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            size++;
            return;
        }
        Node<T> temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = newNode;
        size++;
    }

    // Eliminar el primero: O(1)
    public T popFront() {
        if (head == null)
            return null;
        T ret = head.data;
        head = head.next;
        size--;
        return ret;
    }

    // Eliminar el último: O(n)
    public T popBack() {
        if (head == null)
            return null;
        if (head.next == null) {
            T ret = head.data;
            head = null;
            size--;
            return ret;
        }
        Node<T> temp = head;
        while (temp.next.next != null) {
            temp = temp.next;
        }
        T ret = temp.next.data;
        temp.next = null;
        size--;
        return ret;
    }

    // Buscar un elemento: O(n)
    public Node<T> find(T key) {
        Node<T> temp = head;
        while (temp != null) {
            if (temp.data.equals(key))
                return temp;
            temp = temp.next;
        }
        return null;
    }

    // Insertar después de un nodo dado: O(1) una vez localizado el nodo
    public void addAfter(Node<T> key, T data) {
        if (key == null)
            return;
        Node<T> newNode = new Node<>(data);
        newNode.next = key.next;
        key.next = newNode;
        size++;
    }

    // Insertar antes de un nodo dado: O(n)
    public void addBefore(Node<T> key, T data) {
        if (head == null || key == null)
            return;
        if (head == key) {
            pushFront(data);
            return;
        }
        Node<T> temp = head;
        while (temp.next != key) {
            if (temp.next == null)
                return;
            temp = temp.next;
        }
        Node<T> newNode = new Node<>(data);
        newNode.next = temp.next;
        temp.next = newNode;
        size++;
    }

    // Eliminar el primer nodo con ese valor: O(n)
    public void erase(T key) {
        if (head == null)
            return;
        if (head.data.equals(key)) {
            head = head.next;
            size--;
            return;
        }
        Node<T> temp = head;
        while (temp.next != null) {
            if (temp.next.data.equals(key)) {
                temp.next = temp.next.next;
                size--;
                return;
            }
            temp = temp.next;
        }
    }

    public boolean empty() {
        return head == null;
    }

    public int size() {
        return size;
    }
}