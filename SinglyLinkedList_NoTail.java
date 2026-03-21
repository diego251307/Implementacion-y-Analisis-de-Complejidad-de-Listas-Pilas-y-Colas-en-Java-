// Clase que representa cada elemento de la lista
class Node {
    int data;
    Node next;

    public Node(int data) {
        this.data = data;
        this.next = null;
    }
}

// Clase principal de la estructura
public class SinglyLinkedList {
    private Node head; // Solo conocemos el inicio

    public SinglyLinkedList() {
        this.head = null;
    }

    // Insertar al inicio: O(1) - Muy rápido
    public void pushFront(int data) {
        Node newNode = new Node(data);
        newNode.next = head;
        head = newNode;
    }

    // Insertar al final: O(n) - Debe recorrer toda la lista
    public void pushBack(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            return;
        }
        Node temp = head;
        while (temp.next != null) { // Caminando hasta el último
            temp = temp.next;
        }
        temp.next = newNode;
    }

    // Eliminar el primero: O(1)
    public void popFront() {
        if (head != null) {
            head = head.next;
        }
    }

    // Eliminar el último: O(n) - Debe buscar al penúltimo
    public void popBack() {
        if (head == null) return;
        if (head.next == null) {
            head = null;
            return;
        }
        Node temp = head;
        while (temp.next.next != null) { // Se detiene en el penúltimo
            temp = temp.next;
        }
        temp.next = null;
    }

    // Buscar un elemento: O(n)
    public Node find(int key) {
        Node temp = head;
        while (temp != null) {
            if (temp.data == key) return temp;
            temp = temp.next;
        }
        return null;
    }

    // Verificar si está vacía
    public boolean empty() {
        return head == null;
    }
}