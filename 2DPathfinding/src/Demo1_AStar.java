import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class Demo1_AStar extends JPanel implements MouseListener, KeyListener {

	boolean rmouseDown = false, lmouseDown = false;
	HashSet<Integer> keysDown = new HashSet<Integer>();
	boolean isRunning = true;
	Grid grid; 
	int xBlocks, yBlocks;
	Rectangle2D.Double gridBox = new Rectangle2D.Double(0,0,600,600);
	AStarPathFinder pfer; 
	Path currentPath;
	
	public Demo1_AStar() {
		addMouseListener(this);
		addKeyListener(this);
		this.setFocusable(true);
		
		try { //we use a resource and not a text file specifically here because this program will be run as a JAR.
			grid = new Grid(getClass().getResource("SampleGrid.txt"), 30, 30);
		} 
		catch (IOException e) { e.printStackTrace();}
		
		xBlocks = grid.getXBlocks();
		yBlocks = grid.getYBlocks();
		pfer = new AStarPathFinder(grid);
		currentPath = pfer.findPath(new Point(0,0), new Point(25,10));
	
		Thread t = new Thread(new Runnable() {
			public void run() {
				demoLoop();
			}
		});
		t.start();

	}
	
	public void demoLoop(){
		while(isRunning){
			updateInput();
			updateGrid();
			repaint();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void updateInput(){
		if(rmouseDown || lmouseDown){
			Point mAt = MouseInfo.getPointerInfo().getLocation();
			mAt.translate(-this.getLocationOnScreen().x,-this.getLocationOnScreen().y);
			if(currentPath == null){
				currentPath = pfer.findPath(new Point(0,0), mouseToGridPoint(mAt));
				rmouseDown = false; //this covers the case where the previous path did not exist
			}
			else if(gridBox.contains(mAt) && lmouseDown )
				currentPath = pfer.findPath(currentPath.getEnd(), mouseToGridPoint(mAt) );
			else if(gridBox.contains(mAt) && rmouseDown)
				currentPath = pfer.findPath(mouseToGridPoint(mAt), currentPath.getStart());
			
		}
	}
	
	private Point mouseToGridPoint(Point mAt){
		return new Point((int)(1.0*mAt.x*xBlocks/gridBox.width),(int)(1.0*mAt.y*yBlocks/gridBox.height));
	}
	
	public void updateGrid(){
		;
	}
	
	private void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		drawGrid(g2);
		drawPath(g2);
		drawSidebar(g2);
	}
	
	private void drawGrid(Graphics2D g2){
		int x = (int) gridBox.x, y=(int) gridBox.y, width=(int) gridBox.width, height=(int) gridBox.height;
		g2.setColor(Color.black);
		
		for(int i=x;i<=x+width; i+=width/(xBlocks)){
			g2.drawLine(i, y, i, y+height);
		}
		for(int i=y;i<=y+height; i+=height/(yBlocks)){
			g2.drawLine(x, i, x+width, i);
		}
		
		for(int i=0;i<xBlocks;++i){
			for(int j=0;j<yBlocks;++j){
				if(grid.whatBlock(i,j)==1){
					g2.setColor(Color.BLACK);
					drawBlock(g2,x,y,width,height,i,j);
				}
				else if(grid.whatBlock(i,j)==2){
					g2.setColor(new Color(102,102,0));
					drawBlock(g2,x,y,width,height,i,j);
				}
				else if(grid.whatBlock(i,j)==3){
					g2.setColor(new Color(153,102,0));
					drawBlock(g2,x,y,width,height,i,j);
				}
				
				
			}
		}
		
	}
	
	private void drawBlock(Graphics2D g2, int x, int y, int width, int height, int xAt, int yAt){
		g2.fillRect(x+xAt*(width/xBlocks)+1, y+ yAt*(height/yBlocks)+1, width/xBlocks -1, height/yBlocks -1);
	}

	private void drawPath(Graphics2D g2){
		int x = (int) gridBox.x, y=(int) gridBox.y, width=(int) gridBox.width, height=(int) gridBox.height;

		if(currentPath==null)
			return;
		
		g2.setColor(new Color(0,0,1,0.5f));
		g2.setStroke(new BasicStroke(15,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		GeneralPath gp = new GeneralPath(currentPath.getGeneralPath());
		gp.transform(new AffineTransform((width/xBlocks),0,0,(width/yBlocks),(width/xBlocks/2),(width/yBlocks/2)));
		g2.draw(gp);
		
		//draw the path given by currentPath
	//	for(int i=0;i<currentPath.steps.size();++i)
	//		drawBlock(g2,x,y,width,height,currentPath.steps.get(i).x,currentPath.steps.get(i).y);
		
	}
	
	public void drawSidebar(Graphics2D g2){
		
		g2.setColor(Color.black);
		g2.setFont(new Font("SansSerif",Font.BOLD,22));
		drawString(g2, "Welcome to Demo 1\n"
				+ "      Of PathEdge!" , 620, 20);
		
		g2.setFont(new Font("Dialog",Font.PLAIN, 18));
		drawString(g2, "In this demo, right click on a \n"
				     + "spot to set the beginning\n"
				     + "location and left click \n"
				     + "on a spot to set the end of\n"
				     + "the path.\n\n"
				     + "The demo will find the\n"
				     + "shortest path between\n"
				     + "the points as well \n"
				     + "as the time taken to find it.", 620, 110);
		
		drawString(g2, "Time taken: "+pfer.getLastTimeTaken()+"ms", 620,500);
	}
	
	private void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		keysDown.add(e.getKeyCode());
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysDown.remove(e.getKeyCode());
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e))
			lmouseDown = true;
		else if(SwingUtilities.isRightMouseButton(e))
			rmouseDown = true;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		rmouseDown = false;
		lmouseDown = false;
	}

}
