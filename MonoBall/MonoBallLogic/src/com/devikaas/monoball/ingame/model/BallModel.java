package com.devikaas.monoball.ingame.model;

import com.devikaas.monoball.ingame.model.map.Collidable;

import owg.engine.Engine;
import owg.engine.util.Calc;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

public class BallModel implements SpriteModel, Collidable, Steppable {
	public static final float friction = 0.25f;

	private final BallGameModel model;
	
	private final V3F location;
	private final float radius;
	
	private float angle;
	private float angleSp;

	private final V3F speed;

	public BallModel(BallGameModel model, V3F location, float radius) {
		this.model = model;
		this.location = location;
		this.speed = new V3F();
		this.radius = radius;
		this.angle = 0;
		this.angleSp = 0;
	}
	
	@Override
	public String getSprite() {
		return "ball";
	}

	@Override
	public int getSubimage() {
		return 0;
	}

	@Override
	public V3F getSpriteLocation() {
		return location;
	}

	@Override
	public Compass getOrientation() {
		return Compass.CENTER;
	}

	@Override
	public float getXScale() {
		return radius/50f;
	}

	@Override
	public float getYScale() {
		return getXScale();
	}

	@Override
	public float getAngle() {
		return angle;
	}

	@Override
	public V3F getLocation() {
		return location;
	}

	@Override
	public V3F getSpeed() {
		return speed;
	}

	@Override
	public float getRadius() {
		return radius;
	}

	@Override
	public boolean collision(Object src, V3F normal, V3F referencePosition,
			float normalForce, float referenceFriction) {
		if(normalForce < 0)
			return false;
		location.set(referencePosition).add(normal, radius);
		speed.add(normal, normalForce);
		speed.accelerate(-friction*referenceFriction);
		
		if(normalForce > 4) {
			float i = Calc.clamp(0.5f+normalForce/16f, 0f, 1f);
			Engine.audioLib().get("clank").play(0.5f+i/2, 0, 1.5f-i/2);
		}
		
		angleSp = speed.createCross(normal).z()/(radius);
		
		return true;
	}

	@Override
	public void step() {
		location.add(speed);
		speed.add(model.getGravity());
		
		angle += angleSp;
	}

}
