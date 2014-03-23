package com.devikaas.monoball.ingame.model.map.blocks;

import com.devikaas.monoball.ingame.model.map.Row;
import owg.engine.util.Kryo;

public class StickyBlock extends BasicBlock {
    public static final char TYPE = 'x';

    @Kryo
    private StickyBlock() {
        super();
    }

    public StickyBlock(Row row, float xOffset, float width) {
        super(row, xOffset, width);
        setFriction(4);
    }

}
