import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;

public class Path {
	LinkedList<Point> steps = new LinkedList<Point>();
	GeneralPath generalPath = null;

	public void add(Point a) {
		steps.add(a);
	}

	public GeneralPath getGeneralPath(){
		if(generalPath==null)
			genGPath();
		
		return generalPath;
	}
	
	private void genGPath(){
		generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD, steps.size());
		generalPath.moveTo(steps.get(0).x, steps.get(0).y);
		for(int i=0;i<steps.size();++i)
			generalPath.lineTo(steps.get(i).x,steps.get(i).y);
	}
	

}
