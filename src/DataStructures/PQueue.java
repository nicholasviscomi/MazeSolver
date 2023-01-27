package DataStructures;

import java.awt.*;
import java.util.ArrayList;

public class PQueue {
    /*
    doubly linked queue
    enqueue goes to back 
    while curr.next != null && curr > curr.next  {
        swap()

    }
    */

    public PNode head, tail;
    public int size = 0;
    public ArrayList<PNode> data;

    public PQueue() {
        data = new ArrayList<>();
    }

    public void enqueue(Point p, double dist) {
        int i = 0;
        while (i < data.size()) {
            if (dist > data.get(i).dist) {
                i++;
            } else {
                data.add(i, new PNode(dist, null, p));
                break;
            }
        }
        if (i == data.size()) {
            data.add(i, new PNode(dist, null, p));
        }
        size++;
    }

    public Point dequeue() {
        if (size > 0) {
            PNode tmp = data.get(0);
            data.remove(0);
            size--;
            return tmp.p;
        } else {
            return null;
        }
    }

    public void clear() {
        data.clear();
        size = 0;
    }

    public void print() {
        for (PNode p : data) {
            System.out.print(p.dist + "-->");
        }
    }

}

