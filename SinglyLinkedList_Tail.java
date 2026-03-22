// Clase que representa cada elemento de la lista
class Node {

    int data;
    Node next;
    Node prev;

    public Node(int data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}

// Clase principal de la estructura
public class SinglyLinkedList {

    private Node head; // Conocemos el primer nodo
    private Node tail; // Conocemos el último nodo

    public SinglyLinkedList() {
        this.head = null;
        thsi.tail = null;
    }

    // PushFront: O(1) - Muy rápido
    public void pushFront(int data) {
        Node newNode = new Node(data);
        if (empty()) tail = newNode;
        else if (!empty()) newNode.next = head;
        head = newNode;
    }

    // PushBack: O(1) - Tiene acceso al último nodo
    public void pushBack(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
            return;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    // popFront: O(1)
    public int popFront() {
        int val = -1;
        if (!empty()) {
            val = head.data;
            head = head.next;
        }
        return val;
    }

    // popBack: O(n) - Debe buscar al penúltimo
    public int popBack() {
        int val = -1;
        if (!empty()) {
            if (head == null) {
                val = head.data;
                head = null;
                tail = null;
            } else if (head.next == tail) {
                val = tail;
                tail = head;
            } else {
                Node temp = head;
                while (temp.next != tail) {
                    temp = temp.next;
                }
                val = tail.data;
                tail = temp;
            }
        }
        return val;
    }

    // find: O(n)
    public Node find(int key) {
        Node temp = head;
        while (temp != tail) {
            if (temp.data == key) return temp;
            temp = temp.next;
        }
        return null;
    }

    // addBefore: O(n)
    public void addBefore(Node node, int data){
        Node newNode = new Node(data);
        newNode.next = node;
        Node temp = head;
        while (temp.next.next != node){
            temp = temp.next;
        }
        temp.next.next = node;
    }

    // addAfter: O(1)
    public void addAfter(Node node, int data){
        Node newNode = new Node(data);
        newNode.next = node.next;
        node.next = newNode;
    }

    // empty: O(1)
    public boolean empty() {
        return head == tail;
    }

    // topBack: O(1)
    public int topBack(){
        int val = -1;
        if (!empty()) val = tail.data;
        return val
    }

    // topFront: O(1)
    public int topFront(){
        int val = -1;
        if (!empty()) val = head.data;
        return val
    }
}
