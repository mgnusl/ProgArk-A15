package com.devikaas.monoball.ingame.model;

import owg.engine.graphics.ColorF;
import owg.engine.graphics.ColorF.ColorFMutable;
import owg.engine.util.V3F;
/**The in-game camera. Defines the visible area of the game world.*/
public class CameraModel implements Steppable {
	private V3F location;
	private V3F speed;
	private float viewWidth, viewHeight;
	
	private ColorFMutable clearColor;
	
	public CameraModel(V3F location, float width, float height) {
		this.location = location;
		this.viewWidth = width;
		this.viewHeight = height;
		speed = new V3F();
		clearColor = ColorF.LTGRAY.getMutableCopy();
	}
	
	@Override
	public void step() {
		location.add(speed);
		//float screenAspect = (float)scene().getWidth()/scene().getHeight();
        //viewHeight = 320;
        //viewWidth = viewHeight*screenAspect;
	}	
	
	public V3F getLocation() {
		return location;
	}

	public float getWidth() {
		return viewWidth;
	}

	public float getHeight() {
		return viewHeight;
	}
	
	public ColorF getClearColor() {
		return clearColor;
	}

	public void setVerticalSpeed(float ySpeed) {
		speed.y(ySpeed);
	}
}
