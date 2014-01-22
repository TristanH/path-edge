package tools;
import java.awt.Point;

/** An implementation of the Floyd-Warshall Algorithm, which will find the shortest distance from every point to every other point on the grid */
public class WarshallPathFinder {
	private Grid grid;
	private int[][] dist;
	
	/** Creates the pathfinding object.
	 * 
	 * @param gridIn The grid to have its paths found on.
	 */
	public WarshallPathFinder(Grid gridIn){
		grid = gridIn;
		dist = new int[grid.getXBlocks()*grid.getYBlocks()][grid.getXBlocks()*grid.getYBlocks()]; //we use block IDs for this to simplify the array
		//dist[a][b] is the distance from block a to block b
	}
	
	//Warshall's cannot be used effectively to find specific paths
	//Rather, it effectively finds DISTANCE from every node to every other node
	//Thus, Warshall's version of findPath is findDistances, which returns a 2D array of distances to every node
	//This information can be incredibly useful.

	/** This will find the DISTANCE, not the path, from every spot on the grid to every spot. This information can be incredibly useful. Keep in mind this operation does take a long time, specifically O(n^3) where n is the number of spots on the grid. Thus, it is not recommended for very large grids. 
	 * 
	 * @return a 2D array of distances to every spot, from every spot.
	 */
	public int[][] findDistances(){
		
		//initialize the inital distance array so that Warshall's will work with it
		for(int i=0;i<grid.getTotalBlocks();++i)
			for(int j=0;j<grid.getTotalBlocks();++j)
				if (grid.costID[j]==-1)
					dist[i][j]=-2;
				else if(grid.IDAbove(i)==j || grid.IDBelow(i)==j || grid.IDLeft(i)==j || grid.IDRight(i)==j)
					dist[i][j]=grid.costID[j]*10;
				else if(grid.IDAbove(grid.IDRight(i))==j || grid.IDAbove(grid.IDLeft(i))==j
						|| grid.IDBelow(grid.IDRight(i))==j || grid.IDBelow(grid.IDLeft(i))==j)
					dist[i][j]=grid.costID[j]*14;
				else if(i==j)
					dist[i][j] = 0;
				else
					dist[i][j]=-1;//for when we have no path yet
		
		
		//The actual algorithm, checking intermediate blocks
		for(int k=0;k<grid.getTotalBlocks();++k){
			for(int i=0;i<grid.getTotalBlocks();++i){
				for(int j=0;j<grid.getTotalBlocks();++j){
					if( dist[i][k] >-1 && dist[k][j] >-1 && (dist[i][k] + dist[k][j] < dist[i][j] || dist[i][j]==-1))
						dist[i][j] = dist[i][k] + dist[k][j];
				}
			}
		}
		
		for(int i=0;i<grid.getTotalBlocks();++i){
			System.out.print(dist[0][i]+" ");
		}
		return dist;
		
	}
}
