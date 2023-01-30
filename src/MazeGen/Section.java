package MazeGen;

import java.awt.*;

class Section {
    Dimension dimension;
    Point startPoint;
    boolean should_be_horizontal;
    int prev_hole; // tracks where the hole in the previous wall is so that the new one does not cover it up

    Section(Dimension dimension, Point startPoint, boolean should_be_horizontal, int prev_hole) {
        this.dimension = dimension;
        this.startPoint = startPoint;
        this.should_be_horizontal = should_be_horizontal;
        this.prev_hole = prev_hole;
    }
}