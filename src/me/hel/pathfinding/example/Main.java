package me.hel.pathfinding.example;

import me.hel.pathfinding.PathFinder;
import me.hel.pathfinding.evaluator.BasicWeightedDistanceCostEvaluator;

public class Main {

    public static void main(String... args){
        new GUI(new PathFinder(new BasicWeightedDistanceCostEvaluator()));
    }
}
