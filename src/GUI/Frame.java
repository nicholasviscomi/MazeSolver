package GUI;

import Algorithms.Algorithm;
import Algorithms.FloodFill;
import DataStructures.Node;
import Algorithms.BreadthFirstSearch;
import Algorithms.BestFirstSearch;
import Helper.Helper;
import MazeGen.MazeGenerator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


/*
make a new project with very similar things except the mazes are borders and not whole blocks
they should be 4 individual lines around a point
when cells get connected the line on a certain side of the cell will be removed

look through the functions used in the solving algos and recreate them (funcs like isWall)
eventually try and carry that creation over to this project

*/
@SuppressWarnings("ALL")
public class Frame extends JPanel implements MouseListener, MouseMotionListener, ActionListener, KeyListener, ChangeListener {
    private static final long serialVersionUID = 1L;
    public int width = 1200;
    public int height = 600;
    public int gridSide = 60;
    public Point sPoint;
    public Point ePoint;

    static JFrame frame;
    // private JPanel panel;

    public ArrayList<Point> walls = new ArrayList<>();
    public ArrayList<ArrayList<Node<Point>>> grid = new ArrayList<>();
    public ArrayList<Point> path = new ArrayList<>();
    public ArrayList<Point> openNodes = new ArrayList<>();

    private boolean mouseOnScreen = false;

    private final JButton solve, clear, createMaze, bestFirst, floodFill;

    private final JSpinner maze_depth;
    // private JCheckBox diagonalCB;

    //ALGORITHM CLASSES
    private BreadthFirstSearch breadthFirstSearch;
    private BestFirstSearch bestFirstSearch;
    private FloodFill floodFillSearch;
    MazeGenerator mazeGen;

    private boolean pathOnScreen = false;
    private boolean pathIsDrawn = false;
    private boolean nodesAreDrawn = false;

    static Timer pathTimer;
    static Timer oNodeTimer;
    public static void main(String[] args) {
        new Frame();
    }

