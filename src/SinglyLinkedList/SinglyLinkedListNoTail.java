class Node {
    int data;
    Node next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}

// Clase principal
public class SinglyLinkedList {
    private Node head; // conocemos el inicio

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

    // Eliminar el primero O(1)
    public int popFront() {
        if (head == null) {
            return -1;
        }
        int ret = head.data;
        head = head.next;
        return ret;
    }

    // eliminar el último O(n)
    public int popBack() {
        if (head == null) {
            return -1;
        }
        if (head.next == null) {
            int ret = head.ret;
            head = null;
            return ret;
        }
        Node temp = head;
        while (temp.next.next != null) { // se detiene en el penúltimo
            temp = temp.next;
        }
        int ret = temp.next.ret;
        temp.next = null;
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

    // Verificar si está vacía
    public boolean empty() {
        return head == null;
    }

    public int addAfter(Node key, int data) {
        if (key == null) {
            return -1;
        }
        Node newNode = new Node(data);
        newNode.next = key.next; // el nuevo apunta al siguiente del original
        key.next = newNode; // el original ahora apunta al nuevo
        return 1;
    }

    public void erase(int key){
        if (head == null) {
            return;
        }
        else if (head.data == key){
            head = head.next;
            return;
        }

        Node temp = head;
        while (temp.next != null && temp.next.data != key) {
        temp = temp.next;

        if (temp.next != null) {
        temp.next = temp.next.next; 
        }
    }

    public int addBefore(Node key, int data) {
        if (head == null || key == null) {
            return -1;
        }
        if (head == key) {
            pushFront(data);
        }
        Node temp = head;
        // buscamos al nodo que apunta al key
        while (temp.next != key) {
            temp = temp.next;
            if (temp == null) {
                return -1;
            }
        }
        // si encontramos al anterior
        if (temp != null) {
            Node newNode = new Node(data);
            newNode.next = temp.next;
            temp.next = newNode;
        }
        return 1;
    }
}
