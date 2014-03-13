package owg.engine.game;

import owg.engine.Engine;
import owg.engine.graphics.ColorF;
import owg.engine.graphics.Sprite2D;
import owg.engine.util.Calc;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

/**Represents basic functionality required by distinct objects that 
 * exist in the game world and are displayed on the screen.
 * As a convenience, this class contains default implementations for certain standard functionality.*/
public class Entity<S extends SortedInstanceList> {
	/**Global ID counter.*/
	private static long currentID = 0;
	/**Unique instance ID on a per-session basis.*/
	public final transient long id = currentID++;
	/**The instance list to which this instance belongs*/
	protected S instanceList;
	
	/**The current depth of the instance. Always reflects its position in the instance list.
	 * Is updated via {@link #depthUpdated()}*/
	private int currentDepth;
	/**The next depth of the instance. If inequal to {@link #currentDepth}, the list position will be changed ASAP.
	 * Is changed via {@link #changeDepth(int)}*/
	private int nextDepth;
	
	/**Whether the object has been destroyed(removed from the instance list).
	 * The application should strive to sever all references to objects before destroying them.*/
	private boolean isDestroyed = false;
	/**Whether the object should be rendered.*/
	protected boolean isVisible = true;
	
	/**A sprite image associated with the entity. Subclasses may ignore this property if it is not useful.*/
	public Sprite2D sprite = null;
	/**The current subimage of the {@link #sprite}.*/
	public float imageIndex = 0.0f;
	/**The animation rate for the {@link #sprite}.*/
	public float imageSpeed = 0.0f;
	/**The orientation of the {@link #sprite}.*/
	public Compass orientation = Compass.CENTER;
	/**The angle of the {@link #sprite}.*/
	public float angle = 0.0f;
	
	/**The object's color*/
	public final ColorF.ColorFMutable color;
	
	/**The object's scale factor.*/
	public final V3F scale;
	/**The object's position in the game world.
	 * @see #speed*/
	public final V3F pos;
	/**The object's speed in units per step in the game world.
	 * @see #pos*/
	public final V3F speed;
	
	public Entity(S instanceList, int depth) {
		this.instanceList = instanceList;
		this.currentDepth = depth;
		this.nextDepth = depth;
		instanceList.addInstance(this);
		
		this.color = ColorF.WHITE.getMutableCopy(); 
		
		this.pos = new V3F();
		this.speed = new V3F();
		this.scale = new V3F(1,1,1);
	}
	/**
	 * Call to change the instance's depth. 
	 * Note that this will not take effect until the current step or render iteration has completed.
	 * @param depth The new depth value to use. Note that {@link #getCurrentDepth()} will return the old depth until
	 * the instance's position has been updated in the instance list.<br/> 
	 * {@link #getNextDepth()}, however, will return the new value immediately.
	 */
	public void changeDepth(int depth) {
		int nd = nextDepth;
		nextDepth = depth;
		if(currentDepth == nd)//We only need to send the change signal once.
			instanceList.changeInstanceDepth(this);
	}
	/**Is called when the instance's position in the instance list has been updated to reflect it's new depth.*/
	void depthUpdated() {
		currentDepth = nextDepth;
	}
	/**Returns the current depth of the instance, as reflected in the instance list. 
	 * @see #changeDepth(int)*/
	public int getCurrentDepth() {
		return currentDepth;
	}
	/**Returns the desired depth of the instance.
	 * @see #changeDepth(int)*/
	public int getNextDepth() {
		return nextDepth;
	}

	/**Called at a constant rate. All game logic should be executed here.
	 * The default implementation contains logic for sprite animation and 
	 * advancing the position based on the object's speed.
	 * Most objects will benefit from dispatching a call to super.step 
	 * when overriding this method, but it is not required.*/
	public void step() {
		pos.add(speed);
		if(sprite != null)
			imageIndex = Calc.cyclic(imageIndex+imageSpeed, sprite.getNumFrames());
	}
	/**Normally called after a step. However, it is not specified that this is always the case.
	 * Computations affecting the game state should not happen in this method.
	 * Only graphics related operations should take place here.
	 * The default implementation renders the object's sprite.*/
	public void render() {
		if(sprite != null) {
			Engine.glUtil().setColor(color);
			sprite.render(((int)imageIndex)%sprite.getNumFrames(), pos, orientation, scale.x(), scale.y(), angle);
		}
	}
	/**Call this when the instance should be removed from the instance list.
	 * Destroyed objects will no longer receive step/render calls.<br/>
	 * <br/>
	 * The application should make an effort to make destroyed objects inaccessible, 
	 * so that they may be garbage collected.<br/>
	 * <br/>
	 * The application should not call this multiple times on the same object.*/
	public void destroy() {
		if(isDestroyed) {
			System.err.println("Warning: Double destruction of "+this);
			new Throwable().printStackTrace();
			return;
		}
		instanceList.removeInstance(this);
		isDestroyed = true;
	}
	/**Returns whether {@link #destroy()} has been called on this instance.*/
	public boolean isDestroyed() {
		return isDestroyed;
	}
	/**Sets whether the object should receive {@link #render()} calls.*/
	public void setVisible(boolean v) {
		this.isVisible = v;
	}
	
	/**Returns whether the object should receive {@link #render()} calls.*/
	public boolean isVisible() {
		return isVisible;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+id;
	}
}
