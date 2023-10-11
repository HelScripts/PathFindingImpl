package me.hel.pathfinding.node;

import me.hel.pathfinding.util.Visitable;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public interface IPathNode extends Visitable {

    Point2D getNodeLocation();
    IPathNode getParentNode();
    void setParentNode(IPathNode parent);
    double getCost();
    void setCost(double cost);
    ArrayList<PathNodeConnection> getConnected();

    default boolean equals(IPathNode o){

        double eval = Math.abs(getNodeLocation().getX() - o.getNodeLocation().getX())
                + Math.abs(getNodeLocation().getY() - o.getNodeLocation().getY());
        return eval < 1;
    }
}
