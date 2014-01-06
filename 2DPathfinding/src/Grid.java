import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Grid {

	/*
	 * Grids have 2 representations: 1. The straightforward 2D representations
	 * with x and y coordinates 2. A 1D grid of block IDs, each corresponding to
	 * a certain spot on the grid, used to make algorithms faster/easier
	 */

	int[][] blockAt;
	int[][] cost2D; // a cost profile in 2D ie cost2D[0][0] = 5 means block
					// (0,0) takes 5 time steps to traverse
	int[] costID; // a cost profile in 1D
	HashMap<Integer, Integer> costMap; // maps block values to their cost to
										// traverse
	int numDiffBlocks; // the number of different types of blocks
	int xBlocks, yBlocks;
	int totalBlocks;

	public Grid() {
		this(30, 30);
	}

	public Grid(int xBlocks, int yBlocks) {
		this(new int[xBlocks][yBlocks], 4, makeDefaultCostMap());
	}

	// constructor for grids from file, uses default costMap
	public Grid(String fileName, int xBlocks, int yBlocks) {
		this(parseGridFromFile(fileName, xBlocks, yBlocks), 4,
				makeDefaultCostMap());
	}

	public Grid(int[][] blockAt, int numDiffBlocks,
			HashMap<Integer, Integer> costMap) {

		this.costMap = costMap;

		xBlocks = blockAt.length;
		yBlocks = blockAt[0].length;
		this.numDiffBlocks = numDiffBlocks;

		this.blockAt = blockAt;
		cost2D = new int[xBlocks][yBlocks];
		costID = new int[xBlocks * yBlocks];

		for (int x = 0; x < xBlocks; ++x) {
			for (int y = 0; y < yBlocks; ++y) {
				cost2D[x][y] = costMap.get(blockAt[x][y]);
				costID[x + y * xBlocks] = costMap.get(blockAt[x][y]);
			}
		}

	}

	private static HashMap<Integer, Integer> makeDefaultCostMap() {
		HashMap<Integer, Integer> defaultCostMap = new HashMap<Integer, Integer>();
		defaultCostMap.put(0, 1); // a block which requires 1 time step to
									// traverse
		defaultCostMap.put(1, -1); // -1 signifies a block which is impossible
									// to traverse
		defaultCostMap.put(2, 3); // a block which requires 3 time steps to
									// traverse
		defaultCostMap.put(3, 10);

		return defaultCostMap;
	}

	private static int[][] parseGridFromFile(String fileName, int xBlocks,
			int yBlocks) {

		int[][] tempGrid = new int[xBlocks][yBlocks];

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String lineAt;

			for (int y = 0; y < yBlocks; ++y) {
				lineAt = br.readLine();
				for (int x = 0; x < xBlocks; ++x) {
					tempGrid[x][y] = Integer.parseInt(lineAt
							.substring(x, x + 1));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return tempGrid;
	}

	public int pointToID(Point at) {
		return at.x + xBlocks * at.y;
	}

	public Point idToPoint(int idAt) {
		return new Point(idAt % xBlocks, idAt / xBlocks);
	}

	public int IDAbove(int idAt) {
		return idAt - xBlocks;
	}

	public int IDBelow(int idAt) {
		return idAt + xBlocks;
	}

	public int IDRight(int idAt) {
		return idAt + 1;
	}

	public int IDLeft(int idAt) {
		return idAt - 1;
	}

	public int[] getCostMapID() {
		return costID;
	}

	public int getTotalBlocks() {
		return xBlocks * yBlocks;
	}

	public boolean walkable(int idAt) {
		return idAt > 0 && idAt < xBlocks * yBlocks && costID[idAt] != -1;
	}

	public boolean canWalk(int current, int to) {// determines if a one step jump can be made from one block to another.
											     // Prevents out of bounds cases.
		if (current % xBlocks == 0 && (to == IDLeft(current) || to == IDLeft(IDAbove(current)) || to == IDLeft(IDBelow(current))))
			return false;
		else if(current % xBlocks == xBlocks-1 && (to == IDRight(current) || to== IDRight(IDAbove(current)) || to == IDRight(IDBelow(current)) ))
			return false;
		else
			return walkable(to);
	}

	public boolean diagonal(int check1, int check2) {
		return IDRight(IDAbove(check1)) == check2
				|| IDRight(IDBelow(check1)) == check2
				|| IDLeft(IDAbove(check1)) == check2
				|| IDLeft(IDBelow(check1)) == check2;
	}
}
