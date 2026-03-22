class Node {
    int data;
    Node next;

    public Node(int data) {
        this.data = data;
        this.next = null;
    }
}

// Clase principal
public class SinglyLinkedList {
    private Node head; // conocemos el inicio

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
    public void addAfter(Node node, int data) {
        if (node == null){
            return;
        }
        Node newNode = new Node(data);
        newNode.next = node.next; // el nuevo apunta al siguiente del original
        node.next = newNode;      // el original ahora apunta al nuevo
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
    public void addBefore(Node key, int data) {
        if (head == null || key == null){
            return;
        }
        
        if (head == key) {
            pushFront(data);
            return;
        }
    
        Node temp = head;
        // buscamos al nodo que apunta al key
        while (temp != null && temp.next != key) {
            temp = temp.next;
        }
    
        // si encontramos al anterior
        if (temp != null) {
            Node newNode = new Node(data);
            newNode.next = targetNode;
            temp.next = newNode;
        }
    }
}
