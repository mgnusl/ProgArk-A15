package owg.engine;

public interface GameState {
    /**Called at a constant rate. All game logic should be executed here.*/
	public void step();
    /**Normally called after a step. However, it is not specified that this is always the case.
     * Therefore, computations affecting the game state should not happen in this method.
     * Only graphics related operations should take place here.*/
	public void render();
}
