package com.devikaas.monoball.ingame.model.map.blocks;

import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.Collidable;

/**
 * Created by oknak_000 on 3/20/14.
 *
 * Simple block that is just like BasicBlock in every way,
 * except it does not handle collisions.
 *
 * This way it will act as a trap/shortcut block
 */
public class FakeBlock extends BasicBlock {
    public static final char TYPE = 'f';
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
