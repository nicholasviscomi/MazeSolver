package DataStructures;

public class Queue<T> {

    public Node<T> tail, head;
    public int size = 0;

    private final GUI.Frame frame;
    public Queue(GUI.Frame frame) {
        tail = null;
        head = null;
        this.frame = frame;
    }

    public void enqueue(T val) {
        Node<T> newNode = new Node<>(null, val, frame);

        if (head == null) {
            head = newNode;
            size++;
        }

        if (tail != null) {
            tail.next = newNode;
            size++;
        }
        tail = newNode;

    }

    public T dequeue() {
        if (head == null) {
            return null;
        }

        Node<T> tmp = head;
        T data = tmp.value;
        head = head.next;

        if (head == null) {
            tail = null;
        }
        size--;
        return data;
    }

    public void clear() {
        size = 0;
        head = null;
        tail = null;
    }

    public void print() {
        if (head == null) {
            return;
        }

        Node<T> curr = head;
        System.out.print(curr.value);

        while (curr.next != null) {
            curr = curr.next;
            System.out.print("<-" + curr.value);
        }

        System.out.println();
    }
}


