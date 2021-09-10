package DataStructures;

import GUI.*;

import java.awt.Point;

public class Node<T> {
    public Node<T> next;
    public T value;
    public boolean isStart = false;
    public boolean isEnd = false;

    public Node(Node<T> next, T value, GUI.Frame frame) {
        this.value = value;
        this.next = next;

        if (value instanceof Point) {
            Point p = (Point) value;
            // System.out.println("Node: s " + Frame.getSPoint());
            if (p.x == frame.getSPoint().x && p.y == frame.getSPoint().y) {
                isStart = true;
            }
        }
        
        if (value instanceof Point) {
            Point p = (Point) value;
            // System.out.println("Node: e " + Frame.getEPoint());
            if (p.x == frame.getEPoint().x && p.y == frame.getEPoint().y) {
                isEnd = true;
            }
        }
    }

}
