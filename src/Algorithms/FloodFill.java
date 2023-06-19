package Algorithms;

import DataStructures.Node;
import DataStructures.PQueue;
import DataStructures.Queue;
import Helper.Helper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FloodFill implements Algorithm {

    ArrayList<ArrayList<Integer>> distances;
    ArrayList<Point> excluded;
    private final ArrayList<ArrayList<Node<Point>>> grid;
    private final Point sPoint;
    private final Point ePoint;

    private PQueue pq;
    private final ArrayList<ArrayList<Boolean>> visited = new ArrayList<>();
    private final GUI.Frame frame;

    public FloodFill(ArrayList<ArrayList<Node<Point>>> grid, Point sPoint, Point ePoint, GUI.Frame frame) {
        this.grid = grid;
        this.sPoint = sPoint;
        this.ePoint = ePoint;
        this.frame = frame;
        dq = new Queue<>(this.frame);
        pq = new PQueue();

        excluded = new ArrayList<>();
        distances = new ArrayList<>();
        initDistances();

        setVisited();
    }

    /*
    - look around at neighbors
    - update distances
    - go to the valid point with the smallest distance
    - add it to current path
    - enqueue next batch of valid points
    - update priority queue in order of the updated distances
    - dequeue next point

    to clean up the final path:
    - iterate through and see if any points are not next to each other
    - continue to remove points until every point is next to each other
    OR?
    - look at the distances and go through the shortest path????
        - this is probably the correct answer but seems computationally inefficient?
     */

    ArrayList<Point> nextPoints = new ArrayList<>();

    ArrayList<Point> path = new ArrayList<>();
    @Override
    public boolean solve() {
        boolean foundPath = false;
        setVisited();
        updateDistances();

        /*
        - maintain an array with the all the distances in order
        - should just look like [10,9,8,7,6,...]
        - then if there is a new wall that means we need to go backwards, update the distances
          within this array
         - then iterate from the end until you reach the smallest number and then start searching from there
         */

        nextPoints.add(sPoint);
        while (nextPoints.size() > 0) {
            Point curr = nextPoints.get(0);
            frame.openNodes.add(curr);
            path.add(curr);
            visited.get(curr.y).set(curr.x, true);

            nextPoints.remove(0);

            if (Helper.pEqualsP(curr, ePoint)) { //next point is the end point
                System.out.println("Hit end point");
                System.out.println("Open Nodes: " + frame.openNodes.size());
                foundPath = true;
                break;
            }

            ArrayList<Point> validSquares = exploreNeighbors(curr);
            nextPoints.addAll(validSquares);

            // sort the nextPoints array according to the newly updated distances array
            // e.x. if a newly discovered wall changes the path, it will go back to the
            //      shortest point
            nextPoints.sort((p1, p2) -> getDistance(p1) - getDistance(p2));

            //Now need to sub-sort this array by how close each point is to the current point

        }

        return foundPath;
    }

    private ArrayList<Point> exploreNeighbors(Point curr) {
        ArrayList<Point> validSquares = new ArrayList<>();

        // Add all the new neighboring walls to the excluded list
        int[] dy = {-1, 0, 1, 0 }; //{-1, 1, 0, 0, -1, 1, -1, 1};
        int[] dx = {0, 1, 0, -1}; //{0, 0, 1, -1, 1, 1, -1, -1};
        for (int i = 0; i < 4; i++) {
            int newX = curr.x + dx[i];
            int newY = curr.y + dy[i];

            if ( //if new val is out of frame
                newY == frame.getHeight()/frame.gridSide ||
                newY == -1 ||
                newX == frame.getWidth()/frame.gridSide ||
                newX == -1
            ) { continue; }

            Point np = new Point(newX, newY);
            boolean isWall = frame.walls.contains(np);
            boolean isVisited = visited.get(np.y).get(np.x);
            if (isWall) {
                excluded.add(np);
            } else if (!isVisited) { // crucial line to stop perpetual iterations over the same squares
                validSquares.add(np);
            }
        }
        //------------------

        // Update the distances with the newly updated list of excluded squares
        updateDistances();
        //------------------

        return validSquares;
    }

    private final Queue<Point> dq; //queue for updating the distances
    /*
    Essentially breadth first search expansion from the end point
     */
    private void updateDistances() {
        ArrayList<ArrayList<Boolean>> local_visited = new ArrayList<>();

        for (int i = 0; i < frame.getHeight()/frame.gridSide; i++) {
            ArrayList<Boolean> row = new ArrayList<Boolean>();
            for (int j = 0; j < frame.getWidth()/frame.gridSide; j++) {
                row.add(false);
            }
            local_visited.add(row);
        }

        dq.enqueue(ePoint);

        distances.get(ePoint.y).set(ePoint.x, 0);
        local_visited.get(ePoint.y).set(ePoint.x, true); // critical line: ensures the end point distance stays 0

        while (dq.size > 0) {
            Point curr = dq.dequeue();
            int[] dy = {-1, 0, 1, 0}; //{-1, 1, 0, 0, -1, 1, -1, 1};
            int[] dx = {0, 1, 0, -1}; //{0, 0, 1, -1, 1, 1, -1, -1};
            for (int i = 0; i < 4; i++) {
                int newX = curr.x + dx[i];
                int newY = curr.y + dy[i];

                if ( //if new val is out of frame
                    newY == frame.getHeight()/frame.gridSide ||
                    newY == -1 ||
                    newX == frame.getWidth()/frame.gridSide ||
                    newX == -1
                ) { continue; }

                boolean isVisited = local_visited.get(newY).get(newX);
                // using excluded here because flood fill initially assumes there are no walls and then
                boolean isWall = excluded.contains(new Point(newX, newY));

                if (isVisited || isWall) { continue; }

                int prevDistance = distances.get(curr.y).get(curr.x);
                // here is where the distance values are getting updated
                distances.get(newY).set(newX, prevDistance + 1);
                local_visited.get(newY).set(newX, true);

                dq.enqueue(new Point(newX, newY));
            }
        }
    }

    private int getDistance(Point p){
        return distances.get(p.y).get(p.x);
    }

    private void initDistances() {
        for (int i = 0; i < frame.getHeight()/frame.gridSide; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < frame.getWidth()/frame.gridSide; j++) {
                row.add(1000000);
            }
            distances.add(row);
        }
    }

    @Override
    public ArrayList<Point> getPath(Node<Point> ePoint) {
        ArrayList<Point> res = new ArrayList<>();
        System.out.println(path);

//        dq.clear();
//        for (ArrayList<Integer> row : distances) {
//            System.out.println(row);
//        }
//        //Go through the distances matrix from the end point and choose the quickest path
//        ArrayList<ArrayList<Boolean>> local_visited = new ArrayList<>();
//
//        // set local visited matrix
//        for (int i = 0; i < frame.getHeight()/frame.gridSide; i++) {
//            ArrayList<Boolean> row = new ArrayList<>();
//            for (int j = 0; j < frame.getWidth()/frame.gridSide; j++) {
//                row.add(false);
//            }
//            local_visited.add(row);
//        }
//        //-------------------------
//
//        dq.enqueue(sPoint);
//
//        distances.get(sPoint.y).set(sPoint.x, 0);
//        local_visited.get(sPoint.y).set(sPoint.x, true); // critical line: ensures the end point is not revisited
//
//        while (dq.size > 0) {
//            Point curr = dq.dequeue();
//            res.add(curr);
//
//            ArrayList<Point> validPoints = new ArrayList<>();
//
//            // look up, down, left, right to find next point which is the shortest distance away
//            int[] dy = {-1, 0, 1, 0}; //{-1, 1, 0, 0, -1, 1, -1, 1};
//            int[] dx = {0, 1, 0, -1}; //{0, 0, 1, -1, 1, 1, -1, -1};
//            for (int i = 0; i < 4; i++) {
//                int newX = curr.x + dx[i];
//                int newY = curr.y + dy[i];
//
//                if ( //if new val is out of frame
//                        newY == frame.getHeight()/frame.gridSide ||
//                                newY == -1 ||
//                                newX == frame.getWidth()/frame.gridSide ||
//                                newX == -1
//                ) { continue; }
//
//                Point newP = new Point(newX, newY);
//                boolean isVisited = local_visited.get(newY).get(newX);
//                // using excluded here because flood fill only knows about the walls it encountered
//                boolean isWall = excluded.contains(newP);
//
//                if (isVisited || isWall) { continue; }
//
//                local_visited.get(newY).set(newX, true);
//                validPoints.add(newP);
//
//                if (Helper.pEqualsP(newP, ePoint.value)) {
//                    res.add(newP);
//                    return res;
//                }
//            }
//            validPoints.sort((p1, p2) -> getDistance(p1) - getDistance(p2));
//            if (validPoints.size() > 0) {
//                System.out.println("validPoints.get(0) = " + validPoints.get(0));
//                dq.enqueue(validPoints.get(0));
//            }
//        }
        return path;
    }

    void setVisited() {
        for (int i = 0; i < frame.getHeight()/frame.gridSide; i++) {
            ArrayList<Boolean> row = new ArrayList<Boolean>();
            for (int j = 0; j < frame.getWidth()/frame.gridSide; j++) {
                row.add(false);
            }
            visited.add(row);
        }
    }

}
