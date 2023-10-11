package me.hel.pathfinding.example;

import me.hel.pathfinding.Path;
import me.hel.pathfinding.PathFinder;
import me.hel.pathfinding.node.IPathNode;
import me.hel.pathfinding.node.PathNodeConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Optional;

/**
 *
 *  Messy implementation
 *
 */
public class GUI extends JFrame implements MouseWheelListener {

    private static final double minZoom = 0.5;
    private static final double maxZoom = 10.0;
    private static double zoom = 1.0;

    private JPanel content;
    private Optional<Path> lastPath = Optional.empty();

    private final PathFinder pathFinder;

    private IPathNode start;

    JSlider sizeX = new JSlider(10, 150, 50);
    JSlider sizeY = new JSlider(10, 150, 50);
    JSlider obstaclePercent = new JSlider(0, 85, 50);
    public GUI(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();


        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;

        panel.add(new JLabel("Size X"), constraints);
        constraints.gridy = 1;

        panel.add(sizeX, constraints);

        constraints.gridy = 2;
        panel.add(new JLabel("Size Y"), constraints);
        constraints.gridy = 3;

        panel.add(sizeY, constraints);

        constraints.gridy = 4;
        panel.add(new JLabel("Obstacle %"), constraints);
        constraints.gridy = 5;

        panel.add(obstaclePercent, constraints);
        constraints.gridy = 6;
        JButton generateMap = new JButton("Regenerate Map");
        generateMap.addActionListener((event) -> {
            populateMap();
            content.revalidate();
        });
        panel.add(generateMap, constraints);

        panel.add(new JButton("Button2"), constraints);

        JPanel global = new JPanel();


        content = new JPanel();
        content.setLayout(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(content);
        content.addMouseWheelListener(this);

        populateMap();

        pathFinder.addStepCallback((pathNode) -> {
            if(pathNode instanceof WalkableCell){ // higher version could use WalkableCell walkableCell
                WalkableCell walkableCell = (WalkableCell) pathNode;
                walkableCell.setBackground(Color.ORANGE);
            }
        });

        global.add(panel);
        global.add(scrollPane);
        this.setContentPane(global);
        pack();
        setVisible(true);
    }


    private void populateMap(){
        content.removeAll();
        pathFinder.getNodes().clear();
        GridBagConstraints gbc = new GridBagConstraints();
        for(int x = 0; x < sizeX.getValue(); x++){
            for(int y = 0; y < sizeY.getValue(); y++){
                gbc.gridx = x;
                gbc.gridy = y;


                if(Math.random() * 100 <= obstaclePercent.getValue()){
                    content.add(new BlockedCell(), gbc);
                }else {
                    WalkableCell cell = new WalkableCell(x, y);
                    pathFinder.add(cell);
                    content.add(cell, gbc);
                }
            }
        }

        for(int x = 0; x < sizeX.getValue(); x++){
            for(int y = 0; y < sizeY.getValue(); y++){
                Optional<IPathNode> nodeOpt = pathFinder.get(new Point2D.Double(x, y));

                if(nodeOpt.isEmpty()) continue;

                IPathNode node = nodeOpt.get();

                Optional<IPathNode> up = pathFinder.get(new Point2D.Double(x, y+1));
                Optional<IPathNode> down = pathFinder.get(new Point2D.Double(x, y-1));
                Optional<IPathNode> left = pathFinder.get(new Point2D.Double(x - 1, y));
                Optional<IPathNode> right = pathFinder.get(new Point2D.Double(x + 1, y));

                up.ifPresent(iPathNode -> pathFinder.connect(node, iPathNode));
                down.ifPresent(iPathNode -> pathFinder.connect(node, iPathNode));
                left.ifPresent(iPathNode -> pathFinder.connect(node, iPathNode));
                right.ifPresent(iPathNode -> pathFinder.connect(node, iPathNode));
            }
        }
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.getWheelRotation() < 0){ // zoom inward
            System.out.println("ZOOM INWARD");
            if(zoom < maxZoom) zoom += 0.1;

        }else if(e.getWheelRotation() > 0) {
            System.out.println("ZOOM OUTWARD");
            if(zoom > minZoom) zoom -= 0.1;

        }
        content.repaint();
        content.revalidate();
    }

    private class BlockedCell extends JPanel {
        public BlockedCell(){
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        @Override
        public Dimension getPreferredSize(){
            return new Dimension((int)Math.round(50 * GUI.zoom), (int)Math.round(50 * GUI.zoom));
        }

    }

    private class WalkableCell extends JPanel implements IPathNode, MouseListener {

        IPathNode parent;
        double cost;
        ArrayList<PathNodeConnection> connections = new ArrayList<>();
        boolean visited;
        Point2D nodeLocation;


        public WalkableCell(int x, int y){
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            this.nodeLocation = new Point2D.Double(x, y);
            addMouseListener(this);
        }

        @Override
        public Dimension getPreferredSize(){
            return new Dimension((int)Math.round(50 * GUI.zoom), (int)Math.round(50 * GUI.zoom));
        }

        private void update(){
            if(lastPath.isPresent()){

                Path path = lastPath.get();
                if(path.getStart().equals(this)){
                    setBackground(Color.GREEN);
                }else if(path.getEnd().equals(this)){
                    setBackground(Color.RED);
                }else if(path.getNodes().contains(this)){
                    setBackground(Color.YELLOW);
                }else{
                    setBackground(Color.WHITE);
                }
            }else{
                if(start != null && this.equals(start)){
                    setBackground(Color.GREEN);
                }else{
                    setBackground(Color.WHITE);
                }
            }
        }

        @Override
        public Point2D getNodeLocation() {
            return nodeLocation;
        }

        @Override
        public IPathNode getParentNode() {
            return parent;
        }

        @Override
        public void setParentNode(IPathNode parent) {
            this.parent = parent;
        }

        @Override
        public double getCost() {
            return cost;
        }

        @Override
        public void setCost(double cost) {
            this.cost = cost;
        }

        @Override
        public ArrayList<PathNodeConnection> getConnected() {
            return connections;
        }

        @Override
        public boolean isVisited() {
            return visited;
        }

        @Override
        public void setVisited(boolean b) {
            this.visited = b;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(start == null){
                System.out.println("Set startpoint!");
                start = this;
                lastPath = Optional.empty();
                // update all
                pathFinder.getNodes().forEach( i -> {
                    if(i instanceof WalkableCell){
                        WalkableCell walkableCell = (WalkableCell) i; // higher language could have put in instanceof
                        walkableCell.update();
                    }
                });
            }else {
                lastPath = pathFinder.generatePath(start, this);
                if(lastPath.isPresent()){
                    Path path = lastPath.get();
                    System.out.println("Generated path in " + path.getExecutionTime());
                    path.getNodes().stream().filter(i -> !i.equals(path.getEnd()) && !i.equals(path.getStart())).forEach(i -> {
                        if(i instanceof WalkableCell){
                            WalkableCell walkableCell = (WalkableCell) i;
                            walkableCell.setBackground(Color.YELLOW);
                        }
                    });
                }else{
                    System.out.println("Path generation failed.");
                }

                start = null;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setBackground(Color.GREEN);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            update();
        }
    }
}
