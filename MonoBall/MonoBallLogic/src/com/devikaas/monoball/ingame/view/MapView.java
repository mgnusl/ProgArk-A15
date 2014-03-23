package com.devikaas.monoball.ingame.view;

import owg.engine.Engine;
import owg.engine.graphics.ColorF;

import com.devikaas.monoball.ingame.model.SpriteModel;
import com.devikaas.monoball.ingame.model.map.Block;
import com.devikaas.monoball.ingame.model.map.MapModel;
import com.devikaas.monoball.ingame.model.map.Row;

/**Displays the game map object.*/
public class MapView implements Renderable {
	
	private MapModel model;
	private BallGameView gameView;

	public MapView(BallGameView gameView, MapModel model) {
		this.gameView = gameView;
		this.model = model;
	}

	@Override
	public void render(float alpha) {
		Engine.glUtil().setColor(ColorF.WHITE);
		SpriteView s = new SpriteView(null);
		
		Row[] rr = model.getAccessibleRows(gameView.getVisibleRangeMinY(alpha), gameView.getVisibleRangeMaxY(alpha));
		
		for(Row r : rr) {
			for(Block b : r.getBlocks()) {
				if(b instanceof SpriteModel) {
					s.setModel((SpriteModel)b);
					s.render(alpha);
				}
			}
		}
	}

}
