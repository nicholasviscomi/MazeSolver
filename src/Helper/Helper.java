package Helper;

import Algorithms.BestFirstSearch;
import DataStructures.*;

import java.util.ArrayList;
import java.awt.*;
import java.util.Random;


public class Helper {
    public static void printGrid(ArrayList<ArrayList<Node<Point>>> grid) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                System.out.print("(" + grid.get(i).get(j).value.x + "," + grid.get(i).get(j).value.y + ")");
                System.out.print(" ");
            }
        }
    }

    public static void printPoint(Point p) {
        System.out.println("(" + p.x + "," + p.y + ")");
    }

    public static boolean pEqualsP(Point p1, Point p2) {
        return (p1.x == p2.x && p1.y == p2.y);
    }
    
    public static <T> ArrayList<T> reverse(ArrayList<T> arr) {
        ArrayList<T> copy = new ArrayList<>();
        for (int i = arr.size()-1; i > -1; i--) {
            copy.add(arr.get(i));
        }
        return copy;
    }

    public static void swap(Point[] arr, int x, int y) {
        Point temp = arr[y];
        arr[y] = arr[x];
        arr[x] = temp;
    }

    public static Point[] bubbleSort(Point[] arr, BestFirstSearch dbf) {
        for (int step = 0; step < arr.length - 2; step++) {
            for (int i = 0; i < arr.length - 1; i++) {
                double d1 = dbf.distFromEnd(arr[i]);
                double d2 = dbf.distFromEnd(arr[i + 1]);
                if (d1 > d2) {
                    swap(arr, i, i + 1);
                }
            }
        }
        return arr;
    }

    public static int randomNumber(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
