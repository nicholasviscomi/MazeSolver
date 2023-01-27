package MazeGen;

import java.awt.*;

class Section {
    Dimension dimension;
    Point startPoint;
    boolean horiz;

    Section(Dimension dimension, Point startPoint, boolean horiz) {
        this.dimension = dimension;
        this.startPoint = startPoint;
        this.horiz = horiz;
    }
}