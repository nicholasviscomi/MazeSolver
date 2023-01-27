package MazeGen;

import DataStructures.Node;
import DataStructures.Queue;
import Helper.Helper;
import GUI.Frame;

import java.util.ArrayList;
import java.awt.*;

public class MazeGenerator {
    //recursively create maze

    private final GUI.Frame frame;
    Point sPoint, ePoint;
    private final ArrayList<ArrayList<Boolean>> visited = new ArrayList<>();
    ArrayList<ArrayList<Node<Point>>> grid;
    ArrayList<Point> walls = new ArrayList<>();
    Queue<Section> queue;
//     Random rand = new Random();

    public MazeGenerator(Point sPoint, Point ePoint, ArrayList<ArrayList<Node<Point>>> grid, GUI.Frame frame) {
        this.sPoint = sPoint;
        this.ePoint = ePoint;
        this.grid = grid;
        this.frame = frame;

        queue = new Queue<>(frame);
    }
    /*
    Architecture:

    pick a random seed to draw the first vertical wall
    then after each division add a Section to a queue of sections that need to be divided

    ie: vertical wall from (10,0) to (10,19) --> subsections on either side of that wall get added to queue
    then a section is dequeued, split and its subsections are added to the queue.
    (continues until the subsections are too small to be divided anymore)
     */

    public ArrayList<Point> recursive_division() {
        Section seed = new Section(new Dimension(frame.getWidth(), frame.getHeight()), new Point(0, 0), false);
        queue.enqueue(seed);

        while (queue.size > 0 && queue.size < 4) {
            Section s = queue.dequeue();
            Section[] subs = divide(s.horiz, s.dimension, s.startPoint);
            if (isLargeEnough(subs[0]) && isLargeEnough(subs[1])) {
                queue.enqueue(subs[0]);
                queue.enqueue(subs[1]);
            }
        }

        System.out.println("Finished maze creation");
        return walls;
    }

    Section[] divide(boolean horiz, Dimension d, Point sectionStart) {
        Section[] res = new Section[2];
        if (horiz) { //drawing horizontal line
            int seed = Helper.randomNumber(sectionStart.y, sectionStart.y+(d.height/20)); //which y value the line will have
            int hole = Helper.randomNumber(sectionStart.x, sectionStart.x+(d.width/20));
            System.out.println("Seed: " + seed);
            for (int i = sectionStart.x; i < sectionStart.x + d.width; i++) {
                if (i != hole) {
                    walls.add(new Point(i, seed));
                }
            }
//            res[0] = new Section(new Dimension(d.width - seed.x, ), seed, false); // top subsection
//            res[1] = new Section(new Dimension(), new Point(s.startPoint.x + ), false); //bottom subsection
        } else { //drawing vertical line
            int seed = Helper.randomNumber(sectionStart.x, sectionStart.x+(d.width/20)); //which x value the line will have
            int hole = Helper.randomNumber(sectionStart.y, sectionStart.y+(d.height/20));
            System.out.println("Seed: " + seed + "\nmax: " + sectionStart.x+(d.width/20) + "\nhole: " + hole);
            for (int i = sectionStart.y; i < sectionStart.y + d.height; i++) {
                if (i != hole) {
                    walls.add(new Point(seed, i));
                }
            }
            res[0] = new Section(new Dimension((hole) - 2, d.height), sectionStart, false); // left subsection
            res[1] = new Section(new Dimension(d.width - (hole*20), d.height), new Point(sectionStart.x + ((hole) + 2), sectionStart.y), false); //right subsection
        }

        return res;
    }

    boolean isLargeEnough(Section s) {
        return (s.dimension.width > 1 && s.dimension.height > 1);
    }

    ArrayList<Point> setAllWalls() {
        ArrayList<Point> walls = new ArrayList<>();
        for (int y = 0; y < frame.getHeight()/20; y++) {
            for (int x = 0; x < frame.getWidth()/20; x++) {
                if (!Helper.pEqualsP(new Point(x, y), sPoint) && !Helper.pEqualsP(new Point(x, y), ePoint)) { 
                    walls.add(new Point(x, y));
                }
            }
        }

        return walls;
    }

    private void setVisited() {
        for (int i = 0; i < frame.getHeight()/20; i++) {
            ArrayList<Boolean> row = new ArrayList<>();
            for (int j = 0; j < frame.getWidth()/20; j++) {
                row.add(false);
            }
            visited.add(row);
        }
    }

    private boolean isWall(Point p) {
        return walls.contains(p);
    }

}
