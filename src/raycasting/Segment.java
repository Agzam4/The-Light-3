package raycasting;
import java.awt.Point;

public class Segment {
	
	protected int x,  y,  x2,  y2;
	
	public Segment(int x, int y, int x2, int y2) {
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public Segment(int x, int y, int size, double dir) {
		this.x = x;
		this.y = y;
		double maxSize = size;
		x2 = (int) (x + maxSize*Math.sin(dir));
		y2 = (int) (y + maxSize*Math.cos(dir));
		Point p = getIntersectionPoint(
				new Segment(x, y, x2, y2));
		if(p != null) {
			this.x2 = p.x;
			this.y2 = p.y;
		}
	}
	
	public boolean isIntersect(Segment segment) {
		int x1 = this.getX();
		int x2 = this.getX2();
		int x3 = segment.getX();
		int x4 = segment.getX2();
		int y1 = this.getY();
		int y2 = this.getY2();
		int y3 = segment.getY();
		int y4 = segment.getY2();
		
		int v1 = (x4-x3)*(y1-y3)-(y4-y3)*(x1-x3);
		int v2 = (x4-x3)*(y2-y3)-(y4-y3)*(x2-x3);
		int v3 = (x2-x1)*(y3-y1)-(y2-y1)*(x3-x1);
		int v4 = (x2-x1)*(y4-y1)-(y2-y1)*(x4-x1);
		return v1*v2 < 0 && v3*v4 < 0;
	}
	
	public Point getIntersectionPoint(Segment segment) {
		int x1 = this.getX();
		int x2 = this.getX2();
		int x3 = segment.getX();
		int x4 = segment.getX2();
		int y1 = this.getY();
		int y2 = this.getY2();
		int y3 = segment.getY();
		int y4 = segment.getY2();

		double v1, v2, v3, v4; 
		double xv12, xv13, xv14, xv31, xv32, xv34, yv12, yv13, yv14, yv31, yv32, yv34;

		xv12 = x2 - x1;		xv13 = x3 - x1;		xv14 = x4 - x1;	
		yv12 = y2 - y1;		yv13 = y3 - y1;		yv14 = y4 - y1;	
 
		xv31 = x1 - x3;		xv32 = x2 - x3;		xv34 = x4 - x3; 
		yv31 = y1 - y3;		yv32 = y2 - y3;		yv34 = y4 - y3; 

		v1 = xv34 * yv31 - yv34 * xv31; 
		v2 = xv34 * yv32 - yv34 * xv32; 
		v3 = xv12 * yv13 - yv12 * xv13; 
		v4 = xv12 * yv14 - yv12 * xv14; 
		
		if((v1 * v2) < 0 && (v3 * v4) < 0){
			int A1, B1, C1, A2, B2, C2;
			A1 = y2 - y1;									
            A2 = y4 - y3;
			B1 = x1 - x2;									
            B2 = x3 - x4;
			C1 = (x1 * (y1 - y2) + y1 * (x2 - x1)) * (-1);
	        C2 = (x3 * (y3 - y4) + y3 * (x4 - x3)) * (-1);
 
			double D = (double) ((A1 * B2) - (B1 * A2)); 
			double Dx = (double) ((C1 * B2) - (B1 * C2)); 
			double Dy = (double) ((A1 * C2) - (C1 * A2));
 
			if(D != 0){
				return new Point((int) (Dx / D), (int) (Dy / D));
			}
		}
		return null;
	}
	
	public Point getPoint1() {
		return new Point(x, y);
	}
	public Point getPoint2() {
		return new Point(x2, y2);
	}
	
	public int getX() {
		return x;
	}
	public int getX2() {
		return x2;
	}
	public int getY() {
		return y;
	}
	public int getY2() {
		return y2;
	}

	public void setPoint1(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPoint2(int x, int y) {
		this.x2 = x;
		this.y2 = y;
	}

	public int minDistance(Point p) {
		double d = Math.hypot(p.x-x, p.y-y);
		double d2 = Math.hypot(p.x-x2, p.y-y2);
		if(d > d2) {
			d = d2;
		}
		return (int) (d-Math.hypot(x-x2, y-y2));
	}
}
