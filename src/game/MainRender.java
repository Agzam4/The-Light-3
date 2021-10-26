package game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import javax.annotation.Generated;

import main.JLightPanel;
import raycasting.LightBlockCasting;

public class MainRender {

	public static int blockSize = JLightPanel.blockSize;
	public static int visibilityRadiusK = 15;
	
	
	
	private static Block[][] blocks;
	private static int mapWidth, mapHeight;
	
	private static final Color[] debug$colors = new Color[] {
			Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, 
			Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, 
			Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, 
//			Color.WHITE, Color.WHITE, Color.WHITE,
//			Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE,
//			Color.RED, Color.GREEN, Color.BLUE,
			Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.YELLOW, Color.PINK
	};
	
	enum Blocks {
		AIR (Color.BLACK, true),
		WALL (Color.BLACK, false),
		WHITE (Color.WHITE, true),
		RED (Color.RED, true),
		GREEN (Color.GREEN, true),
		BLUE (Color.BLUE, true)
		;
		
		Block block;
		private Blocks(Color c, boolean b) {
			block = new Block(c);
			block.setLightTransmitting(b);
		}
		
		public Block getBlock() {
			return block;
		}
	}
	
	public static void createMap(int w, int h) {
		mapWidth = w;
		mapHeight = h;
		blocks = new Block[w][h];
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				blocks[x][y] = Blocks.AIR.getBlock();//debug$colors[(int) (Math.random()*debug$colors.length)]);
				blocks[x][y].setLightTransmitting(true); // Math.random() > 0.2
			}
		}
		
		blocks[(int) (Math.random()*mapWidth)][(int) (Math.random()*mapHeight)] = new Block(Color.RED);
		blocks[(int) (Math.random()*mapWidth)][(int) (Math.random()*mapHeight)] = new Block(Color.BLUE);
		blocks[(int) (Math.random()*mapWidth)][(int) (Math.random()*mapHeight)] = new Block(Color.GREEN);
//		blocks[2][2] = new Block(Color.BLUE);
//		blocks[2][2].setLightTransmitting(true);
		
		blockCasting = new LightBlockCasting(blockSize);
		blockCasting.setVisibilityRadius((int) (blockSize*visibilityRadiusK));
	}

	private static LightBlockCasting blockCasting;
	
	public static void draw(Graphics2D g, JLightPanel lp) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, lp.getGameWidth(), lp.getGameHeight());

//		System.out.println("NEW");
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				
				if(blocks[x][y].isLightTransmitting()) {
					if(blocks[x][y].getLightPower() > 0) {
					blockCasting.draw(g, new Point(x*blockSize+blockSize/2, y*blockSize+blockSize/2),
							new Dimension(lp.getGameWidth(), lp.getGameHeight()),
							blocks[x][y].getLightColor());
					g.setComposite(AlphaComposite.getInstance(3));
					g.setColor(blocks[x][y].getLightColor());
					} else {
						// Air
					}
				} else {
					g.setColor(Color.WHITE);
					g.drawRect(x*blockSize, y*blockSize, blockSize, blockSize);
				}
			}
		}
		
		g.setComposite(AlphaComposite.getInstance(3));
		

		g.fillRect(x*blockSize, y*blockSize, blockSize, blockSize);
		g.setColor(new Color(0,0,0));
		g.fillRect(x*blockSize +blockSize/4,
				y*blockSize+blockSize/4, blockSize/2 + 1, blockSize/2 + 1);
		
		Color lc = getSelectedBlock().getLightColor();
		if(lc.getRGB() == Color.BLACK.getRGB() && getSelectedBlock().isLightTransmitting()) {
			g.setColor(new Color(255,255,255,50));
			g.fillRect((int) (mx*blockSize)+2, (int) (my*blockSize)+2, blockSize-3, blockSize-3);
			g.drawRect((int) (mx*blockSize), (int) (my*blockSize), blockSize, blockSize);
		}else {
			g.setColor(new Color(lc.getRed(), lc.getGreen(), lc.getBlue(), 100));
			g.fillRect((int) (mx*blockSize), (int) (my*blockSize), blockSize+1, blockSize+1);
			if(!getSelectedBlock().isLightTransmitting()) {
				g.setColor(new Color(255,255,255,100));
				g.drawRect((int) (mx*blockSize), (int) (my*blockSize), blockSize, blockSize);
			}else {
				g.setColor(lc);
				g.drawRect((int) (mx*blockSize), (int) (my*blockSize), blockSize, blockSize);
			}
		}
//		g.setColor(Color.WHITE);
//		g.drawString(Blocks.values()[type].name(), (int) (mx*blockSize)+2, (int) (my*blockSize)+2);

		
		
		

//		blockCasting.draw(g, new Point(0,0),
//				new Dimension(lp.getGameWidth(), lp.getGameHeight()),
//				Color.GREEN);
//		
//		blockCasting.draw(g, new Point(lp.getGameWidth()/2,0),
//				new Dimension(lp.getGameWidth(), lp.getGameHeight()),
//				Color.BLUE);
	}
	
	static int type;

	public static void update() {
		blockCasting.updateSegments(blocks, mapWidth, mapHeight);
		if(isMousePressed) {
			try {
				blocks[mx][my] = getSelectedBlock();
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}
	}
	
	
	private static Block getSelectedBlock() {
		return Blocks.values()[type].getBlock();
	}
	
	private static int mx, my;

	private static boolean isMousePressed;
	
	public static void setMousePoint(double x, double y) {
		mx = (int) (x/blockSize);
		my = (int) (y/blockSize);
	}
	
	public static void setMousePressed(boolean b) {
		isMousePressed = b;
	}

	public static void nextType() {
		type++;
		type = type%Blocks.values().length;
	}
	
	public static void lastType() {
		type--;
		if(type < 0) {
			type = Blocks.values().length-1;
		}
	}
	
	private static final int NORTH = 0;
	private static final int SOUTH = 1;
	private static final int EAST = 2;
	private static final int WEST = 3;

	private static int random_int(int a, int b) {
		return (int) (Math.random()*(b-a)) + a;
	}
}
