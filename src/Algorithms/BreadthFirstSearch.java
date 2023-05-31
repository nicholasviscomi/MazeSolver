package Algorithms;

import DataStructures.*;
import GUI.Frame;
import Helper.Helper;

import java.util.ArrayList;
import java.awt.*;

public class BreadthFirstSearch implements Algorithm {

    private final ArrayList<ArrayList<Node<Point>>> grid;
    private final Point sPoint;
    private final Point ePoint;

    private final Queue<Point> q;
    private final ArrayList<ArrayList<Boolean>> visited = new ArrayList<>();
    private final GUI.Frame frame;

    public BreadthFirstSearch(ArrayList<ArrayList<Node<Point>>> grid, Point sPoint, Point ePoint, GUI.Frame frame) {
        this.grid = grid;
        this.sPoint = sPoint;
        this.ePoint = ePoint;
        this.frame = frame;
        q = new Queue<>(this.frame);
        setVisited();
    }

    public boolean solve() {
        boolean foundPath = false;
        setVisited();
        q.clear();
        q.enqueue(sPoint);
        System.out.println("Breadth sPoint = " + sPoint);

        while (q.size > 0) {
            Point next = q.dequeue();
            Node<Point> nNode = frame.nodeAtPoint(next);

            if (Helper.pEqualsP(next, ePoint)) { //next point is the end point
                System.out.println("Open Nodes: " + frame.openNodes.size());
                System.out.println("Breadth First Path: " + getPath(frame.nodeAtPoint(ePoint)).size());
                foundPath = true;
                break;
            }

            exploreNeighbors(nNode, q);
        }

        return foundPath;

    }

    public void exploreNeighbors(Node<Point> n, Queue<Point> queue) {
        // System.out.println("x: " + n.value.x + "y: " + n.value.y);

        int[] dy = {-1, 0, 1, 0 }; //{-1, 1, 0, 0, -1, 1, -1, 1};
        int[] dx = {0, 1, 0, -1}; //{0, 0, 1, -1, 1, 1, -1, -1};
        for (int i = 0; i < 4; i++) {
            int newX = n.value.x + dx[i];
            int newY = n.value.y + dy[i];
            Point newPoint = new Point(newX, newY);
            Node<Point> newNode = new Node<>(n, newPoint, frame); //makes new node that points back to previous one
            if ( //if new val is NOT out of frame
                    !(
                            newNode.value.y == frame.getHeight()/frame.gridSide ||
                            newNode.value.y == -1 ||
                            newNode.value.x == frame.getWidth()/frame.gridSide ||
                            newNode.value.x == -1
                    )
            ) {
                boolean isVisited = visited.get(newNode.value.y).get(newNode.value.x);
                boolean isWall = frame.walls.contains(newPoint);

                if(!isVisited && !isWall) {
                    grid.get(newNode.value.y).set(newNode.value.x, newNode);
                    visited.get(newNode.value.y).set(newNode.value.x, true);
                    queue.enqueue(newPoint);
                    frame.openNodes.add(newPoint);
                }
            }

        }

    }

    public ArrayList<Node<Point>> getPath(Node<Point> end) {
        ArrayList<Node<Point>> path = new ArrayList<Node<Point>>();
        Node<Point> curr = end;

        while (curr.next != null) {
            path.add(curr);
            curr = curr.next;
        }
        path.add(curr);

        return path;
    }
    void setVisited() {
        for (int i = 0; i < frame.getHeight()/ frame.gridSide; i++) {
            ArrayList<Boolean> row = new ArrayList<Boolean>();
            for (int j = 0; j < frame.getWidth()/ frame.gridSide; j++) {
                row.add(false);
            }
            visited.add(row);
        }
    }

    /*
    Next steps:
        -->when the queue dequeues the endPoint trace through node.next  back to start
           and append each point to an array called path
        -->once the path array is created have call repaint and have paintcomponent draw the path to the end
    */

}

// int count = 2;
//     public boolean bidirectionalSolve() {
//         boolean found = false;
//         Queue<Point> sq = new Queue<Point>();
//         Queue<Point> eq = new Queue<Point>();
//         setVisited();

//         sq.enqueue(sPoint);
//         eq.enqueue(ePoint);

//         Point eCurr = new Point();
//         Point sCurr = new Point();

//         outer: while (sq.size > 0 || eq.size > 0) {
//             Point next;
//             Node<Point> nNode;

//             System.out.println("(" + sCurr.x + "," + sCurr.y + ")---(" + eCurr.x + "," + eCurr.y + ")");

//             if (count % 2 == 0) { //count is even so open up a start queue node
//                 next = sq.dequeue();
//                 sCurr = next;
//                 nNode = Frame.nodeAtPoint(sCurr);

//                 int[] dy = {-1, 0, 1, 0 }; //{-1, 1, 0, 0, -1, 1, -1, 1};
//                 int[] dx = {0, 1, 0, -1}; //{0, 0, 1, -1, 1, 1, -1, -1};
//                 for (int i = 0; i < 4; i++) {
//                     //if the point just dequeued is one of the neighbors of the current end node we found it
//                     int newX = eCurr.x + dx[i];
//                     int newY = eCurr.y + dy[i];
//                     Point nPoint = new Point(newX, newY);

//                     if (Helper.pEqualsP(sCurr, nPoint)) {
//                         found = true;
//                         System.out.println("FOUND IT LETS GOOOOO (start node BIDIRECTIONAL)");
//                         break outer;
//                     }
//                 }

//                 exploreNeighbors(nNode, sq);
//             } else { //count is odd so open up a end queue node
//                 next = eq.dequeue();
//                 eCurr = next;
//                 nNode = Frame.nodeAtPoint(eCurr);

//                 int[] dy = {-1, 0, 1, 0 }; //{-1, 1, 0, 0, -1, 1, -1, 1};
//                 int[] dx = {0, 1, 0, -1}; //{0, 0, 1, -1, 1, 1, -1, -1};
//                 for (int i = 0; i < 4; i++) {
//                     //if the point just dequeued is one of the neighbors of the current start node we found it
//                     int newX = sCurr.x + dx[i];
//                     int newY = sCurr.y + dy[i];
//                     Point nPoint = new Point(newX, newY);

//                     if (Helper.pEqualsP(eCurr, nPoint)) {
//                         System.out.println("FOUND IT LETS GOOOOO (end node BIDIRECTIONAL)");

//                         Node<Point> curr = nNode;
//                         while (curr.next != null) {
//                             System.out.println(curr.value);
//                             curr = curr.next;
//                         }
//                         System.out.println(curr.value);
//                         System.out.println();

//                         Node<Point> c = Frame.nodeAtPoint(sCurr);
//                         while (c.next != null) {
//                             System.out.println(c.value);
//                             c = c.next;
//                         }
//                         System.out.println(c.value);
//                         System.out.println();
//                         break outer;
//                     }
//                 }

//                 exploreNeighbors(nNode, eq);
//             }


//             count++;
//         }

//         /*
//         BIDIRECTIONAL SOLVE: keep a count variable and if its even expand a start node point if odd expand an end node point
//         in one while loop check to see if snext equals enext (if this doesnt work set enext.next as snext)
//         */

//         return found;
//     }