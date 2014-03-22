package com.devikaas.monoball.ingame.model;

import static owg.engine.Engine.scene;
import owg.engine.graphics.ColorF;
import owg.engine.graphics.ColorF.ColorFMutable;
import owg.engine.util.V3F;
/**The in-game camera. Defines the visible area of the game world.*/
public class CameraModel implements Steppable {
	private V3F location;
	private float speed;
	private float viewWidth, viewHeight;
	
	private ColorFMutable clearColor;
    private final BallGameModel ballGameModel;

    private static final float MARGIN = 200;
	
	public CameraModel(BallGameModel ballGameModel, V3F location, float width, float height) {
		this.location = location;
		this.viewWidth = width;
		this.viewHeight = height;
		speed = 0;
		clearColor = ColorF.LTGRAY.getMutableCopy();
		this.ballGameModel = ballGameModel;
	}
	
	@Override
	public void step() {
        float screenAspect = (float)scene().getWidth()/scene().getHeight();
        viewHeight = 320;
        viewWidth = viewHeight*screenAspect;

        location.add(0, speed, 0);

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
		this.speed = ySpeed;
	}

    public void reverse() {speed *= -1;}
}
