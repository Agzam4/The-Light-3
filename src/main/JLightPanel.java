package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import game.MainRender;

public class JLightPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final long sleep = 50L;

	public static int gameh = 25, gamew = 25;
	public static int blockSize = 25;
	
	public static long debug$lags;
	
	private BufferedImage game;
	
	/**
	 * Focusable JPanel
	 */
	public JLightPanel() {
		setFocusable(true);
		setBackground(Color.BLACK);
		setBorder(new LineBorder(Color.WHITE));
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
//				MainRender.setMousePoint(
//						(e.getX()/blockSize-getGameX())*k, (e.getY()/blockSize-getGameY())*k);

				MainRender.setMousePoint(
						(e.getX()-getGameX())/k,
						(e.getY()-getGameY())/k
						);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				MainRender.setMousePressed(true);
				MainRender.setMousePoint(
						(e.getX()-getGameX())/k,
						(e.getY()-getGameY())/k
						);
			}
		});
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				MainRender.setMousePressed(false);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				MainRender.setMousePressed(true);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				MainRender.setMousePressed(true);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				MainRender.setMousePressed(false);
				if(e.getWheelRotation() > 0) {
					MainRender.nextType();
				}else {
					MainRender.lastType();
				}
			}
		});
	}
	
	public void run() {
		MainRender.createMap(gamew, gameh);
		createImage(gamew, gameh, blockSize);
		Thread t = new Thread(() -> {
			long start;
			long wait;
			while (true) {
				start = System.nanoTime();
				update();
				draw();
				wait = sleep - (System.nanoTime() - start)/1_000_000;
				debug$lags = -wait;
				if(wait < 0) {
					wait = 0;
				}
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
				}
			}
		});
		t.start();
		
	}
	
	public void createImage(int w, int h, int bsize) {
		gamew = w;
		gameh = h;
		blockSize = bsize;
		game = new BufferedImage(getGameWidth(), getGameHeight(), BufferedImage.TYPE_INT_RGB);
	}
	
	static double k;

	public void draw() {
		int fw = getWidth();
		int fh = getHeight();
		k = (double)fh / (double)gameh;
		if(k > fw/(double)gamew) {
			k = fw/(double)gamew;
		}
		
		k /= blockSize;
		
		// Draw "Game" (drawing on "game" image)
		Graphics2D g = (Graphics2D) game.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setComposite(AlphaComposite.getInstance(3));
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, gamew, gameh);
		MainRender.draw(g, this);
		g.setComposite(AlphaComposite.getInstance(3));
		g.dispose();

		Graphics2D tg = (Graphics2D) getGraphics(); // this (JPanel) Graphics
		tg.drawImage(game, getGameX(), getGameY(), 
				(int) (getFrameGameWidth()), (int) (getFrameGameHeight()), null);
		
		tg.setColor(Color.WHITE);
		tg.drawRect(getGameX(), getGameY(), 
				(int) (getFrameGameWidth())-1, (int) (getFrameGameHeight())-1);
//		tg.setColor(Color.RED);
//		tg.drawString("" + debug$lags, 5, 12);
		tg.dispose();
	}
	
	public void update() {
		MainRender.update();
	}
	

	private int getGameX() {
		return (int) ((getWidth()-getFrameGameWidth())/2);
	}

	private int getGameY() {
		return (int) ((getHeight()-getFrameGameHeight())/2);
	}

	public int getGameHeight() {
		return blockSize*gameh;
	}
	
	public int getGameWidth() {
		return blockSize*gamew;
	}
	
	public int getFrameGameHeight() {
		return (int) (blockSize*gameh*k);
	}
	
	public int getFrameGameWidth() {
		return (int) (blockSize*gamew*k);
	}
	
	public static int getBlockSize() {
		return blockSize;
	}
}
