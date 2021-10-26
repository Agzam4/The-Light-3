package game;

import java.awt.Color;
import java.util.ArrayList;

import raycasting.Segment;

public class Block {

	boolean isLightTransmitting = true;
	Color lightColor;
	int lightPower = 10;
	
	public Block(Color lightColor) {
		setLight(lightColor);
	}
	
	public void setLight(Color lightColor) {
		this.lightColor = lightColor;
		if(lightColor.getRGB() == Color.BLACK.getRGB()) {
			isLightTransmitting = true;
			lightPower = 0;
		}
	}
	
	public void setLightPower(int lightPower) {
		if(lightPower < 1) {
			lightPower = 0;
		}
		this.lightPower = lightPower;
	}
	
	
	public boolean isLightTransmitting() {
		return isLightTransmitting;
	}
	
	public void setLightTransmitting(boolean isLightTransmitting) {
		this.isLightTransmitting = isLightTransmitting;
	}
	
	public int getLightPower() {
		return lightPower;
	}
	
	public Color getLightColor() {
		return lightColor;
	}
	
	public void addSegments(ArrayList<Segment> segments, int x, int y) {
		if(!isLightTransmitting) {
//			segments.add(getSegment(x, y, x+1, y+1));
//			segments.add(getSegment(x+1, y, x, y+1));
			
			segments.add(getSegment(x, y, x+1, y));
			segments.add(getSegment(x, y, x, y+1));
			segments.add(getSegment(x, y+1, x+1, y+1));
			segments.add(getSegment(x+1, y, x+1, y+1));
		}
	}
	
	
	private Segment getSegment(int x, int y, int x2, int y2) {
		return new Segment(x*MainRender.blockSize, y*MainRender.blockSize, x2*MainRender.blockSize, y2*MainRender.blockSize);
	}
}
