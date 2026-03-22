package SinglyLinkedList;

// Clase que representa cada elemento de la lista

class Node<T> {
    T data;
    Node<T> next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}

// Clase principal de la estructura

public class SinglyLinkedListNoTail<T> {
    private Node<T> head; // Solo conocemos el inicio
    private int size;

    public SinglyLinkedListNoTail() {
        this.head = null;
        this.size = 0;
    }

    // Insertar al inicio: O(1) - Muy rápido
    public void pushFront(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = head;
        head = newNode;
        size++;
    }

    // Insertar al final: O(n) - Debe recorrer toda la lista
    public void pushBack(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            return;
        }
        Node<T> temp = head;
        while (temp.next != null) { // Caminando hasta el último
            temp = temp.next;
        }
        temp.next = newNode;
        size++;
    }

    // Eliminar el primero: O(1)
    public void popFront() {
        if (head != null) {
            head = head.next;
        }
        size--;
    }

    // Eliminar el último: O(n) - Debe buscar al penúltimo
    public void popBack() {
        if (head == null)
            return;
        if (head.next == null) {
            head = null;
            return;
        }
        Node<T> temp = head;
        while (temp.next.next != null) { // Se detiene en el penúltimo
            temp = temp.next;
        }
        temp.next = null;
        size--;
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

    // Verificar si está vacía
    public boolean empty() {
        return head == null;
    }

    public int size() {
        return size;
    }
}