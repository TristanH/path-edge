import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JPanel;


public class Demo extends JPanel implements MouseListener, KeyListener {

	boolean mouseDown = false;
	HashSet<Integer> keysDown = new HashSet<Integer>();
	boolean isRunning = true;
	Grid grid = new Grid("SampleGrid.txt",30,30);
	Rectangle2D.Double gridBox = new Rectangle2D.Double(0,0,600,600);
	AStarPathFinder pfer = new AStarPathFinder(grid);
	Path currentPath;
	
	public Demo() {
		addMouseListener(this);
		addKeyListener(this);
		this.setFocusable(true);
		//for(int i=0;i<10000;++i)
			currentPath = pfer.findPath(new Point(0,0), new Point(25,10));
		//currentPath = pfer.findPath(new Point((int)(Math.random()*30),(int)(Math.random()*30)), new Point((int)(Math.random()*30), (int)(Math.random()*30)));
		
		WarshallPathFinder warsh = new WarshallPathFinder(grid);
		int[][] dists = warsh.findDistances();
		

				
			
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
		if(mouseDown){
			Point mAt = MouseInfo.getPointerInfo().getLocation();
			mAt.translate(-this.getLocationOnScreen().x,-this.getLocationOnScreen().y);
			if(gridBox.contains(mAt))
				currentPath = pfer.findPath(new Point(0,0), new Point((int)(1.0*mAt.x*grid.xBlocks/gridBox.width),(int)(1.0*mAt.y*grid.yBlocks/gridBox.height)));
			
		}
	}
	
	public void updateGrid(){
		;
	}
	
	private void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		drawGrid(g2);
		drawPath(g2);
		drawSidebar(g2);
	}
	
	private void drawGrid(Graphics2D g2){
		int x = (int) gridBox.x, y=(int) gridBox.y, width=(int) gridBox.width, height=(int) gridBox.height;
		g2.setColor(Color.black);
		
		for(int i=x;i<=x+width; i+=width/(grid.xBlocks)){
			g2.drawLine(i, y, i, y+height);
		}
		for(int i=y;i<=y+height; i+=height/(grid.yBlocks)){
			g2.drawLine(x, i, x+width, i);
		}
		
		for(int i=0;i<grid.xBlocks;++i){
			for(int j=0;j<grid.yBlocks;++j){
				if(grid.blockAt[i][j]==1)
					drawBlock(g2,x,y,width,height,i,j);
			}
		}
		
	}
	
	private void drawBlock(Graphics2D g2, int x, int y, int width, int height, int xAt, int yAt){
		g2.fillRect(x+xAt*(width/grid.xBlocks)+1, y+ yAt*(height/grid.yBlocks)+1, width/grid.xBlocks -1, height/grid.yBlocks -1);
	}

	private void drawPath(Graphics2D g2){
		int x = (int) gridBox.x, y=(int) gridBox.y, width=(int) gridBox.width, height=(int) gridBox.height;

		if(currentPath==null)
			return;
		
		g2.setColor(new Color(0,0,1,0.5f));
		g2.setStroke(new BasicStroke(15,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		GeneralPath gp = new GeneralPath(currentPath.getGeneralPath());
		gp.transform(new AffineTransform((width/grid.xBlocks),0,0,(width/grid.yBlocks),(width/grid.xBlocks/2),(width/grid.yBlocks/2)));
		g2.draw(gp);
		
		//draw the path given by currentPath
	//	for(int i=0;i<currentPath.steps.size();++i)
	//		drawBlock(g2,x,y,width,height,currentPath.steps.get(i).x,currentPath.steps.get(i).y);
		
	}
	
	public void drawSidebar(Graphics2D g2){
		
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
		mouseDown = true;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseDown = false;
	}

}
