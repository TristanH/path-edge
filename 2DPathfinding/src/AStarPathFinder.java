import java.awt.Point;
import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarPathFinder {

	public final static int HEURISTIC_NONE = 0;
	public final static int HEURISTIC_MANHATTAN = 1;
	private Grid grid;
	private PriorityQueue<Integer> open = new PriorityQueue<Integer>(100, new fscoreComparator());
	private int[] fscore;
	private int[] hscore;
	private int[] costGrid;
	private int[] parent;
	private boolean[] visited;
	private int current;
	private int heuristicType;

	public AStarPathFinder(Grid gridIn) {
		this(gridIn, HEURISTIC_MANHATTAN );
	}
	
	public AStarPathFinder(Grid gridIn, int heuristicType){
		this.grid = gridIn;
		costGrid = grid.getCostMapID();
		parent = new int[grid.xBlocks * grid.yBlocks];
		hscore = new int[grid.xBlocks * grid.yBlocks];
		fscore = new int[grid.xBlocks * grid.yBlocks];
		visited = new boolean[grid.xBlocks * grid.yBlocks];
		this.heuristicType = heuristicType;
	}

	public Path findPath(Point start, Point end) {
		parent = new int[grid.xBlocks * grid.yBlocks];
		hscore = new int[grid.xBlocks * grid.yBlocks];
		fscore = new int[grid.xBlocks * grid.yBlocks];
		visited = new boolean[grid.xBlocks * grid.yBlocks];
		open = new PriorityQueue<Integer>(100,new fscoreComparator());
		
		current = grid.pointToID(start);
		int goal = grid.pointToID(end);

		while (current != goal) {
			visited[current] = true;
			addAdjacentBlocks();
			if (open.size() == 0)
				return null; // we've checked all the blocks, there is no path
		//	System.out.println(current + " " + fscore[current]);
			current = open.poll();

		}

		Path path = new Path();

		while (current != grid.pointToID(start)) {
			path.add(grid.idToPoint(current));
			current = parent[current];
		}
		path.add(start);

		// for(int i=0;i<path.steps.size();++i)
		// System.out.println(path.steps.get(i)+" "+fscore[grid.pointToID(path.steps.get(i))]);

		return path;
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
		if (grid.canWalk(current,to) && !visited[to]) {
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
			hscore[to] = 10*(Math.abs(current/grid.xBlocks - to/grid.xBlocks) + Math.abs(current%grid.xBlocks - to%grid.xBlocks));
		else if(heuristicType ==0)
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

	class fscoreComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer b1, Integer b2) {
			if (fscore[b1] < fscore[b2])
				return -1;
			else
				return 1;
		}
	}
}
