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
	private float startSpeed = 1f;
	private float viewWidth, viewHeight;
    private int stepCounter = 0;
	
	private ColorFMutable clearColor;
    private final BallGameModel ballGameModel;

    private static final float MARGIN = 300;
	
    @Kryo
    private CameraModel() {
		ballGameModel=null;
	}
    
	public CameraModel(BallGameModel ballGameModel, V3F location, float width, float height) {
		this.location = location;
		this.previousLocation = location.clone();
		this.viewWidth = width;
		this.viewHeight = height;
		verticalSpeed = startSpeed;
		clearColor = ColorF.LTGRAY.getMutableCopy();
		this.ballGameModel = ballGameModel;
	}
	
	@Override
	public void step() {
		previousLocation.set(location);
        stepCounter++;

        float speedStep = (float)(Math.log(stepCounter)) / 1000;
		if(verticalSpeed > 0)
			verticalSpeed += speedStep;
		else
			verticalSpeed -= speedStep;

        location.add(0, verticalSpeed, 0);


        // Check location of ball
        float ballY = ballGameModel.getBall().getLocation().y();

        // Move camera according to ball movement if it's reaching end of the screen
        float totalBall, totalCam, distance = 0;
        if (ballGameModel.isReversed()) {
            totalBall = ballY - MARGIN;
            totalCam = location.y();

            if (totalBall <= totalCam) {
                distance = totalBall - totalCam;
                location.add(0, distance, 0);
            }

        } else {
            totalBall = ballY + MARGIN;
            totalCam = location.y()+viewHeight;

            if (totalBall >= totalCam) {
                distance = totalBall - totalCam;
                location.add(0, distance, 0);
            }
        }

        // Avoid points for the ball having a fast descent/ascent
        if (distance != 0) {
            int score = (int)Math.abs(distance) / 3;
            ballGameModel.getCurrentPlayer().addScore(score);
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

    public void reverse() {
		if(verticalSpeed < 0)
			verticalSpeed = startSpeed;
		else
			verticalSpeed = -startSpeed;
		/*verticalSpeed *= -1;*/

        stepCounter = 0;
    }
}
