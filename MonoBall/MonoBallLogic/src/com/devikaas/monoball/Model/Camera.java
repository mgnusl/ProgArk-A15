package com.devikaas.monoball.model;

import owg.engine.util.V3F;

/**
 * Very simple camera class
 *
 * Created by Ole Kristian on 3/17/14.
 */
public class Camera {
    /* Singleton */
    private static Camera instance = null;

    public static Camera getInstance(){
        if(instance == null)
            instance = new Camera();

        return instance;
    }

    public V3F size;
    public V3F position;
    private float speed;

    private Camera(){
		//TODO: Set proper values
        position = new V3F(0,0,0);
        size = new V3F(18,32,0);
        speed = 0;
    }

    public void step(){
		/**
		 * TODO: Increase speed
		 *
		 * Updates position according to camera speed.
		 * NOTE: Might me implemented incorrectly
		 */
		position.y(position.y() + speed);
    }

    public void render() {

    }

	public float bottom(){
		return position.y() + size.y();
	}

	public float top(){
		return position.y();
	}
}
