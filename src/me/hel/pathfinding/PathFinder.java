package me.hel.pathfinding;

import me.hel.pathfinding.evaluator.AbstractCostEvaluator;
import me.hel.pathfinding.node.IPathNode;
import me.hel.pathfinding.node.PathNodeConnection;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PathFinder {

    private final AbstractCostEvaluator evaluator;

    Consumer<IPathNode> onStep;

    private long stepSleep = 0;

    public PathFinder addStepCallback(Consumer<IPathNode> consumer){
        this.onStep = consumer;
        return this;
    }

    public PathFinder setStepSleep(long sleep){
        this.stepSleep = sleep;
        return this;
    }

    private final ArrayList<IPathNode> nodes = new ArrayList<>();
    public PathFinder(AbstractCostEvaluator evaluator){
        this.evaluator = evaluator;
    }

    public PathFinder add(IPathNode... nodes){
        this.nodes.addAll(List.of(nodes));
        return this;
    }

    public ArrayList<IPathNode> getNodes(){
        return nodes;
    }

    public PathFinder connect(IPathNode start, IPathNode end){
        PathNodeConnection connection = new PathNodeConnection(evaluator, start, end);
        start.getConnected().add(connection);
        return this;
    }

    public Optional<IPathNode> get(Point2D point){
        for(IPathNode pathNode : nodes){
            if(pathNode.getNodeLocation().equals(point)) return Optional.of(pathNode);
        }
        return Optional.empty();
    }

    public Optional<Path> generatePath(IPathNode start, IPathNode end){
        long startTime = System.currentTimeMillis();
        IPathNode current = start;
        Optional<Path> pathOptional = Optional.empty();

        ArrayList<IPathNode> visitedNodes = new ArrayList<>();
        ArrayList<PathNodeConnection> openConnections = new ArrayList<>();

        //To avoid NPE on first loop
        if(current.getConnected().size() == 0) return pathOptional;

        //Bad practice but shouldn't get stuck.
        while(true){
            visitedNodes.add(current);
            current.setVisited(true);
            if(current.equals(end)){

                ArrayList<IPathNode> path = new ArrayList<>();
                // generate path from parents
                while(current.getParentNode() != null){
                    path.add(0, current);
                    current = current.getParentNode();
                }
                pathOptional = Optional.of(new Path(path, System.currentTimeMillis() - startTime));
                break;
            }

            //Add all the possible connections that aren't already loaded
            for(PathNodeConnection connection : current.getConnected()){
                // filter if end point is already visited or the connection has been visited
                if(!connection.isVisited() && !connection.getEnd().isVisited()) openConnections.add(connection);
                connection.setVisited(true);
            }

            //Filter out connections to the end node we have just traversed to with previous loop
            openConnections.removeIf(i -> i.getEnd().isVisited());

            //Get smallest weighted cost node connection
            openConnections.sort(Comparator.comparingDouble(i -> evaluator.weightedCost(i, end)));

            //If there are no connections, the end node is unreachable from start node
            if(openConnections.size() == 0) break;


            //Get the lowest cost connection from sorted list
            PathNodeConnection connection = openConnections.remove(0);
            IPathNode next = connection.getEnd();

            //Set the parent to previous
            next.setParentNode(connection.getStart());

            //set cost to get to this node, use regular cost instead of weighted
            next.setCost(connection.getStart().getCost() + connection.getCost());

            // step to this node
            current = next;

            //Callback
            if(onStep != null){
                onStep.accept(current);
            }

            if(stepSleep > 0){
                try {
                    Thread.sleep(stepSleep);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }


        }

        //revert the connections and node states so they can be re-used in future paths
        visitedNodes.forEach(node -> {
            node.getConnected().forEach(connection ->  {
                connection.setVisited(false);
                connection.getStart().setVisited(false);
                connection.getEnd().setVisited(false);
            });
            node.setCost(0);
            node.setParentNode(null);
            node.setVisited(false);
        });


        return pathOptional;
    }
}