    public Frame() {
        setLayout(null);
        setFocusable(true);
        requestFocus();
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        sPoint = new Point((int) Math.floor(width/gridSide * 0.2),  (int) Math.floor(height/gridSide * 0.5));
        System.out.println("sPoint = " + sPoint);
        ePoint = new Point((int) Math.floor(width/gridSide * 0.8),  (int) Math.floor(height/gridSide * 0.5));
        System.out.println("ePoint = " + ePoint);

        frame = new JFrame();
        frame.setContentPane(this);
        frame.getContentPane().setPreferredSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Maze Solver");
        frame.setSize(width, height);
        frame.pack();
        frame.setLocationRelativeTo(null);

        solve = new JButton("Breadth First");
        Dimension solveSize = solve.getPreferredSize();
        solve.setBounds(30, 530, solveSize.width, solveSize.height);
        solve.setOpaque(true);
        solve.setVisible(true);
        solve.addActionListener(this);

        clear = new JButton("Clear");
        Dimension clearSize = clear.getPreferredSize();
        clear.setBounds(30, 530+solveSize.height, clearSize.width, clearSize.height);
        clear.setOpaque(true);
        clear.setVisible(true);
        clear.addActionListener(this);

        createMaze = new JButton("Create Maze");
        Dimension createMazeSize = createMaze.getPreferredSize();
        createMaze.setBounds(30 + solveSize.width, 530, createMazeSize.width, createMazeSize.height);
        createMaze.setOpaque(true);
        createMaze.setVisible(true);
        createMaze.addActionListener(this);

        bestFirst = new JButton("Best First Search");
        Dimension bfsSize = bestFirst.getPreferredSize();
        bestFirst.setBounds(30 + clearSize.width, 530 + solveSize.height, bfsSize.width, bfsSize.height);
        bestFirst.setOpaque(true);
        bestFirst.setVisible(true);
        bestFirst.addActionListener(this);

        floodFill = new JButton("Flood Fill Search");
        Dimension aSize = floodFill.getPreferredSize();
        floodFill.setBounds(bestFirst.getX() + bfsSize.width + 5, bestFirst.getY(), aSize.width, aSize.height);
        floodFill.setOpaque(true);
        floodFill.setVisible(true);
        floodFill.addActionListener(this);

        SpinnerModel model = new SpinnerNumberModel(0, 0, gridSide, 1);
        maze_depth = new JSpinner(model);
        Dimension d = maze_depth.getPreferredSize();
        maze_depth.setBounds(
                createMaze.getX() + createMazeSize.width + 10, createMaze.getY(),
                d.width, d.height
        );
        maze_depth.addChangeListener(this);
        maze_depth.setVisible(true);

        frame.add(solve);
        frame.add(clear);
        frame.add(createMaze);
        frame.add(bestFirst);
        frame.add(maze_depth);
        frame.add(floodFill);

        frame.setVisible(true);
        
        createGrid();

        pathTimer = new Timer(35, this);
        oNodeTimer = new Timer(2, this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //draw the walls on
        for (Point point : walls) {
            if ((point.y == sPoint.y*gridSide && point.x == sPoint.x*gridSide) || (point.y == ePoint.y*gridSide && point.x == ePoint.x*gridSide)) {
                walls.remove(point);
                continue;
            }
            g.setColor(Color.BLACK);
            g.fillRect(point.x * gridSide, point.y * gridSide, gridSide, gridSide);
        }

        //create the grid
        for (int y = 0; y < height; y+=gridSide) {
            for (int x = 0; x < width; x+=gridSide) {
                if (y == sPoint.y*gridSide && x == sPoint.x*gridSide) {
                    g.setColor(Color.ORANGE);
                    g.fillRect(x, y, gridSide, gridSide);
                } else if (y == ePoint.y*gridSide && x == ePoint.x*gridSide) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x, y, gridSide, gridSide);
                } else {
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, gridSide, gridSide);
                }
            }
        }

        //animate drawing the path 
        if (path.size() > 0 && pathIndex < path.size() && !pathIsDrawn) {
            if (pathTimer.isRunning()) {
                for (int i = 0; i <= pathIndex; i++) {
                    Point n = path.get(i);
                    // System.out.println("paint spoint: " + n.value);
                    g.setColor(new Color(0, 0, 0, 80));
                    g.fillRect(n.x*gridSide, n.y*gridSide, gridSide, gridSide);
                }
                if (pathIndex == path.size()-1) {
                    pathTimer.stop();
                    pathIsDrawn = true;
                    pathIndex = 0;
                }
            
            }
        } 

        // drawing open nodes after they have been animated on
        if (nodesAreDrawn) {
            for (Point n : openNodes) {
                g.setColor(new Color(255, 165, 0, 45));
                g.fillRect(n.x*gridSide, n.y*gridSide, gridSide, gridSide);
            }
        }

        // animate drawing open nodes
        if (openNodes.size() > 0 && oNodeIndex < openNodes.size() && !nodesAreDrawn) {
            if (oNodeTimer.isRunning()) {
                for (int i = 0; i <= oNodeIndex; i++) {
                    // System.out.print("open node: "); Helper.printPoint(n);
                    Point n = openNodes.get(i);
                    g.setColor(new Color(255, 165, 0, 45));
                    g.fillRect(n.x*gridSide, n.y*gridSide, gridSide, gridSide);
                }
                if (oNodeIndex == openNodes.size()-1) {
                    oNodeTimer.stop();
                    nodesAreDrawn = true;
                    oNodeIndex = 0;
                }
            }
        }

    }

    public Node<Point> nodeAtPoint(Point p) {
        return grid.get(p.y).get(p.x);
    }

    void solve(Algorithm alg) {
        if (pathOnScreen) { return; }
        
        createGrid();

        System.out.print("solve: ");
        Helper.printPoint(sPoint);

        if (alg.solve()) {
            path.clear();
            path = alg.getPath(nodeAtPoint(ePoint));
            if (!alg.equals(floodFillSearch)) {
                path = Helper.reverse(path);
            }
            pathTimer.start();
            oNodeTimer.start();
        }

        frame.repaint();
        requestFocus();
        pathOnScreen = true;
    }

    void clear() {
        if (!pathOnScreen) {
            walls.clear();
        }
        path.clear();
        openNodes.clear();
        pathOnScreen = false;
        pathIsDrawn = false;
        nodesAreDrawn = false;
        pathIndex = 0;
        oNodeIndex = 0;
        repaint();
        requestFocus();
        createGrid();
    }

    int pathIndex = 0;
    int oNodeIndex = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == solve) {
            breadthFirstSearch = new BreadthFirstSearch(grid, sPoint, ePoint, this);
            oNodeTimer.setDelay(2);
            solve(breadthFirstSearch);
            return;
        }

        if (e.getSource() == bestFirst) {
            bestFirstSearch = new BestFirstSearch(grid, sPoint, ePoint, this);
            oNodeTimer.setDelay(7);
            solve(bestFirstSearch);
            return;
        }

        if (e.getSource() == floodFill) {
            floodFillSearch = new FloodFill(grid, sPoint, ePoint, this);
            oNodeTimer.setDelay(7);
            solve(floodFillSearch);
            return;
        }

        if (e.getSource() == clear) {
            clear();
            return;
        }

        if (e.getSource() == pathTimer) {
            if (!nodesAreDrawn) { return; }
            if (pathIndex == path.size()-1) {
                System.out.println("stopping path timer! ");
                pathTimer.stop();
                pathIsDrawn = true;
                pathIndex = 0;
            }
            pathIndex++;
            repaint();
        }

        if (e.getSource() == oNodeTimer) {
            if (oNodeIndex == openNodes.size()-1) {
                System.out.println("stopping oNode timer!");
                oNodeTimer.stop();
                nodesAreDrawn = true;
                oNodeIndex = 0;
            }
            oNodeIndex++;
            repaint();
        }

        if (e.getSource() == createMaze) {
            clear();
            mazeGen = new MazeGenerator(sPoint, ePoint, grid, this);
            walls.clear();
            walls = mazeGen.recursive_division((Integer) maze_depth.getValue());
            repaint();
        }
         
    }

	@Override
	public void mouseEntered(MouseEvent e) {
        // System.out.println("entered screen");
        mouseOnScreen = true;
    }

	@Override
	public void mouseExited(MouseEvent e) {
        // System.out.println("exited screen");
        mouseOnScreen = false;
    }

    @Override
	public void mouseClicked(MouseEvent e) {
        if (mouseOnScreen && !pathOnScreen) {
            double x = e.getX();
            double y = e.getY();

            x = x/gridSide;
            y = y/gridSide;

            int sqX = (int) Math.ceil(x) - 1;
            int sqY = (int) Math.ceil(y) - 1;
    
            Point p = new Point(sqX, sqY);
            
            if (walls.contains(p)) { return; }

            if (!(Helper.pEqualsP(p, ePoint) || Helper.pEqualsP(p, sPoint))) {
                walls.add(p);
            }
            // }
            repaint();
        }
    }

	@Override
	public void mouseDragged(MouseEvent e) {
        if (mouseOnScreen && !pathOnScreen) {
            double x = e.getX();
            double y = e.getY();   

            // if ( x >= 595 || y >= 575) { return; }

            x = x/gridSide;
            y = y/gridSide;

            int sqX = (int) Math.ceil(x) - 1;
            int sqY = (int) Math.ceil(y) - 1;

            Point p = new Point();
            p.x = sqX;
            p.y = sqY;

            if (walls.contains(p)) { return; }

            if (!(Helper.pEqualsP(p, ePoint) || Helper.pEqualsP(p, sPoint))) {
                walls.add(p);
            }

            repaint();
        }
    }

    void createGrid() {
        grid.clear();

        for (int y = 0; y <= height; y+=gridSide) {
            ArrayList<Node<Point>> row = new ArrayList<>();
            for (int x = 0; x < width; x+=gridSide) {
                Point p = new Point(x/gridSide, y/gridSide);
                Node<Point> n = new Node<>(null, p, this);
                row.add(n);
            }
            grid.add(row);
        }
        // System.out.println(grid.get(0).size());
    }

    public Point getSPoint() {
        return sPoint;
    }
    
    public Point getEPoint() {
        return ePoint;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    @Override
    public void keyPressed(KeyEvent e) {
        // currKey = e.getKeyChar();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // currKey = (char) 0;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
	public void mousePressed(MouseEvent e) {}

	@Override
    public void mouseReleased(MouseEvent e) {}
    
	@Override
	public void mouseMoved(MouseEvent e) {}


    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == maze_depth) {
            clear();
            mazeGen = new MazeGenerator(sPoint, ePoint, grid, this);
            walls.clear();
            walls = mazeGen.recursive_division((Integer) maze_depth.getValue());
            repaint();
        }
    }
}
