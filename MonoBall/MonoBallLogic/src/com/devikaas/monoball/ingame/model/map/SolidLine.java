package com.devikaas.monoball.ingame.model.map;

import owg.engine.util.Kryo;
import owg.engine.util.V3F;
/**An abstraction for a solid line segment.*/
public class SolidLine {
	/**Whether to allow collisions "behind" the line. 
	 * This is more likely to create problems if the line is used in shape with sharp angles,
	 * unless the lines are sorted on distance before each collision test.<br/>
	 * Under normal circumstances, the line normal will point 90 degrees clockwise from the direction of the line.
	 * E.g. extrovert polygons should be defined in a counter-clockwise order.*/
	final boolean allowBackfaceCollisions = false;
	
	final float friction;
	final float length;
	
	final V3F pos, scale;
	
	@Kryo
	private SolidLine() {
		friction=0;length=0;pos=null;scale=null;
	}
	/**
	 * Create a line segment. The line normal will point 90 degrees clockwise from the line's direction.
	 * @param start The first position of the line in world space.
	 * @param end The last position of the line in world space.
	 * @param friction The friction of the line segment. Ordinary material would be 1, frictionless material 0.
	 */
	public SolidLine(V3F start, V3F end, float friction) {
		pos = start.clone();
		scale = end.clone().sub(pos);
		length = scale.len();
		this.friction = friction;
		if (Float.isInfinite(length))
			throw new IllegalArgumentException("You cannot create a NaN-length line: "+start+", "+end);
		if (Float.isNaN(length))
			throw new IllegalArgumentException("You cannot create a NaN-length line: "+start+", "+end);
		if (length == 0)
			throw new IllegalArgumentException("You cannot create a zero-length line: "+start+", "+end);
	}
	/**Check if there is a collision with the indicated object and dispatch a collision event if there is.<br/>
	 * This method checks for collisions with the line segment itself, not the endpoints.*/
	public boolean evaluateLine(Collidable sphere) {
		
		float m = sphere.getSpeed().manLen();
		//Early bounds check
		if(		sphere.getLocation().x() >= pos.x()+Math.min(0, scale.x())-m-sphere.getRadius() || 
				sphere.getLocation().y() >= pos.y()+Math.min(0, scale.y())-m-sphere.getRadius() ||
				sphere.getLocation().x() <= pos.x()+Math.max(0, scale.x())+m+sphere.getRadius() || 
				sphere.getLocation().y() <= pos.y()+Math.max(0, scale.y())+m+sphere.getRadius()) {
		
			if(length == 0)
				return false;
			V3F normal = new V3F(-scale.y()/length, scale.x()/length, 0);
			V3F offset = sphere.getLocation().clone().sub(pos);
			float vDist = normal.dot(offset);
			
			if(vDist < 0) {
				if(!allowBackfaceCollisions)
					return false;
				normal.reverse();
				vDist = -vDist;
			}
			
			//The displacement is the amount that the sphere must move along the normal in order to get to the collision surface
			final float displacement = sphere.getRadius()-vDist;
			//The reach is how much further the sphere will be able to move along the normal.
			final float reach = sphere.getSpeed().dot(normal);
			
			final float ticksUntilCollision;
			//compute the number of speed vectors that will be necessary to generate a collision
			if(reach > -1) { //The reach is very small or in the wrong direction.
				if(displacement > -1) //If the displacement is very small or negative, 
					ticksUntilCollision = 0; //assume that the collision is already happening.
				else
					return false; //If the displacement is large then do not generate a collision.
			} else //The reach is going in the right direction, so we can calculate the time until the collision:
				ticksUntilCollision = Math.max(0,displacement/reach);
			
			if (ticksUntilCollision > 1)
				return false; //If the collision is too far into the future, do not generate a collision.
			
			//Evaluate the point on the line where the sphere will be at the time of the collision
			offset.set(sphere.getLocation()).add(sphere.getSpeed(), ticksUntilCollision).sub(pos);
			float f = offset.dot(scale)/length/length;
			if(f < 0 || f > 1)
				return false;//If the collision will be out of bounds, then do not generate a collision.
			
			float normalForce = -sphere.getSpeed().dot(normal);
			return sphere.collision(this, normal, pos.clone().add(scale, f), normalForce, friction);
		} 
		else {
			return false;
		}
	}
	/**Check if there is a collision with the indicated object and dispatch a collision event if there is.<br/>
	 * This method checks for collisions with a single endpoint.*/
	private boolean evaluateEndpoint(V3F point, Collidable sphere) {
		//Compute the squared radius
		float sqr = sphere.getRadius()*sphere.getRadius();
		
		V3F evalPoint = sphere.getLocation().clone();
		
		if(evalPoint.sqDistance(point) > sqr+1)
			return false;
		V3F normal = evalPoint.sub(point).normalize();
		float normalForce = -sphere.getSpeed().dot(normal);
				
		return sphere.collision(this, normal, point, normalForce, friction);
		
	}
	/**Check if there is a collision with the indicated object and dispatch a collision event if there is.<br/>
	 * This method checks for collisions with the two endpoints, not the line segment itself.*/
	public boolean evaluateEndpoints(Collidable subject) {
		boolean c1 = evaluateEndpoint(pos, subject);
		boolean c2 = evaluateEndpoint(pos.clone().add(scale), subject);
		return c1 || c2;
	}
	public V3F getLocation() {
		return pos;
	}
}
