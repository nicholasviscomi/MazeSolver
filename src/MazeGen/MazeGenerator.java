package MazeGen;

import DataStructures.Node;
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
    // Random rand = new Random();

    public MazeGenerator(Point sPoint, Point ePoint, ArrayList<ArrayList<Node<Point>>> grid, GUI.Frame frame) {
        this.sPoint = sPoint;
        this.ePoint = ePoint;
        this.grid = grid;
        this.frame = frame;
    }

    public ArrayList<Point> recursiveDivision() {
        setVisited();
        
        // System.out.println("Generate Maze");

        //@Error - still doesn't work completely but its better
        //       - height and width are sometimes just 1 short
        Section[] curr = divide(walls, new Dimension(frame.getWidth()/20, frame.getHeight()/20), new Point(0, 0));

        // Section[] two = divide(walls, curr[0].dimension, curr[0].startPoint);

        // divide(walls, two[0].dimension, two[0].startPoint);

        while (curr != null) {
            curr = divide(walls, curr[1].dimension, curr[1].startPoint);
        }

        return walls;
    }

    int sectionCount = 0;
    /* 
    @param sectionStart = top left section being divided
    @param section = width and height of section being divided
    */
    private Section[] divide(ArrayList<Point> walls, Dimension section, Point sectionStart) {
        if (section.width < 2 || section.height < 2) {
            System.out.println("section is too small bro");
            return null;
        }

        boolean horiz = (section.height > section.width);
        System.out.println("Main section #" + sectionCount + "--> \n\tisHoriz: " + horiz + " section: " + section + " Start: " + sectionStart);
        /*
        ----width and height could be calculated incorrectly----

        maybe keep track of where the last hole was at and pass it as a param so no wall covers a hole

        STEPS: http://weblog.jamisbuck.org/2011/1/12/maze-generation-recursive-division-algorithm
        */

        if (horiz) {
            int endCount = 0;
            int ry = Helper.randomNumber(0, section.height-1);
            while (isWall(new Point(sectionStart.x, ry-1)) || isWall(new Point(sectionStart.y, ry +1))) {
                if (endCount > 100) {
                    // System.out.println("tried too many times!");
                    return null;
                } else {
                    ry = Helper.randomNumber(0, section.height-1);
                    endCount++;
                    // System.out.println("new random!");
                    //one to the left or the right is a wall. Should return so the maze has a real path
                }
            }

            int holeX = Helper.randomNumber(sectionStart.x, sectionStart.x + section.width);

            for(int x = sectionStart.x; x < sectionStart.x + section.width ; x++) {
                Point p = new Point(x, ry);
                if (x != holeX && !Helper.pEqualsP(p, sPoint) && !Helper.pEqualsP(p, ePoint)) {// will create one opening in the bisector
                    walls.add(new Point(x, ry));
                }
            }

            Dimension d = new Dimension(section.width, ry-1);
            Dimension d2 = new Dimension(section.width, section.height - ry);
            Point p = new Point(sectionStart.x, sectionStart.y);
            Point p2 = new Point(sectionStart.x, ry+1);

            Section[] sections = {new Section(d, p), new Section(d2, p2)};

            // divide(walls, d, p);//divide the top section
            // divide(walls, d2, p2);//divide the bottom section

            for (Section s : sections) {
                // divide(walls, s.dimension, s.startPoint);  
                System.out.println("\t\tNew Section --> \n\t\t\tisHoriz: " + (s.dimension.height > s.dimension.width) + " section: " + s.dimension + " Start: " + s.startPoint);
            }

            sectionCount++;
            return sections;
        } else {
            int endCount = 0; //if this gets to 60 there are no new positions that the maze could make a spot at
            int rx = Helper.randomNumber(0, section.width-1);
            while (isWall(new Point(rx+1, sectionStart.y)) || isWall(new Point(rx-1, sectionStart.y))) {
                if (endCount > 100) {
                    // System.out.println("tried too many times!");
                    return null;
                } else {
                    rx = Helper.randomNumber(0, section.width-1);
                    endCount++;
                    // System.out.println("new random!");
                    //one to the left or the right is a wall. Should return so the maze has a real path
                }
            }
            int holeY = Helper.randomNumber(sectionStart.y, sectionStart.y + section.height);

            for (int y = sectionStart.y; y < sectionStart.y + section.height ; y++) {
                Point p = new Point(rx, y);
                if (y != holeY && !Helper.pEqualsP(p, sPoint) && !Helper.pEqualsP(p, ePoint)) {// will create one opening in the bisector
                    walls.add(new Point(rx, y));
                }
            }

            Dimension d = new Dimension(rx-1, section.height);
            Dimension d2 = new Dimension(section.width - rx, section.height);
            Point p = new Point(sectionStart.x, sectionStart.y);
            Point p2 = new Point(rx+1, sectionStart.y);

            Section[] sections = {new Section(d2, p2), new Section(d, p)};

            for (Section s : sections) {
                // divide(walls, s.dimension, s.startPoint);
                System.out.println("\t\tNew Section --> \n\t\t\tisHoriz: " + (s.dimension.height > s.dimension.width) + " section: " + s.dimension + " Start: " + s.startPoint);
            }

            sectionCount++;
            return sections;
        }

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

class Section {
    Dimension dimension;
    Point startPoint;

    Section(Dimension dimension, Point startPoint) {
        this.dimension = dimension;
        this.startPoint = startPoint;
    }
}