package owg.engine;

/**Specifies a program entry point.
 * Should be an awt container(e.g. a Frame) or and android activity.*/
public interface EntryPoint {
	/**Return a state implementation to handle the game logic.
	 * This method is called from the OpenGL thread, so it is safe to perform any game-related initialization here.
	 * The static methods on {@link #Engine} may be used to manipulate the game framework, and, 
	 * in particular, the GLUtil.*/
	public GameState getInitialState();
}
