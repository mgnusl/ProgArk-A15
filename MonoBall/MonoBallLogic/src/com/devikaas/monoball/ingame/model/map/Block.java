package com.devikaas.monoball.ingame.model.map;

public interface Block {
	public void evaluateSurface(Collidable subject);
	public void evaluateEndpoints(Collidable subject);
}
