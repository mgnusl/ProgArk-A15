package com.devikaas.monoball.ingame.model.map.blocks;

import com.devikaas.monoball.ingame.model.BallModel;
import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.SolidLine;
import com.devikaas.monoball.ingame.model.map.Collidable;
/**
 * Created by oknak_000 on 3/18/14.
 */
public class DeathBlock extends BasicBlock {
    public final static char TYPE = 's';
	public DeathBlock(Row row, float xOffset, float width){
		super(row, xOffset, width);
	}

	@Override
	public String getSprite(){
		return "deathBlock";
	}

	@Override
	public void evaluateSurface(Collidable subject) {
		evaluateEndpoints(subject);
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