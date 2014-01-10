import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;

/** A simple object which records steps taken across a grid */
public class Path {
	private LinkedList<Point> steps = new LinkedList<Point>();
	private GeneralPath generalPath = null;

	/** Adds on a Point to the end of the Path.
	 * 
	 * @param a The Point to be added.
	 */
	public void add(Point a) {
		steps.add(a);
	}

	/** Returns the Path in the form of a GeneralPath, very useful for drawing or transformations.
	 * 
	 * @return The Path in the form of a GeneralPath.
	 */
	public GeneralPath getGeneralPath(){
		if(generalPath==null)
			genGPath();
		
		return generalPath;
	}
	
	/** 
	 * 
	 * @return The first point in the Path.
	 */
	public Point getStart(){
		return steps.get(0);
	}
	
	/**
	 * 
	 * @return The last point in the Path.
	 */
	public Point getEnd(){
		return steps.get(steps.size()-1);
	}
	
	
	private void genGPath(){
		generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD, steps.size());
		generalPath.moveTo(steps.get(0).x, steps.get(0).y);
		for(int i=0;i<steps.size();++i)
			generalPath.lineTo(steps.get(i).x,steps.get(i).y);
	}
	

}
