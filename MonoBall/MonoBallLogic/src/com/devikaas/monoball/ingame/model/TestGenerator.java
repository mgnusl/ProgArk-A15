package com.devikaas.monoball.ingame.model;

import com.devikaas.monoball.ingame.model.map.MapGenerator;
import com.devikaas.monoball.ingame.model.map.MapModel;
import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.blocks.BasicBlock;

public class TestGenerator implements MapGenerator {

	@Override
	public void generateChunk(MapModel map, boolean bottom) {
		Row r = new Row(map, bottom);
		new BasicBlock(r, (float)Math.random()*(MapModel.MAP_WIDTH-64), 64);
	}

}
