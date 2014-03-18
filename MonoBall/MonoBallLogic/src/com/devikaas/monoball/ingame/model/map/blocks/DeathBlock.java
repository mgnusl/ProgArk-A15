package com.devikaas.monoball.ingame.model.map.blocks;

import com.devikaas.monoball.ingame.model.BallModel;
import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.SolidLine;
import com.devikaas.monoball.ingame.model.map.Collidable;
/**
 * Created by oknak_000 on 3/18/14.
 */
public class DeathBlock extends BasicBlock{
	public DeathBlock(Row row, float xOffset, float width){
		super(row, xOffset, width);
	}

	@Override
	public String getSprite(){
		return "deathBlock";
	}

	@Override
	public void evaluateSurface(Collidable subject) {
		boolean collidedWithBall = false;
		for(SolidLine l : lines){
			if(l.evaluateLine(subject) && subject instanceof BallModel)
				collidedWithBall=true;
		}

		if(collidedWithBall)
			((BallModel)subject).kill();
	}

	@Override
	public void evaluateEndpoints(Collidable subject) {
		boolean collidedWithBall = false;
		for(SolidLine l : lines){
			if(l.evaluateEndpoints(subject) && subject instanceof BallModel)
				collidedWithBall = true;
		}

		if(collidedWithBall)
			((BallModel)subject).kill();

	}
}