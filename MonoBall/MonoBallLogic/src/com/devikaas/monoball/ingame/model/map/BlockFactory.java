package com.devikaas.monoball.ingame.model.map;

import com.devikaas.monoball.ingame.model.map.blocks.BasicBlock;
import com.devikaas.monoball.ingame.model.map.blocks.DeathBlock;
import com.devikaas.monoball.ingame.model.map.blocks.FakeBlock;
import com.devikaas.monoball.ingame.model.map.blocks.SpriteSwapBlock;
import com.devikaas.monoball.ingame.model.map.blocks.StickyBlock;

public class BlockFactory {
    public static final int BLOCKS_PER_LINE = 12;


    public static Block createBlock(Row r, int offset, char type) {
    	float blockWidth = (float)MapModel.MAP_WIDTH/BLOCKS_PER_LINE;

        switch(type){
            case BasicBlock.TYPE:
                return new BasicBlock(r, offset*blockWidth, blockWidth);
            case DeathBlock.TYPE:
                return new DeathBlock(r, offset*blockWidth, blockWidth);
            case FakeBlock.TYPE:
                return new FakeBlock(r, offset*blockWidth, blockWidth);
            case SpriteSwapBlock.TYPE:
                return new SpriteSwapBlock(r, offset*blockWidth, blockWidth);
            case StickyBlock.TYPE:
                return new StickyBlock(r, offset*blockWidth, blockWidth);
            default:
                return null;
		}
    }
}
