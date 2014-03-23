package com.devikaas.monoball.ingame.model.map.blocks;

import owg.engine.util.Kryo;

import com.devikaas.monoball.ingame.model.BallModel;
import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.SolidLine;
import com.devikaas.monoball.ingame.model.map.Collidable;
/**
 * Created by oknak_000 on 3/18/14.
 */
public class DeathBlock extends BasicBlock{
	@Kryo
	private DeathBlock() {
		super();
	}
	
	public DeathBlock(Row row, float xOffset, float width){
		super(row, xOffset, width);
	}

	@Override
	public String getSprite(){
		return "deathBlock";
	}

	@Override
	public void evaluateSurface(Collidable subject) {
		BallModel ball = null;

		for(SolidLine l : lines){
			if(l.evaluateLine(subject) && subject instanceof BallModel){
				ball = (BallModel)subject;
			}
		}

		if(ball != null)
			ball.kill();
	}

	@Override
	public void evaluateEndpoints(Collidable subject) {
		BallModel ball = null;
		
		for(SolidLine l : lines){
			if(l.evaluateEndpoints(subject) && subject instanceof BallModel){
				ball = (BallModel)subject;
			}
		}

		if(ball != null)
			ball.kill();
	}

}