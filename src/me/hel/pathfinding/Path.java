package me.hel.pathfinding;

import me.hel.pathfinding.node.IPathNode;

import java.util.ArrayList;

public class Path {

    private final ArrayList<IPathNode> nodes; // Does not include start as we are already here
    private final IPathNode start;
    private final IPathNode end;
    private final double cost;
    private final long executionTime;

    public Path(ArrayList<IPathNode> nodes, long executionTime){
        this.nodes = nodes;
        start = nodes.get(0).getParentNode();
        end = nodes.get(nodes.size() - 1);
        System.out.println("Path generated, end point is " + end.getNodeLocation());
        cost = end.getCost();
        this.executionTime = executionTime;
    }

    public long getExecutionTime(){
        return executionTime;
    }

    public double getCost(){
        return cost;
    }

    public IPathNode getStart(){
        return start;
    }

    public IPathNode getEnd(){
        return end;
    }

    public ArrayList<IPathNode> getNodes(){
        return nodes;
    }
}
