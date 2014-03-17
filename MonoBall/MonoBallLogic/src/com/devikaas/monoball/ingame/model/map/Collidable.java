package com.devikaas.monoball.ingame.model.map;

import owg.engine.util.V3F;

/**Defines a circular or approximately circular object which is subject to collisions with the map.<br/>
 * The interface is generally not to be implemented by blocks, but by objects that interact with them such as the ball.<br/>
 * */
public interface Collidable {
	/**Gets the position of the object in the game world.*/
	public V3F getLocation();
	/**Gets the speed of the object, in game units per step.*/
	public V3F getSpeed();
	/**Gets the radius of the object, in game units. 
	 * The object is assumed to be a perfect circle for collision purposes.*/
	public float getRadius();
	/**
	 * Indicates that this object is colliding with some other object. <br/>
	 * Forces are suggested, but must be applied by the object itself.
	 * @param src The object which caused the collision. This will likely be a Block object.
	 * @param normal The direction of the normal force. The force must be applied by the implementation.
	 * @param referencePosition The position that the object must be moved to in order to go off the collision shape's tangent.
	 * @param normalForce The magnitude of the normal force in order to go off the collision shape's tangent.
	 * @param referenceFriction The friction coefficient of the collision surface. Will be 1 for an ordinary block, 0 for ice.
	 * @return Whether the collision had any effect. Should always be true for objects that are affected by collisions.
	 */
	public boolean collision(Object src, V3F normal, V3F referencePosition, float normalForce, float referenceFriction);
}
