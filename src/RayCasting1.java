import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

import javax.sound.sampled.Line;

public class RayCasting1 {

//	public static final int[][] lineSegments = {
//		{0,0 }, {600,0 },
//	    {600,0 }, {600,300 },
//	    {600,300 }, {0,300 },
//		{0,300 }, {0,0 },
//		{200,50 }, {250,50 },
//		{200,50 }, {200,100 },
//		{400,50 }, {350,50 },
//		{400,50 }, {400,100 },
//		{200,250 }, {250,250 },
//		{200,250 }, {200,200 },
//		{400,250 }, {350,250 },
//		{400,250 }, {400,200 },
//		{100,50 }, {100,250 },
//		{500,50 }, {500,250 }
//	};
	
	private static final double angleOffset = 0.00001;
	public static int visibilityRadius = 100;
	public static float power = 0.1f;
	
	public static Segment[] segments = {
			new Segment(0,0, 600,0),
			new Segment(600,0, 600,300),
			new Segment(600,300, 0,300),
			new Segment(0,300, 0,0),
			new Segment(200,50, 250,50),
			new Segment(200,50, 200,100),
			new Segment(400,50, 350,50),
			new Segment(400,50, 400,100),
			new Segment(200,250, 250,250),
			new Segment(200,250, 200,200),
			new Segment(400,250, 350,250),
			new Segment(400,250, 400,200),
			new Segment(100,50, 100,250),
			new Segment(500,50, 500,250)
			,
			new Segment(200,100, 250,100),
			new Segment(250,50, 250,100),
			
			
	};
	
	
	static final Color BG = Color.BLACK;
	
	
	public static  void drawBg(Graphics2D g, Dimension d) {
		g.setColor(BG);
		g.fillRect(0, 0, (int)d.getWidth(), (int)d.getHeight());
	}
	
    private static class XorComposite implements Composite {

        public static XorComposite INSTANCE = new XorComposite();

        private XorContext context = new XorContext();

        @Override
        public CompositeContext createContext(ColorModel srcColorModel,
                ColorModel dstColorModel, RenderingHints hints) {
            return context;
        }

    }
    
	private static class XorContext implements CompositeContext {

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int w = Math.min(src.getWidth(), dstIn.getWidth());
            int h = Math.min(src.getHeight(), dstIn.getHeight());

            int[] srcRgba = new int[4];
            int[] dstRgba = new int[4];

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    src.getPixel(x, y, srcRgba);
                    dstIn.getPixel(x, y, dstRgba);
                    for (int i = 0; i < 4; i++) {
                        dstRgba[i] = (int) ((dstRgba[i] + srcRgba[i]));
                        if(dstRgba[i] > 255) {
                        	dstRgba[i] = 255;
                        }
                        if(dstRgba[i] < 0) {
                        	dstRgba[i] = 0;
                        }
                    }
                    dstOut.setPixel(x, y, dstRgba);
                }
            }
        }

        @Override
        public void dispose() {
        }

    }

	public static void draw(Graphics2D g, Point p, Dimension d, Color light) {
		BufferedImage newImg = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D ng = (Graphics2D) newImg.getGraphics();
		draw(ng, p, light, d);
		ng.dispose();
		g.setComposite(XorComposite.INSTANCE);
		g.drawImage(newImg, 0, 0, null);
	}
	
