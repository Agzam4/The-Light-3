package raycasting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

import game.Block;
import game.MainRender;


public class LightBlockCasting {
	
	private static final Color BG = Color.BLACK;
	private ArrayList<Segment> segments;
	private int visibilityRadius = 100;
	int blockSize;
	
	public LightBlockCasting(int blockSize) {
		this.blockSize = blockSize;
		segments = new ArrayList<Segment>();
	}
	
	private void add2XSegment(int len, int x, int y) {
		int x1 = (x-len)*MainRender.blockSize;
		int x2 = (x)*MainRender.blockSize;
		int y1 = (y)*MainRender.blockSize;
		int y2 = (y+1)*MainRender.blockSize;
		segments.add(new Segment(x1, y1, x2, y1));
		segments.add(new Segment(x1, y2, x2, y2));
	}	
	
	private void add2YSegment(int len, int y, int x) {
		int y1 = (y-len)*MainRender.blockSize;
		int y2 = (y)*MainRender.blockSize;
		int x1 = (x)*MainRender.blockSize;
		int x2 = (x+1)*MainRender.blockSize;
		segments.add(new Segment(x1, y1, x1, y2));
		segments.add(new Segment(x2, y1, x2, y2));
	}
	
	public void updateSegments(Block[][] blocks, int w, int h) {
		segments.clear();
		
		//*

		
		/*
		 * TODO
		 * TODO
		 * TODO
		 * TODO
		 * TODO
		 * TODO
		 * TODO
		 * TODO
		 * TODO
		 * TODO
		 * TODO
		 * TODO
		 * 
		 * 
		 */

		// Creating Segments on X:

		for (int y = 0; y < h; y++) {
			int len = 0;
			for (int x = 0; x < w; x++) {
				if(blocks[x][y].isLightTransmitting()) {
					if(len != 0) {
						add2XSegment(len, x, y);
					}
					len = 0;
				}else {
					len++;
				}
			}
			add2XSegment(len, w, y);
		}


		// Creating Segments on Y:

		for (int x = 0; x < w; x++) {
			int len = 0;
			for (int y = 0; y < h; y++) {
				if(blocks[x][y].isLightTransmitting()) {
					if(len != 0) {
						add2YSegment(len, y, x);
					}
					len = 0;
				}else {
					len++;
				}
			}
			add2YSegment(len, h, x);
		}
	}

	public void draw(Graphics2D g, Point p, Dimension d, Color light) {
		draw(g, p, d, light, visibilityRadius);
	}
	
	public void draw(Graphics2D g, Point p, Dimension d, Color light, int visibilityRadius) {
		BufferedImage newImg = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D ng = (Graphics2D) newImg.getGraphics();
		ng.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		draw(ng, p, light, d, visibilityRadius);
		ng.dispose();
		g.setComposite(LightComposite.INSTANCE);
		g.drawImage(newImg, 0, 0, null);
	}
	
	private void draw(Graphics2D g, Point p, Color light,  Dimension d, int visibilityRadius) {
		g.setStroke(new BasicStroke(2));
		
		g.setPaint(new RadialGradientPaint(
						new Point(p.x, p.y),
						visibilityRadius,
					    new float[] {0.0f, 1f},
					    new Color[] {light, new Color(0,0,0)}));
		g.fillOval(p.x - visibilityRadius, 
				p.y-visibilityRadius, visibilityRadius*2, visibilityRadius*2);
		
		Ellipse2D circle = new Ellipse2D.Double(p.x - visibilityRadius, p.y-visibilityRadius, visibilityRadius*2, visibilityRadius*2); 
		Shape oldClip = g.getClip(); 
		
		for (int i = 0; i < segments.size(); i++) { // TODO: ditanse to segments

			if(segments.get(i).minDistance(p) > visibilityRadius) {
				continue;
			}
//			g.setColor(Color.RED);
//			g.drawLine(segments.get(i).x, segments.get(i).y, segments.get(i).x2, segments.get(i).y2);

			g.setColor(new Color(0,0,0, 100));
//			Point pp1 = getIntersectionPoint(p, segments[i].getX(), segments[i].getY(), segments);
			g.setColor(new Color(0,0,0, 100));
//			Point pp2 = getIntersectionPoint(p, segments[i].getX2(), segments[i].getY2(), segments);
			
			Segment ray = null, ray2 = null;

			ray2 = createRay(p, segments.get(i).getPoint2(),
					(int) Math.hypot(p.x-segments.get(i).x2,p.y-segments.get(i).y2));
			ray = createRay(p, segments.get(i).getPoint1(),
					(int) Math.hypot(p.x-segments.get(i).x,p.y-segments.get(i).y));
			
			if(ray != null && ray2 != null) {
				Polygon polygon = new Polygon(
						new int[] {ray.x, ray.x2, ray2.x2, ray2.x},
						new int[] {ray.y, ray.y2, ray2.y2, ray2.y}, 4);
				g.setClip(polygon);
				g.setColor(Color.BLACK);
				g.clip(circle);
				g.clip(polygon);
				g.setColor(BG);
				g.fill(circle);
				g.setClip(oldClip);				
			}
		}
		
		//*/
	}
	
