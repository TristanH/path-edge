package tools;
import java.awt.Point;
import java.util.Comparator;
import java.util.PriorityQueue;

/** A path finding class which determines the shortest path from one point on a grid to the other in the most efficient manner */
public class AStarPathFinder {

	public final static int HEURISTIC_NONE = 0;
	public final static int HEURISTIC_MANHATTAN = 1;
	public final static int HEURISTIC_DIAGONAL = 2;
	private Grid grid;
	private PriorityQueue<Integer> open = new PriorityQueue<Integer>(100, new fscoreComparator());
	private int[] fscore;
	private int[] hscore;
	private int[] costGrid;
	private int[] parent;
	private boolean[] visited;
	private int xBlocks, yBlocks;
	private int current;
	private int heuristicType;
	private double timeTaken = 0;//in ms

	/** Creates the Pathfinder using the default heuristic, the manhattan method
	 * 
	 * @param gridIn The grid to find a path on.
	 */
	public AStarPathFinder(Grid gridIn) {
		this(gridIn, HEURISTIC_MANHATTAN );
	}
	
	/** Creates the pathfinder using the given heuristic type
	 * 
	 * @param gridIn The grid to find a path on.
	 * @param heuristicType This must be a 0 (no heuristic), 1 (Manhattan heuristic) or 2 (Diagonal heuristic).
	 */
	public AStarPathFinder(Grid gridIn, int heuristicType){
		this.grid = gridIn;
		costGrid = grid.getCostMapID();
		xBlocks = grid.getXBlocks();
		yBlocks = grid.getYBlocks();
		parent = new int[xBlocks * yBlocks];
		hscore = new int[xBlocks * yBlocks];
		fscore = new int[xBlocks * yBlocks];
		visited = new boolean[xBlocks * yBlocks];
		this.heuristicType = heuristicType;
	}

	/** Finds the shortest path from start to end on the grid supplied in the constructor.
	 * 
	 * @param start The starting point, as a 2D coordinate on the grid.
	 * @param end The destination point, as a 2D coordinate on the grid.
	 * @return The shortest path from start to end.
	 */
	public Path findPath(Point start, Point end) {
		long startTime = System.nanoTime();
		parent = new int[xBlocks * yBlocks];
		hscore = new int[xBlocks * yBlocks];
		fscore = new int[xBlocks * yBlocks];
		visited = new boolean[xBlocks * yBlocks];
		open = new PriorityQueue<Integer>(100,new fscoreComparator());
		
		current = grid.pointToID(start);
		int goal = grid.pointToID(end);
		
		if(grid.costID[current]==-1)
			return null;

		while (current != goal) {
			visited[current] = true;
			addAdjacentBlocks();
			if (open.size() == 0)
				return null; // we've checked all the blocks, there is no path
			current = open.poll();

		}

		Path path = new Path();

		while (current != grid.pointToID(start)) {
			path.add(grid.idToPoint(current));
			current = parent[current];
		}
		path.add(start);

		timeTaken = (System.nanoTime() - startTime)/1000000.0;
		return path;
	}
	
	/** Returns how long it took to find the most recent path.
	 * 
	 * @return The time taken (in milliseconds).
	 */
	public double getLastTimeTaken(){
		return timeTaken;
	}

	private void addAdjacentBlocks() {
		addToOpen(grid.IDAbove(current));
		addToOpen(grid.IDBelow(current));
		addToOpen(grid.IDLeft(current));
		addToOpen(grid.IDRight(current));

		addToOpen(grid.IDAbove(grid.IDLeft(current)));
		addToOpen(grid.IDAbove(grid.IDRight(current)));
		addToOpen(grid.IDBelow(grid.IDLeft(current)));
		addToOpen(grid.IDBelow(grid.IDRight(current)));
	}

	private void addToOpen(int to) {
		if (grid.canWalkID(current,to) && !visited[to]) {
			if (!open.contains(to)) {
				updateHScore(to);
				updateFScore(to);
				open.add(to);
				parent[to] = current;
			} else if (scoreIsBetterNow(to)) {
				open.remove(to);
				updateFScore(to);
				open.add(to);
				parent[to] = current;
			}
		}
	}
	
	private void updateHScore(int to){
		if(heuristicType==1)
			hscore[to] = 10*(Math.abs(current/xBlocks - to/xBlocks) + Math.abs(current%xBlocks - to%xBlocks));
		else if(heuristicType==2){
			int xDist = Math.abs(current%xBlocks - to%xBlocks);
			int yDist = Math.abs(current/xBlocks - to/xBlocks);
			hscore[to] = xDist > yDist ?
					14*yDist + 10*(xDist-yDist) : 
					14*xDist + 10*(yDist-xDist);
		}
		else 
			hscore[to]=0;
	}

	private void updateFScore(int to) {
		int costFix = grid.diagonal(current,to)? 14 : 10;
		fscore[to] = fscore[current] + costFix*costGrid[to] + hscore[to];
	}

	private boolean scoreIsBetterNow(int to) {
		int costFix = grid.diagonal(current,to)? 14 : 10;
		return fscore[current] + costFix*costGrid[to] + hscore[to]< fscore[to];
	}

	private class fscoreComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer b1, Integer b2) {
			if (fscore[b1] < fscore[b2])
				return -1;
			else
				return 1;
		}
	}
}