//	public static void drawOnImage(BufferedImage image, Point p, Color light) {
//		Graphics2D g = (Graphics2D) image.getGraphics();
//		g.setComposite(XorComposite.INSTANCE);
//		draw(g, p, light, new Dimension(image.getWidth(), image.getHeight()));
//		g.finalize();
//		g.dispose();
//	}
//	
	private static void draw(Graphics2D g, Point p, Color light,  Dimension d) {
		g.setStroke(new BasicStroke(2));
		
		
		g.setPaint(new RadialGradientPaint(
						new Point(p.x, p.y),
						visibilityRadius,
					    new float[] {0.5f, 1f},
					    new Color[] {light, new Color(0,0,0)}));
		g.fillOval(p.x - visibilityRadius, p.y-visibilityRadius, visibilityRadius*2, visibilityRadius*2);
		g.drawString(p.x + " " + p.y, 50, 50);

		
		
		Ellipse2D circle = new Ellipse2D.Double(p.x - visibilityRadius, p.y-visibilityRadius, visibilityRadius*2, visibilityRadius*2); 
		
//		g.setColor(Color.BLACK);
//		for (int i = 0; i < segments.length; i++) {
//			g.drawLine(segments[i].getX(), segments[i].getY(), segments[i].getX2(), segments[i].getY2());
//		}
		Shape oldClip = g.getClip(); 
//		for (int i = 0; i < lineSegments.length; i+=2) {
//			g.drawLine(lineSegments[i][0], lineSegments[i][1], lineSegments[i+1][0], lineSegments[i+1][1]);
//		}

//		for (int i = 0; i < lineSegments.length; i++) {
//			g.drawLine(p.x, p.y, lineSegments[i][0], lineSegments[i][1]);
//		}
		
//		g.setColor(Color.RED);
//		for (int i = 0; i < 3; i++) {
//			g.setColor(Color.GRAY);
//			g.drawLine(p.x, p.y, lineSegments[i][0], lineSegments[i][1]);
//			for (int j = 0; j < lineSegments.length; j+=2) {
//				g.setColor(Color.RED);
//				Point pp = getIntersectionPoint(
//						p.x, p.y, lineSegments[i][0], lineSegments[i][1],
//						lineSegments[j][0], lineSegments[j][1], lineSegments[j+1][0], lineSegments[j+1][1]
//						);
//				if(pp != null)
//				g.fillOval(pp.x-2, pp.y-2, 4, 4);
//			}
//		}
		
//		ArrayList<Point> pointsToFill = new ArrayList<Point>();

		for (int i = 0; i < segments.length; i++) { // TODO

			g.setColor(new Color(0,0,0, 100));
			Point pp1 = getIntersectionPoint(p, segments[i].getX(), segments[i].getY());
			g.setColor(new Color(0,0,0, 100));
			Point pp2 = getIntersectionPoint(p, segments[i].getX2(), segments[i].getY2());
			
			Segment ray = null, ray2 = null;

//			if(pp1 == null || pp2 == null) {
				ray2 = createRay(p, segments[i].getPoint2(),
						(int) Math.hypot(p.x-segments[i].x2,p.y-segments[i].y2));
				ray = createRay(p, segments[i].getPoint1(),
						(int) Math.hypot(p.x-segments[i].x,p.y-segments[i].y));
//			}
			/*if(pp2 == null) {
				g.setColor(Color.RED);
				g.drawOval(segments[i].x2-1, segments[i].y2-1, 2, 2);
				g.setColor(Color.BLUE);
				g.drawLine(ray2.x, ray2.y, ray2.x2, ray2.y2);
			}
			
			if(pp1 == null) {
				g.setColor(Color.RED);
				g.drawOval(segments[i].x-1, segments[i].y-1, 2, 2);
				g.setColor(Color.BLUE);
				g.drawLine(ray.x, ray.y, ray.x2, ray.y2);
			}*/

//			if(pp2 == null) {
//				pointsToFill.add(new Point(ray.x, ray.y));
//				pointsToFill.add(new Point(ray.x2, ray.y2));
//			}
//			if(pp1 == null) {
//				pointsToFill.add(new Point(ray2.x, ray2.y));
//				pointsToFill.add(new Point(ray2.x2, ray2.y2));
//			}
//			if(pp1 == null) {
//			}
//			if(pp2 == null) {
//			}

			
			if(ray != null && ray2 != null) {
				Polygon polygon = new Polygon(
						new int[] {ray.x, ray.x2, ray2.x2, ray2.x},
						new int[] {ray.y, ray.y2, ray2.y2, ray2.y}, 4);
				g.setClip(polygon);
//				g.setColor(Color.LIGHT_GRAY);
//				g.fillOval(p.x - visibilityRadius, p.y-visibilityRadius, visibilityRadius*2, visibilityRadius*2);

				g.setColor(Color.DARK_GRAY);
				
//				g.fillPolygon(polygon);
				g.clip(circle);
				g.clip(polygon);
//				g.setComposite(AlphaComposite.getInstance(3));
				g.setColor(BG);
				g.fill(circle);
				g.setClip(oldClip);
//				g.draw(circle);
//				g.draw(polygon); 
				
				
			}
//			g.fillArc(p.x, p.y, width, height, startAngle, arcAngle);
		}
//		g.setColor(Color.DARK_GRAY);
//
//		int[] xPoints = new int[pointsToFill.size()];
//		int[] yPoints = new int[pointsToFill.size()];
//		
//		for (int i = 0; i < pointsToFill.size(); i++) {
//			xPoints[i] = pointsToFill.get(i).x;
//			yPoints[i] = pointsToFill.get(i).y;
//		}
//		
//		g.fillPolygon(xPoints, yPoints, pointsToFill.size());
		
//		getIntersectionPoint
//		drawVisibleArea();
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
	
	public static Point getIntersectionPoint(Point p, int x, int y) {
		Segment ray = new Segment(p.x, p.y, x, y);
		ArrayList<Point> intersectionPoint = new ArrayList<Point>();
		for (int j = 0; j < segments.length; j++) {
			Point pp = ray.getIntersectionPoint(segments[j]);
			if(pp != null) {
				intersectionPoint.add(pp);
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
//			g.drawLine(p.x, p.y, pp.x, pp.y);
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
//	public static boolean getClosestIntersectionPoint(int ray, Stream<Point> segments) {
//		return segments.reduce((boolean closest, segment) => {
//			return getIntersectionPoint(ray, segment, closest ? closest.r : null) || closest;
//		}, null);
//	}
//	
	public static Point getIntersectionPoint(Point C, Point D, Point A,  Point B, Integer smallestR) {
//		int [A, B] = segment;
//		int [C, D] = ray;

		double denominator = (D.x - C.x) * (B.y - A.y) - (B.x - A.x) * (D.y - C.y);

		double r = ((B.x - A.x) * (C.y - A.y) - (C.x - A.x) * (B.y - A.y)) / denominator;
		if(r < 0) return null;
		if(smallestR != null && smallestR < r) return null;

		double s = ((A.x - C.x) * (D.y - C.y) - (D.x - C.x) * (A.y - C.y)) / denominator;
		if(s < 0 || s > 1) return null;

		return new Point((int)(s* (B.x - A.x) + A.x), (int) (s * (B.y - A.y) + A.y)); // , r 
	}

}
