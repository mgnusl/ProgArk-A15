package com.devikaas.monoball.ingame.model;

import owg.engine.graphics.ColorF;
import owg.engine.graphics.ColorF.ColorFMutable;
import owg.engine.util.Kryo;
import owg.engine.util.V3F;
/**The in-game camera. Defines the visible area of the game world.*/
public class CameraModel implements Steppable {
	private V3F previousLocation;
	private V3F location;
	private float verticalSpeed;
	private float viewWidth, viewHeight;
	
	private ColorFMutable clearColor;
    private final BallGameModel ballGameModel;

    private static final float MARGIN = 200;
	
    @Kryo
    private CameraModel() {
		ballGameModel=null;
	}
    
	public CameraModel(BallGameModel ballGameModel, V3F location, float width, float height) {
		this.location = location;
		this.previousLocation = location.clone();
		this.viewWidth = width;
		this.viewHeight = height;
		verticalSpeed = 0;
		clearColor = ColorF.LTGRAY.getMutableCopy();
		this.ballGameModel = ballGameModel;
	}
	
	@Override
	public void step() {
		previousLocation.set(location);
        location.add(0, verticalSpeed, 0);

        // Check location of ball
        float ballY = ballGameModel.getBall().getLocation().y();

        if (ballGameModel.isReversed()) {
            float totalBall = ballY - MARGIN;
            float totalCam = location.y();
            if (totalBall <= totalCam) {
                location.add(0, totalBall - totalCam, 0);
            }

        } else {
            float totalBall = ballY + MARGIN;
            float totalCam = location.y()+viewHeight;
            if (totalBall >= totalCam) {
                location.add(0, totalBall - totalCam, 0);
            }
        } 
	}
	public V3F getCurrentLocation() {
		return location;
	}
	public V3F getInterpolatedLocation(float alpha) {
		return previousLocation.clone().multiply(1-alpha).add(location, alpha);
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
		this.verticalSpeed = ySpeed;
	}

    public void reverse() {verticalSpeed *= -1;}
}
