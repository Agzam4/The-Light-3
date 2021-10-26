import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class JMyPanel extends JPanel implements MouseMotionListener {

	private static final long serialVersionUID = 1L;

	private static final int width = 600;
	private static final int height = 300;

	private BufferedImage canvas;
	private Graphics2D g;

	private int mx, my;
	private Point mp;
	
	boolean isCtrlDown;
	
	public JMyPanel() {
		setFocusable(true);
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) canvas.getGraphics();
		addMouseMotionListener(this);
		
		addMouseWheelListener(e -> {
			if(isCtrlDown) {
				float newPower = RayCasting1.visibilityRadius;
				newPower -= e.getWheelRotation()/10f;
				if(newPower < 0.1f) newPower = 0.1f;
				if(newPower > 1f) newPower = 1f;
				RayCasting1.power = newPower;
			}else {
				int newVisibilityRadius = RayCasting1.visibilityRadius;
				newVisibilityRadius -= e.getWheelRotation()*10;
				if(newVisibilityRadius < 10) newVisibilityRadius = 10;
				if(newVisibilityRadius > 600) newVisibilityRadius = 600;
				RayCasting1.visibilityRadius = newVisibilityRadius;
			}
		});
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
					isCtrlDown = false;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
					isCtrlDown = true;
				}
			}
		});
//		RayCasting1.setSegments();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		updateMouseLocation(e);
//		redraw();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		updateMouseLocation(e);
//		redraw();
	}
	
	private void updateMouseLocation(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}
	
	int cosi = 0;
	
	public void go() {
		Thread update = new Thread(() -> {
			while (true) {
				redraw();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		update.start();
	}
	
	public void redraw() {
//		if(g == null || getGraphics() == null)
//			return;
		cosi++;
		double val = Math.cos(cosi/10d);
		g = (Graphics2D) canvas.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		RayCasting1.drawBg(g, new Dimension(width, height));
		
		
		RayCasting1.draw(g, new Point((int) (width/2 + val*100), height/2), new Dimension(width, height), Color.GREEN);
		RayCasting1.draw(g,new Point(width/2 + 10, (int) (height/2+val*75)), new Dimension(width, height), Color.RED);

		RayCasting1.draw(g, new Point(mx, my), new Dimension(width, height), Color.BLUE);

//		RayCasting1.drawOnImage(canvas, new Point(width/2 + 10, (int) (height/2+val*75)), Color.RED);
//		RayCasting1.drawOnImage(canvas, new Point(mx, my), Color.BLUE);
		
		
		g.dispose();
		
		Graphics2D pg = (Graphics2D) getGraphics();
		pg.drawImage(canvas, 0, 0, null);
	}
	
	@Override
	public void repaint() {
//		redraw();
//		super.repaint();
	}
}
