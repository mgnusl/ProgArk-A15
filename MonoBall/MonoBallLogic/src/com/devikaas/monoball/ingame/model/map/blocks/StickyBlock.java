package com.devikaas.monoball.ingame.model.map.blocks;

import com.devikaas.monoball.ingame.model.map.Row;

public class StickyBlock extends BasicBlock {

    public StickyBlock(Row row, float xOffset, float width) {
        super(row, xOffset, width);
        setFriction(4);
    }

}
