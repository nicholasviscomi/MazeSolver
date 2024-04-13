package Algorithms;

import DataStructures.Node;

import java.util.ArrayList;
import java.awt.Point;

public interface Algorithm {
    /*
    To be an algorithm, the class must have a function that solves the maze by going along
    and linking nodes to each other in the correct path order. There must then be a function
    get the found path. Along the way add every node that was searched to the openNodes
    variable in the main Frame
     */
    public boolean solve();
    public ArrayList<Point> getPath(Node<Point> ePoint);
}
