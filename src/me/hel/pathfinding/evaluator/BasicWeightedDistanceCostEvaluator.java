package me.hel.pathfinding.evaluator;

import me.hel.pathfinding.node.IPathNode;
import me.hel.pathfinding.node.PathNodeConnection;

import java.awt.geom.Point2D;
public class BasicWeightedDistanceCostEvaluator extends AbstractCostEvaluator {

    private double weight = 5.0;

    public BasicWeightedDistanceCostEvaluator setWeight(double weight){
        this.weight = weight;
        return this;
    }

    @Override
    public double evaluateCost(IPathNode node1, IPathNode node2) {

        Point2D location1 = node1.getNodeLocation();
        Point2D location2 = node2.getNodeLocation();

        double deltaX = location2.getX() - location1.getX();
        double deltaY = location2.getY() - location1.getY();

        return Math.abs(deltaX) + Math.abs(deltaY);
    }

    @Override
    public double weightedCost(PathNodeConnection connection, IPathNode end) {
        Point2D endLocation = end.getNodeLocation();
        Point2D connectionEndLocation = connection.getEnd().getNodeLocation();

        double deltaX = endLocation.getX() - connectionEndLocation.getX();
        double deltaY = endLocation.getY() - connectionEndLocation.getY();

        double distance = Math.sqrt(Math.abs(deltaX) * Math.abs(deltaX)
                + Math.abs(deltaY) * Math.abs(deltaY));
        return distance * weight + connection.getCost();
    }
}
