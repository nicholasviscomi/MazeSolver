package MazeGen;

import DataStructures.Node;
import DataStructures.Queue;
import Helper.Helper;

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
        System.out.println("\n\nCreate maze!");
        // width needs to be the number of boxes, NOT the number of pixels
        Section seed = new Section(new Dimension(frame.getWidth()/20, frame.getHeight()/20), new Point(0, 0), false);
        queue.enqueue(seed);

        while (queue.size > 0) {
            Section s = queue.dequeue();
            Section[] subs = divide(s.dimension, s.startPoint);
            for (Section section: subs) {
//                System.out.println("Entering subsection | width = " + section.dimension + ", start = " +  section.startPoint);
                if (isLargeEnough(section)) {
                    queue.enqueue(section);
                }
            }
        }

        return walls;
    }

    // first get it working with just drawing vertical lines
    Section[] divide(Dimension d, Point sectionStart) {
        Section[] res = new Section[2];
        int seed = Helper.randomNumber(sectionStart.x, sectionStart.x + d.width);
        System.out.println("Wall at x = " + seed);
        int hole = Helper.randomNumber(sectionStart.y, d.height);
        System.out.println("Hole at (" + seed + ", " + hole + ")");

        for (int i = sectionStart.y; i < sectionStart.y + d.height; i++) {
            if (i != hole) {
                walls.add(new Point(seed, i));
            }
        }

        // need to do (seed - sectionStart.x) so that we don't accidentally get a negative width when we shouldn't
        // it is also just the correct calculation b/c we want to get the width to the right of the wall WITHIN THE CURRENT SECTION
        res[0] = new Section(new Dimension(d.width - (seed - sectionStart.x) - 2, d.height), new Point(seed + 2, sectionStart.y), false); // right subsection
        System.out.println("Right subsection width = " + res[0].dimension + ", start = " +  res[0].startPoint);


        res[1] = new Section(new Dimension((seed - sectionStart.x) - 2, d.height), new Point(sectionStart.x, sectionStart.y), false); // left subsection
        System.out.println("Left subsection width = " + res[1].dimension + ", start = " +  res[1].startPoint);

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

    private void setAllUnVisited() {
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
