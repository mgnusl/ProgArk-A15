package com.devikaas.monoball.ingame.model.map;

import com.devikaas.monoball.ingame.model.map.blocks.BasicBlock;
import com.devikaas.monoball.ingame.model.map.blocks.DeathBlock;
import com.devikaas.monoball.ingame.model.map.blocks.FakeBlock;
import com.devikaas.monoball.ingame.model.map.blocks.SpriteSwapBlock;

public class BlockFactory {
    public static final int BLOCKS_PER_LINE = 16;


    public static void createBlock(Row r, int offset, char type) {
    	float blockWidth = (float)MapModel.MAP_WIDTH/BLOCKS_PER_LINE;
    	
        switch(type){
            case BasicBlock.TYPE:
                new BasicBlock(r, offset*blockWidth, blockWidth);
                break;
            case DeathBlock.TYPE:
                new DeathBlock(r, offset*blockWidth, blockWidth);
                break;
            case FakeBlock.TYPE:
                new FakeBlock(r, offset*blockWidth, blockWidth);
                break;
            case SpriteSwapBlock.TYPE:
                new SpriteSwapBlock(r, offset*blockWidth, blockWidth);
                break;
		}
    }
}
