import java.awt.Point;


public class DijkstraPathFinder {
	//Dijkstra's algorithm is essentially A*, but with no heuristic
	
	private AStarPathFinder pfer;
	
	public DijkstraPathFinder(Grid grid){
		pfer = new AStarPathFinder(grid, AStarPathFinder.HEURISTIC_NONE);
		
	}
	
	public Path findPath(Point start, Point end){
		return pfer.findPath(start, end);
	}
}
