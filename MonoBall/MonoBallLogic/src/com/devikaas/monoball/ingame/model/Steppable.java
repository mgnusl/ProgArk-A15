package com.devikaas.monoball.ingame.model;

/**Should be implemented by all objects that need to be continuously updated.*/
public interface Steppable {
	/**A discrete time step.<br/>
	 * This event is called at a constant rate to ensure 
	 * consistent game speed and behaviour across different platforms.*/
	public void step();
}
