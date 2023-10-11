package me.hel.pathfinding.evaluator;

import me.hel.pathfinding.node.IPathNode;
import me.hel.pathfinding.node.PathNodeConnection;

public abstract class AbstractCostEvaluator {

    public abstract double evaluate(IPathNode node1, IPathNode node2);

    public abstract double weightedCost(PathNodeConnection connection, IPathNode end);

}
