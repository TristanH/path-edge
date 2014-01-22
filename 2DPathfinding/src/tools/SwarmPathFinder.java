package tools;
import java.awt.Point;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;


/** Determines the shortest distance (and path) to a target location from EVERY other spot on the given grid */
public class SwarmPathFinder {
	
	private int[] distance;
	private boolean[] visited;
	private static final double r2=Math.sqrt(2)-1;
	private Grid grid;
	private PriorityQueue<Integer> open=new PriorityQueue<Integer>(100, new swarmComparator());
	private int startID;
	private int check;

	
	/**
	 * 
	 * @param grid The grid to find paths on.
	 */
	public SwarmPathFinder(Grid grid){
		this.grid = grid;
	}
	
	
	public int[][] findDistsFrom(Point start){
		distance = new int[grid.getTotalBlocks()];
		visited=new boolean[grid.getTotalBlocks()];
		for(int i=0;i<grid.getXBlocks();++i){
			for(int j=0;j<grid.getYBlocks();++j){
				distance[grid.pointToID(new Point(i,j))]=-1;
			}
		}
		
		startID = grid.pointToID(start);
		
		distance[startID]= 0;
		open.add(startID);
		scoreGrid();
		
		//after finding the distance to every point, we have to change the info back into usable form
		int[][] fixedDist = new int[grid.getXBlocks()][grid.getYBlocks()];
		for(int i=0;i<grid.getXBlocks();++i){
			for(int j=0;j<grid.getYBlocks();++j){
				fixedDist[i][j] = distance[grid.pointToID(new Point(i,j))];
				//System.out.printf("%5d",fixedDist[i][j]);
			}
			//System.out.println();
		}
		
		
		
		return fixedDist;
	}
	
	private void scoreGrid(){
		while(!open.isEmpty()){
			check = open.poll();
			updateSurrounding();
			visited[check] = true;
		}
	}
	
	private void updateSurrounding() {
		checkUpdate(grid.IDAbove(check));
		checkUpdate(grid.IDBelow(check));
		checkUpdate(grid.IDLeft(check));
		checkUpdate(grid.IDRight(check));
		checkUpdate(grid.IDAbove(grid.IDRight(check)));
		checkUpdate(grid.IDAbove(grid.IDLeft(check)));
		checkUpdate(grid.IDBelow(grid.IDRight(check)));
		checkUpdate(grid.IDBelow(grid.IDLeft(check)));
	}
	
	private void checkUpdate(int to){
		if(grid.canWalkID(check, to) && !visited[to] && (distance[to]==-1 || distance[to]> possibleDist(to))){
			distance[to] = possibleDist(to);
			if(open.contains(to))
				open.remove();
			open.add(to);
		}
	}
	
	private int possibleDist(int to){
		return distance[check] + (grid.diagonal(check, to)? 14 : 10)*grid.costID(to);
	}

	private class swarmComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer b1, Integer b2) {
			if(distance[b1] == distance[b2])
				return 0;
			else if (distance[b1] < distance[b2])
				return -1;
			else
				return 1;
		}
	}
	
}


