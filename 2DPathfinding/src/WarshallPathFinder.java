import java.awt.Point;


public class WarshallPathFinder {
	private Grid grid;
	private int[][] dist;
	
	public WarshallPathFinder(Grid gridIn){
		grid = gridIn;
		dist = new int[grid.xBlocks*grid.yBlocks][grid.xBlocks*grid.yBlocks]; //we use block IDs for this to simplify the array
		//dist[a][b] is the distance from block a to block b
	}
	
	//Warshall's cannot be used effectively to find specific paths
	//Rather, it effectively finds DISTANCE from every node to every other node
	//Thus, Warshall's version of findPath is findDistances, which returns a 2D array of distances to every node
	//This information can be incredibly useful.
	public int[][] findDistances(){
		
		//initialize the inital distance array so that Warshall's will work with it
		for(int i=0;i<grid.getTotalBlocks();++i)
			for(int j=0;j<grid.getTotalBlocks();++j)
				if (grid.costID[j]==-1)
					dist[i][j]=-1;
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
					if( dist[i][k] !=-1 && dist[k][j] !=-1 && (dist[i][k] + dist[k][j] < dist[i][j] || dist[i][j]==-1))
						dist[i][j] = dist[i][k] + dist[k][j];
				}
			}
		}
		
		return dist;
		
	}
}
