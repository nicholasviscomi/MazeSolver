package MazeGen;

import java.awt.*;

class Section {
    Dimension dimension;
    Point startPoint;
    boolean should_be_horizontal;

    Section(Dimension dimension, Point startPoint, boolean horiz) {
        this.dimension = dimension;
        this.startPoint = startPoint;
        this.should_be_horizontal = horiz;
    }
}