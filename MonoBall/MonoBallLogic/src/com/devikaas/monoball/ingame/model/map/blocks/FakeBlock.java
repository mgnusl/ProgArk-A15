package com.devikaas.monoball.ingame.model.map.blocks;

import owg.engine.util.Kryo;

import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.Collidable;

/**
 *
 * Simple block that is just like BasicBlock in every way,
 * except it does not handle collisions.
 *
 * This way it will act as a trap/shortcut block
 */
public class FakeBlock extends BasicBlock {
    public static final char TYPE = 'f';

	@Kryo
	private FakeBlock() {
		super();
	}

	public FakeBlock(Row row, float xOffset, float width){
		super(row, xOffset, width);
	}

	@Override
	public void evaluateEndpoints(Collidable subject){
		//Ignores collisions
	}
	@Override
	public void evaluateSurface(Collidable subject) {
		//Ignores collisions
	}

}
