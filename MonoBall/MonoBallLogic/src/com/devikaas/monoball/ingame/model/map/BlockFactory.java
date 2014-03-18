package com.devikaas.monoball.ingame.model.map;

import com.devikaas.monoball.ingame.model.map.blocks.BasicBlock;
import com.devikaas.monoball.ingame.model.map.blocks.DeathBlock;

public class BlockFactory {
    public static final float BLOCK_WIDTH = 0;


    public static void createBlock(Row r, int offset, char type) {
        // TODO: Set a block width
        if(type == 'a'){
			new BasicBlock(r, offset*Row.ROW_HEIGHT, Row.ROW_HEIGHT);
		}else if(type == 's'){
			new DeathBlock(r, offset*Row.ROW_HEIGHT, Row.ROW_HEIGHT);
		}

    }
}
