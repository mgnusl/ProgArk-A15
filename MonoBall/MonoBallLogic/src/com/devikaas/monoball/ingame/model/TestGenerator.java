package com.devikaas.monoball.ingame.model;

import java.util.Random;

import owg.engine.util.Kryo;

import com.devikaas.monoball.ingame.model.map.MapGenerator;
import com.devikaas.monoball.ingame.model.map.MapModel;
import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.blocks.BasicBlock;

public class TestGenerator implements MapGenerator {
	private Random random;

	@Kryo
	private TestGenerator() {}
	
	public TestGenerator(int seed) {
		random = new Random(seed);
	}
	
	@Override
	public void generateChunk(MapModel map, boolean bottom) {
		Row r = new Row(map, bottom);
		new BasicBlock(r, random.nextFloat()*(MapModel.MAP_WIDTH-64), 64);
	}

}
