package Algorithms;

import DataStructures.Node;

import java.util.ArrayList;
import java.awt.Point;

public interface Algorithm {
    public boolean solve();
    public ArrayList<Point> getPath(Node<Point> ePoint);
}
