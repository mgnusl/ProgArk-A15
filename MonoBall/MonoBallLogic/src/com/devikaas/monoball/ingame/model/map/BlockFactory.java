package com.devikaas.monoball.ingame.model.map;

import com.devikaas.monoball.ingame.model.map.blocks.BasicBlock;

public class BlockFactory {

    public static void createBlock(Row r, int offset, char type) {
        // TODO: Create different blocks

        // TODO: Set a block width
        new BasicBlock(r, offset*Row.ROW_HEIGHT, Row.ROW_HEIGHT);
    }
}
