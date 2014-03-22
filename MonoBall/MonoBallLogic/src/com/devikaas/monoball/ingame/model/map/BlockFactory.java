package com.devikaas.monoball.ingame.model.map;

import com.devikaas.monoball.ingame.model.map.blocks.BasicBlock;
import com.devikaas.monoball.ingame.model.map.blocks.DeathBlock;
import com.devikaas.monoball.ingame.model.map.blocks.FakeBlock;

public class BlockFactory {
    public static final int BLOCKS_PER_LINE = 16;


    public static void createBlock(Row r, int offset, char type) {
    	float blockWidth = (float)MapModel.MAP_WIDTH/BLOCKS_PER_LINE;
    	
        if(type == 'a'){
			new BasicBlock(r, offset*blockWidth, blockWidth);
		}else if(type == 's'){
			new DeathBlock(r, offset*blockWidth, blockWidth);
		}else if(type == 'f'){
			new FakeBlock(r, offset*blockWidth, blockWidth);
		}
		// TODO: MORE BLOCKS!!!


    }
}
