package MazeGen;

import DataStructures.Node;
import DataStructures.Queue;
import Helper.Helper;

import java.util.ArrayList;
import java.awt.*;
import java.util.Random;
import java.util.Scanner;

public class MazeGenerator {
    //recursively create maze

    private final GUI.Frame frame;
    Point sPoint, ePoint;
    private final ArrayList<ArrayList<Boolean>> visited = new ArrayList<>();
    ArrayList<ArrayList<Node<Point>>> grid;
    ArrayList<Point> walls = new ArrayList<>();
    Queue<Section> queue;
     Random rand = new Random();

    public MazeGenerator(Point sPoint, Point ePoint, ArrayList<ArrayList<Node<Point>>> grid, GUI.Frame frame) {
        this.sPoint = sPoint;
        this.ePoint = ePoint;
        this.grid = grid;
        this.frame = frame;

        queue = new Queue<>(frame);
    }
    /*
    Architecture:

    pick a random seed to draw the first  wall
    then after each division add a Section to a queue of sections that need to be divided

    ie: vertical wall from (10,0) to (10,19) --> subsections on either side of that wall get added to queue
    then a section is dequeued, split and its subsections are added to the queue.
    (continues until the subsections are too small to be divided anymore)

    The sub-sections will be every valid square for a new wall and hole, so that on the next itertation only a random seed
    and hole need to be found with no fancy math to make sure it's valid. The key to getting this wokring is by creating the
    correct sub sections!
     */

    public ArrayList<Point> recursive_division(int depth) {
        System.out.println("\n\nCreate maze!");
        // width needs to be the number of boxes, NOT the number of pixels
        Section seed = new Section(new Dimension(frame.getWidth()/20 - 1, frame.getHeight()/20 - 1), new Point(0, 0), false, -1);
        queue.enqueue(seed);

        int i = 0;
        while (queue.size > 0) {
            Section s = queue.dequeue();
            Section[] subs = divide(s);
            for (Section section : subs) {
                if (isLargeEnough(section)) {
                    System.out.println("Entering subsection | width = " + section.dimension + ", start = " + section.startPoint + ", shouldbehoriz = " + section.should_be_horizontal);
                    queue.enqueue(section);
//                    divide(section);
                } else {
                    System.out.println("section too small");
                }
            }
        }

        return walls;
    }

    Section[] divide(Section s) {
        Point sectionStart = s.startPoint;
        Dimension d = s.dimension;
        boolean shouldBeHoriz = s.should_be_horizontal;
        int prev_hole = s.prev_hole;

        Section[] res = new Section[2];
        if (!shouldBeHoriz) {
            //code for drawing vertical walls
            int seed = Helper.randomNumber(sectionStart.x + 1, sectionStart.x + d.width - 1);

            int i = 0;
            while (seed == prev_hole && i++ < 20) {
                seed = Helper.randomNumber(sectionStart.x + 1, sectionStart.x + d.width - 1);
            }
            if (d.height == 3)
                    seed = 2;

            System.out.println("Wall at x = " + seed + " Prev hole @ x = (" + prev_hole + ")");

            int hole = Helper.randomNumber(sectionStart.y + 1, sectionStart.y + d.height - 1);
            System.out.println("Hole at (" + seed + ", " + hole + ")");

            for (i = sectionStart.y; i < sectionStart.y + d.height; i++) {
                if (i != hole) {
                    Point p = new Point(seed, i);
                    if (!Helper.pEqualsP(p, frame.sPoint) && !Helper.pEqualsP(p, frame.ePoint)) {
                        walls.add(p);
                    }
                }
            }

            // need to do (seed - sectionStart.x) so that we don't accidentally get a negative width when we shouldn't
            // it is also just the correct calculation b/c we want to get the width to the right of the wall WITHIN THE CURRENT SECTION
            res[0] = new Section(
                    new Dimension(
                            d.width - (seed - sectionStart.x),
                            d.height - 2 // minus 2 gives a padding of 1 on the top and the bottom (no stacked horizontal lines!)
                    ),
                    new Point(seed + 1, sectionStart.y + 1),
                    true, hole
            ); // right subsection
            System.out.println("Right subsection = " + res[0].dimension + ", start = " + res[0].startPoint);


            res[1] = new Section(
                    new Dimension(seed - sectionStart.x, d.height - 2),
                    new Point(sectionStart.x, sectionStart.y + 1),
                    true, hole
            ); // left subsection
            System.out.println("Left subsection = " + res[1].dimension + ", start = " + res[1].startPoint);
        } else {
            //code for horizontal walls
            int seed = Helper.randomNumber(sectionStart.y + 1, sectionStart.y + d.height - 1);

            int i = 0;
            while (seed == prev_hole && i++ < 20) {
                seed = Helper.randomNumber(sectionStart.y + 1, sectionStart.y + d.height - 1);
            }

            if (d.height == 3)
                    seed = 2;

            System.out.println("Wall at y = " + seed + " Prev hole @ y = " + prev_hole);
            int hole = Helper.randomNumber(sectionStart.x + 1, sectionStart.x + d.width - 1);
            System.out.println("Hole at (" + hole + ", " + seed + ")");

            for (i = sectionStart.x; i < sectionStart.x + d.width; i++) {
                if (i != hole) {
                    Point p = new Point(i, seed);
                    if (!Helper.pEqualsP(p, frame.sPoint) && !Helper.pEqualsP(p, frame.ePoint)) {
                        walls.add(p);
                    }
                }
            }

            res[0] = new Section(
                    new Dimension(
                            d.width - 2, // padding of 1 on the left and right (no stacked vertical lines!)
                            (seed - sectionStart.y)
                    ),
                    new Point(sectionStart.x + 1, sectionStart.y), // x + 1 to create the padding
                    false, hole
            ); // Top subsection
            System.out.println("Top subsection = " + res[0].dimension + ", start = " + res[0].startPoint);


            res[1] = new Section(
                    new Dimension(
                            d.width - 2,
                            d.height - (seed - sectionStart.y)
                    ),
                    new Point(sectionStart.x + 1, seed + 1),
                    false, hole
            ); // Bottom subsection
            System.out.println("Bottom subsection = " + res[1].dimension + ", start = " + res[1].startPoint);
        }
        return res;
    }

    boolean isLargeEnough(Section s) {
        return s.dimension.height > 2 && s.dimension.width > 2;
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
