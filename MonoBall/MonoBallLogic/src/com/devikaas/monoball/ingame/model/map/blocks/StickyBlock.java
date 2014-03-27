package com.devikaas.monoball.ingame.model.map.blocks;

import com.devikaas.monoball.ingame.model.map.Row;
import owg.engine.util.Kryo;

public class StickyBlock extends BasicBlock {
    private static final String SPRITE = "block-sticky";

    public static final char TYPE = 'x';

    @Kryo
    private StickyBlock() {
        super();
    }

    public StickyBlock(Row row, float xOffset, float width) {
        super(row, xOffset, width);
        setFriction(2.5f);
    }

    @Override
    public String getSprite() { return SPRITE; }

}
