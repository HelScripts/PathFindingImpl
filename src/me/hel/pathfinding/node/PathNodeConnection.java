package me.hel.pathfinding.node;

import me.hel.pathfinding.evaluator.AbstractCostEvaluator;
import me.hel.pathfinding.util.Visitable;

public class PathNodeConnection implements Visitable {

    private final IPathNode start; // Where we start traversing this connection
    private final IPathNode end; // Where we end up traversing this connection
    private final double cost; // Evaluator#cost cached
    private boolean visited = false; // Has the path finder loaded this connection

    public PathNodeConnection(AbstractCostEvaluator evaluator,
                              IPathNode start,
                              IPathNode end) {

        this.start = start;
        this.end = end;
        this.cost = evaluator.evaluateCost(start, end);
    }

    public IPathNode getStart(){
        return start;
    }

    public IPathNode getEnd(){
        return end;
    }
    @Override
    public boolean isVisited() {
        return visited;
    }

    @Override
    public void setVisited(boolean b) {
        this.visited = b;
    }

    public double getCost(){
        return cost;
    }
}
