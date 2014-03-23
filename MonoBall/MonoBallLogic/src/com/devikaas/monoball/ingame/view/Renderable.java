package com.devikaas.monoball.ingame.view;

/**Should be implemented by all ingame view objects in order to receive render events.*/
public interface Renderable {
	/**Called when the object should be rendered to the screen.<br/>
	 * The drawing surface can be accessed through {@link owg.engine.Engine#glUtil()},
	 * or by calling the render methods on objects such as {@link owg.engine.graphics.Sprite2D}
	 * @param alpha The interpolation alpha, from 0 to 1. 
	 * If 0, then the model data for the previous frame should be rendered.
	 * If 1, then the model data for the current frame should be rendered.*/
	public void render(float alpha);
}
