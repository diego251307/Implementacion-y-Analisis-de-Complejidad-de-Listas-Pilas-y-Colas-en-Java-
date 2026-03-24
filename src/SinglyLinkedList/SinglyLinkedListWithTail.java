package SinglyLinkedList;

public class SinglyLinkedListWithTail<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    private class Node<T> {
        T data;
        Node<T> next;

        private Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public SinglyLinkedListWithTail() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // PushFront: O(1)
    public void pushFront(T data) {
        Node<T> newNode = new Node<>(data);
        if (empty())
            tail = newNode;
        else
            newNode.next = head;
        head = newNode;
        size++;
    }

    // PushBack: O(1)
    public void pushBack(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    // popFront: O(1)
    public T popFront() {
        if (empty())
            return null;
        T val = head.data;
        head = head.next;
        if (head == null)
            tail = null;
        size--;
        return val;
    }

    // popBack: O(n)
    public T popBack() {
        if (empty())
            return null;
        T val = tail.data;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            Node<T> temp = head;
            while (temp.next != tail)
                temp = temp.next;
            temp.next = null;
            tail = temp;
        }
        size--;
        return val;
    }

    // find: O(n)
    public Node<T> find(T key) {
        Node<T> temp = head;
        while (temp != null) {
            if (temp.data.equals(key))
                return temp;
            temp = temp.next;
        }
        return null;
    }

    // addBefore: O(n) — recibe el valor de referencia, no el nodo
    public void addBefore(T key, T data) {
        if (head == null)
            return;
        if (head.data.equals(key)) {
            pushFront(data);
            return;
        }
        Node<T> temp = head;
        while (temp.next != null) {
            if (temp.next.data.equals(key)) {
                Node<T> newNode = new Node<>(data);
                newNode.next = temp.next;
                temp.next = newNode;
                size++;
                return;
            }
            temp = temp.next;
        }
    }

    // addAfter: O(n) por el find interno — recibe el valor de referencia, no el
    // nodo
    public void addAfter(T key, T data) {
        Node<T> target = find(key);
        if (target == null)
            return;
        Node<T> newNode = new Node<>(data);
        newNode.next = target.next;
        target.next = newNode;
        if (target == tail)
            tail = newNode;
        size++;
    }

    // erase: O(n)
    public void erase(T key) {
        if (head == null)
            return;
        if (head.data.equals(key)) {
            head = head.next;
            if (head == null)
                tail = null;
            size--;
            return;
        }
        Node<T> temp = head;
        while (temp.next != null) {
            if (temp.next.data.equals(key)) {
                if (temp.next == tail)
                    tail = temp;
                temp.next = temp.next.next;
                size--;
                return;
            }
            temp = temp.next;
        }
    }

    // topFront: O(1)
    public T topFront() {
        return empty() ? null : head.data;
    }

    // topBack: O(1)
    public T topBack() {
        return empty() ? null : tail.data;
    }

    public boolean empty() {
        return head == null;
    }

    public int size() {
        return size;
    }
}