package owg.engine;

import java.util.ArrayList;

public abstract class Scene {
	/**Interface for objects that need to be notified of changes in the Scene's dimensions.*/
	public interface DimensionListener {
		/**Called when the Scene's dimension has changed, typically when the window is resized,
		 * or the screen orientation changes.*/
		public void dimensionChanged(Scene src, int width, int height);
	}

    protected GameState state;
    protected Animator animator;
    protected ArrayList<DimensionListener> dimensionListeners = new ArrayList<Scene.DimensionListener>();
	
	/**@return The width of the OpenGL canvas in pixels */
	public abstract int getWidth();
	/**@return The height of the OpenGL canvas in pixels */
	public abstract int getHeight();
	/**Set the instance that will handle game-specific logic and rendering on the OpenGL thread.*/
	public final void setState(GameState state) {
		this.state = state;
	}
	/**Add an object to be notified when the scene size changes*/
	public final void addDimensionListener(DimensionListener d) {
		dimensionListeners.add(d);
	}
	/**Remove an object to be notified when the scene size changes*/
	public final void removeDimensionListener(DimensionListener d) {
		dimensionListeners.remove(d);
	}
	/**Get the fixed-rate animator driving the Scene.*/
	public final Animator getAnimator() {
		return animator;
	}
	/**Set the default size, for window managers that can handle differently sized windows.*/
	public abstract void setPreferredSize(int width, int height);
}
