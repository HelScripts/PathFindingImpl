# PathFindingImpl
Basic pathfinding between two points, click to set a start and end point. Uses a [custom pathfinding algorithm](https://github.com/HelScripts/PathFindingImpl/blob/master/src/me/hel/pathfinding/evaluator/BasicWeightedDistanceCostEvaluator.java)
![image](https://github.com/HelScripts/PathFindingImpl/assets/64774759/5871a143-00b8-4f7c-a46e-16e6cbf85f03)

Green is start point, red is end point.
Orange tiles are searched but not part of the path, yellow is path.

Custom CostEvaluators can be implemented via [CostEvaluator](https://github.com/HelScripts/PathFindingImpl/blob/master/src/me/hel/pathfinding/evaluator/AbstractCostEvaluator.java) and parsed to the [PathFinder](https://github.com/HelScripts/PathFindingImpl/blob/master/src/me/hel/pathfinding/PathFinder.java)
