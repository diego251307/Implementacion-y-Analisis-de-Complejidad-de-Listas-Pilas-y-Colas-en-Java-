package SinglyLinkedList;

// Clase que representa cada elemento de la lista
class Node<T> {
    T data;
    Node<T> next;
    Node<T> prev;

    public Node(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}

// Clase principal de la estructura
public class SinglyLinkedListWithTail<T> {
    private Node<T> head; // Conocemos el primer nodo
    private Node<T> tail; // Conocemos el último nodo

    public SinglyLinkedListWithTail() {
        this.head = null;
        this.tail = null;
        this.size = 0;

    }

    private int size;

    // PushFront: O(1) - Muy rápido
    public void pushFront(T data) {
        Node<T> newNode = new Node<>(data);
        if (empty())
            tail = newNode;
        else if (!empty())
            newNode.next = head;
        head = newNode;
        size++;
    }

    // PushBack: O(1) - Tiene acceso al último nodo
    public void pushBack(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
            return;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    // popFront: O(1)
    public T popFront() {
        T val = null;
        if (!empty()) {
            val = head.data;
            head = head.next;
        }
        size--;
        return val;
    }

    // popBack: O(n) - Debe buscar al penúltimo
    public T popBack() {
        T val = null;
        if (!empty()) {
            if (head == null) {
                val = head.data;
                head = null;
                tail = null;
            } else if (head.next == tail) {
                val = tail.data;
                tail = head;
            } else {
                Node<T> temp = head;
                while (temp.next != tail) {
                    temp = temp.next;
                }
                val = tail.data;
                tail = temp;
            }
        }
        size--;
        return val;
    }

    // find: O(n)
    public Node<T> find(T key) {
        Node<T> temp = head;
        while (temp != tail) {
            if (temp.data.equals(key))
                return temp;
            temp = temp.next;
        }
        return null;
    }

    // addBefore: O(n)
    public void addBefore(Node<T> node, T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = node;
        Node<T> temp = head;
        while (temp.next.next != node) {
            temp = temp.next;
        }
        temp.next.next = node;
        size++;
    }

    // addAfter: O(1)
    public void addAfter(Node<T> node, T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = node.next;
        node.next = newNode;
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

    // empty: O(1)
    public boolean empty() {
        return head == tail;
    }

    // topBack: O(1)
    public T topBack() {
        T val = null;
        if (!empty())
            val = tail.data;
        return val;
    }

    // topFront: O(1)
    public T topFront() {
        T val = null;
        if (!empty())
            val = head.data;
        return val;
    }

    public int size() {
        return size;
    }
}