	/**
	 * 
	 * @param mp - Mouse Point
	 * @param fp - "Red" Point
	 * @param md - Distance to Mouse
	 */
	public static Segment createRay(Point mp, Point fp, int md) {
		double dir = Math.atan2(mp.x - fp.x, mp.y - fp.y) + Math.toRadians(180);
		Segment ray2 = new Segment(fp.x, fp.y, Integer.MAX_VALUE /*visibilityRadius-md*/, dir);
		return ray2;
	}
	
	public static Point isIntersection(Point p, int x, int y, Segment[] segments) {
		Segment ray = new Segment(p.x, p.y, x, y);
		ArrayList<Point> intersectionPoint = new ArrayList<Point>();
		for (int j = 0; j < segments.length; j++) {
			if(ray.isIntersect(segments[j])) {
//				intersectionPoint.add(pp); FIXME
			}
		}
		intersectionPoint.sort(new Comparator<Point>() {
			
			@Override
			public int compare(Point p1, Point p2) {
				double hpot1 = Math.hypot(p.x - p1.x, p.y - p1.y);
				double hpot2 = Math.hypot(p.x - p2.x, p.y - p2.y);
				return (int) (hpot1 - hpot2);
			}
		});
		
		if(intersectionPoint.size() > 0) {
			Point pp = intersectionPoint.get(0);
			return pp;
		}
		return null;
	}

	public void drawVisibleArea() {

		
	}
	
	
//	public static  Point getIntersectionPoint(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
//		int v2 = ((y1-y2)*(x4-x3)-(y3-y4)*(x2-x1));
//		int v4 = (x4-x3);
//		if(v2 == 0 || v4 == 0) return null;
//		int x = ((x1*y2-x2*y1)*(x4-x3)-(x3*y4-x4*y3)*(x2-x1))/v2;
//		int y = ((y3-y4)*x-(x3*y4-x4*y3))/v4;
//		return new Point(x, y);
//	}
	
	public static  Point getIntersectionPoint(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
		int v1 = (x4-x3)*(y1-y3)-(y4-y3)*(x1-x3);
		int v2 = (x4-x3)*(y2-y3)-(y4-y3)*(x2-x3);
		int v3 = (x2-x1)*(y3-y1)-(y2-y1)*(x3-x1);
		int v4 = (x2-x1)*(y4-y1)-(y2-y1)*(x4-x1);
		return new Point(v1*v2, v3*v4);
	}

	public static boolean isIntersect(Point C, Point D, Point A,  Point B, Integer smallestR) {
		double denominator = (D.x - C.x) * (B.y - A.y) - (B.x - A.x) * (D.y - C.y);
		double r = ((B.x - A.x) * (C.y - A.y) - (C.x - A.x) * (B.y - A.y)) / denominator;
		if(r < 0) return false;
		if(smallestR != null && smallestR < r) return false;
		double s = ((A.x - C.x) * (D.y - C.y) - (D.x - C.x) * (A.y - C.y)) / denominator;
		if(s < 0 || s > 1) return false;
		return true;
	}
	
	
	
	public void setVisibilityRadius(int visibilityRadius) {
		this.visibilityRadius = visibilityRadius;
	}
}
