package com.devikaas.monoball.ingame.model.map.blocks;

import owg.engine.util.Kryo;

import com.devikaas.monoball.ingame.model.BallModel;
import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.SolidLine;
import com.devikaas.monoball.ingame.model.map.Collidable;

public class DeathBlock extends BasicBlock {
    public final static char TYPE = 'd';

	@Kryo
	private DeathBlock() {
		super();
	}

	public DeathBlock(Row row, float xOffset, float width){
		super(row, xOffset, width);
	}

	@Override
	public String getSprite(){
		return "block-spikey";
	}

	@Override
	public void evaluateSurface(Collidable subject) {
		// verify subject is ball
		if (subject instanceof BallModel) {
			BallModel ball = (BallModel)subject;

			// Check for collisions
			for(SolidLine l : lines){
				if(l.evaluateLine(subject)) {
					ball.kill();
				}
			}
		}
	}

	@Override
	public void evaluateEndpoints(Collidable subject) {
		// verify subject is ball
        if (subject instanceof BallModel) {
            BallModel ball = (BallModel)subject;

            // Check for collisions
            for(SolidLine l : lines){
                if(l.evaluateEndpoints(subject)) {
                    ball.kill();
                }
            }
        }
	}

}