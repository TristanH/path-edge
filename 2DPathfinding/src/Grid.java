import java.awt.Point;
import java.io.*;
import java.net.URL;
import java.util.HashMap;

/** A representation of a 2D grid, which can be used in many applications and have attached PathEdge operations applied to it. */
public class Grid {

	/*
	 * Grids have 2 representations: 1. The straightforward 2D representations
	 * with x and y coordinates 2. A 1D grid of block IDs, each corresponding to
	 * a certain spot on the grid, used to make algorithms faster/easier. The 1D 
	 * representation is much more complicated and is not available to the public.
	 */

	private int[][] blockAt;
	private int[][] cost2D; // a cost profile in 2D ie cost2D[0][0] = 5 means block
					// (0,0) takes 5 time steps to traverse
	protected int[] costID; // a cost profile in 1D
	private HashMap<Integer, Integer> costMap; // maps block values to their cost to
										// traverse
	private int numDiffBlocks; // the number of different types of blocks
	private int xBlocks, yBlocks;

	/** Construct a default 30 by 30 grid */
	public Grid() {
		this(30, 30);
	}

	/** Constructs a grid of the given dimensions and blocks of only ID 0, meaning a cost of 1 to traverse.
	 * 
	 * @param xBlocks The x dimension of the grid.
	 * @param yBlocks The y dimension of the grid.
	 */
	public Grid(int xBlocks, int yBlocks) {
		this(new int[xBlocks][yBlocks], 4, makeDefaultCostMap());
	}

	/** Constructs a grid from a file given in fileName and the dimensions given. This uses the default cost values:
	 *  	A block of value 0 has a cost of 1 to traverse.
	 *  	A block of value 1 cannot be traversed.
	 *  	A block of value 2 has a cost of 3 to traverse.
	 *  	A block of value 3 has a cost of 10 to traverse.
	 *  
	 *  @param fileName The file name of the map file.
	 *  @param xBlocks The x dimension of the grid.
	 *  @param yBlocks The y dimension of the grid.
	 *  @throws FileNotFoundException 
	 */
	public Grid(String fileName, int xBlocks, int yBlocks) throws FileNotFoundException {
		this(parseGridFromFile(new BufferedReader(new FileReader(fileName)), xBlocks, yBlocks), 4,
				makeDefaultCostMap());
	}
	
	/** Generates a file from, the given url, very useful for working with JARs
	 * 
	 * @param url The url of the map file.
	 * @param xBlocks The x dimension of the given map.
	 * @param yBlocks The y dimension of the given map.
	 * @throws IOException 
	 */
	public Grid(URL url , int xBlocks, int yBlocks) throws IOException{
		this(parseGridFromFile(new BufferedReader(new InputStreamReader(url.openStream())),
				xBlocks, yBlocks), 
			4, makeDefaultCostMap());
	}

	/** Constructs a grid given the base components needed.
	 * 
	 * @param blockAt A 2D array of integers, representing block IDs.
	 * @param numDiffBlocks The number of different block IDs
	 * @param costMap A HashMap mapping block IDs to their cost of traversing.
	 */
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

	private static int[][] parseGridFromFile(BufferedReader br, int xBlocks,
			int yBlocks) {

		int[][] tempGrid = new int[xBlocks][yBlocks];
		
		
		try {
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
	

	protected int pointToID(Point at) {
		return at.x + xBlocks * at.y;
	}

	protected Point idToPoint(int idAt) {
		return new Point(idAt % xBlocks, idAt / xBlocks);
	}

	protected int IDAbove(int idAt) {
		return idAt - xBlocks;
	}

	protected int IDBelow(int idAt) {
		return idAt + xBlocks;
	}

	protected int IDRight(int idAt) {
		return idAt + 1;
	}

	protected int IDLeft(int idAt) {
		return idAt - 1;
	}

	protected int[] getCostMapID() {
		return costID;
	}

	public int getTotalBlocks() {
		return xBlocks * yBlocks;
	}

	protected boolean walkableID(int idAt) {
		return idAt > 0 && idAt < xBlocks * yBlocks && costID[idAt] != -1;
	}

	protected boolean canWalkID(int current, int to) {// determines if a one step jump can be made from one block to another.
											     // Prevents out of bounds cases.
		if (current % xBlocks == 0 && (to == IDLeft(current) || to == IDLeft(IDAbove(current)) || to == IDLeft(IDBelow(current))))
			return false;
		else if(current % xBlocks == xBlocks-1 && (to == IDRight(current) || to== IDRight(IDAbove(current)) || to == IDRight(IDBelow(current)) ))
			return false;
		else
			return walkableID(to);
	}

	protected boolean diagonal(int check1, int check2) {
		return IDRight(IDAbove(check1)) == check2
				|| IDRight(IDBelow(check1)) == check2
				|| IDLeft(IDAbove(check1)) == check2
				|| IDLeft(IDBelow(check1)) == check2;
	}
	
	/** 
	 * 
	 * @return The x dimension of the grid.
	 */
	public int getXBlocks(){
		return xBlocks;
	}
	
	/**
	 * 
	 * @return The y dimension of the grid.
	 */
	public int getYBlocks(){
		return yBlocks;
	}
	
	/** 
	 * 
	 * @param x The x-coordinate being searched for.
	 * @param y The y-coordinate being searched for.
	 * @return The ID of the grid spot at the coordinate (x,y).
	 */
	public int whatBlock(int x, int y){
		return blockAt[x][y];
	}
	
	protected int costID(int id){
		return costID[id];
	}
}
