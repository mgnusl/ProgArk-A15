package com.devikaas.monoball.ingame.model;

import owg.engine.util.Compass;
import owg.engine.util.Kryo;
import owg.engine.util.V3F;

import com.devikaas.monoball.ingame.model.map.Collidable;

public class BallModel implements SpriteModel, Collidable, Steppable {
	public static final float FRICTION = 0.25f;
    public static final float AIR_FRICTION = 0.01f;

	private final BallGameModel model;
	
	private final V3F location;
	private final float radius;
	
	private float angle;
	private float angleSp;

	private final V3F speed;
	@Kryo
	public BallModel() {
		model=null;location=null;radius=0;speed=null;
	}
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
		speed.accelerate(-FRICTION*referenceFriction*normalForce);
		
		/*if(normalForce > 4) {
			float i = Calc.clamp(0.5f+normalForce/16f, 0f, 1f);
			// Engine.audioLib().get("clank").play(0.5f+i/2, 0, 1.5f-i/2);
		}*/
		
		angleSp = speed.createCross(normal).z()/(radius);
		
		return true;
	}

	@Override
	public void step() {
		location.add(speed);
		
		speed.add(model.getGravity());
        speed.accelerate(-speed.sqLen()*AIR_FRICTION);
		angle += angleSp;


		//Kills the player if he's outside the camera view
		CameraModel cam = model.getCamera();
		if(location.y() + radius < cam.getLocation().y() ||
				location.y() - radius > cam.getLocation().y() + cam.getHeight())
			kill();

	}

	// Switches players, effectivly killing the current player
	public void kill(){
		//Gets the amount of time player has been alive.
		float time = (float)model.getPlayerTimeLimit() - (float)model.getPlayerTimeLimit() / (float)model.getPlayerTime() * (float)model.getAlarm().get(0);

		//If player has been alive less than 0.5 seconds, he is invulnerable
		if(time > 0.5)
			model.switchPlayer();
	}


}
