package Algorithms;

import DataStructures.Node;
import DataStructures.PQueue;
import Helper.Helper;

import java.awt.*;
import java.util.ArrayList;


public class BestFirstSearch implements Algorithm {
    /*
    priority queue with the next node that has the shortest distance to the end node at the top
    then expand the dequeued node and look around it adding them to the Priority queue.
    In the pqueue, when something in enqueued it goes through every element and sort the
    queue to be in increasing size of distance to end node
    */

    private final GUI.Frame frame;
    private final ArrayList<ArrayList<Node<Point>>> grid;
    public Point sPoint;
    public Point ePoint;

    PQueue pq = new PQueue();
    private final ArrayList<ArrayList<Boolean>> visited = new ArrayList<>();

    int[] dy = { -1, 0, 1, 0 };
    int[] dx = { 0, 1, 0, -1 };
    // int[] dy = {-1, 1, 0, 0, -1, 1, -1, 1};
    // int[] dx = {0, 0, 1, -1, 1, 1, -1, -1};

    // int[] vdy = {1, 1, -1, -1};
    // int[] vdx = {1, -1, 1, -1};

    public BestFirstSearch(ArrayList<ArrayList<Node<Point>>> grid, Point sPoint, Point ePoint, GUI.Frame frame) {
        this.grid = grid;
        this.sPoint = sPoint;
        this.ePoint = ePoint;
        this.frame = frame;
        setVisited();
    }

    boolean found = false;

    public boolean solve() {
        setVisited();
        pq.clear();
        frame.openNodes.clear();
        frame.path.clear();

        pq.enqueue(frame.getSPoint(), distFromEnd(frame.getSPoint()));

        while (pq.size > 0 && !found) {
            Point next = pq.dequeue();
            frame.openNodes.add(next);
            
            Node<Point> newNode = frame.nodeAtPoint(next);

            if (Helper.pEqualsP(newNode.value, ePoint)) {
                System.out.println("Open Nodes: " + frame.openNodes.size());
                System.out.println("Best First Path: " + getPath(frame.nodeAtPoint(ePoint)).size());
                found = true;
                break;
            }

            exploreNeighbors(newNode);

        }

        return found;
    }

    /*
    get distance form end for each point
    when it is added to the q it will move to the front
    then when it is dequeued it will continuously be the 1 of 4 neighbors closest to the end
    */

    public void exploreNeighbors(Node<Point> n) {
//        System.out.print("exploring: "); Helper.printPoint(n.value);

        for (int i = 0; i < 4; i++) {
            int newX = n.value.x + dx[i];
            int newY = n.value.y + dy[i];
            Point newPoint = new Point(newX, newY);
            Node<Point> newNode = new Node<>(n, newPoint, frame);

            if (
                    !(newNode.value.y == frame.getHeight()/20 ||
                newNode.value.y == -1 ||
                newNode.value.x == frame.getWidth()/20 ||
                newNode.value.x == -1)
            ) {
                boolean isVisited = visited.get(newNode.value.y).get(newNode.value.x);
                boolean isWall = frame.walls.contains(newPoint);

                if(!isVisited && !isWall) {
//                    Helper.printGrid(grid);
                    pq.enqueue(newPoint, distFromEnd(newPoint));
                    grid.get(newNode.value.y).set(newNode.value.x, newNode);
                    visited.get(newNode.value.y).set(newNode.value.x, true);
//                    System.out.println("Point: ("+newPoint.x+", "+newPoint.y+") = "+distFromEnd(newPoint) +"from the end");
                }
            }
        }

    }

    public ArrayList<Node<Point>> getPath(Node<Point> end) {
        ArrayList<Node<Point>> path = new ArrayList<>();
        Node<Point> curr = end;

        System.out.println("get path: " + sPoint);
        while (curr.next != null) {// maybe add an exception where it breaks the loop if it equals the spoint
            path.add(curr);
            curr = curr.next;
        }   

        path.add(curr);

        return path;
    }

    public double distFromEnd(Point p) {
        double dy = Math.abs(p.y - frame.getEPoint().y);
        double dx = Math.abs(p.x - frame.getEPoint().x);
        // dx = Math.pow(dx, 2);
        // dy = Math.pow(dy, 2);

        // return Math.sqrt(sum);
        return dx + dy;
    }

    void setVisited() {
        for (int i = 0; i < frame.getWidth()/20; i++) {
            ArrayList<Boolean> row = new ArrayList<Boolean>();
            for (int j = 0; j < frame.getWidth()/20; j++) {
                row.add(false);
            }
            visited.add(row);
        }
    }
}
