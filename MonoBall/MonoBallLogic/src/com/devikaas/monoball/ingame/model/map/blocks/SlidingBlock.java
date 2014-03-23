package com.devikaas.monoball.ingame.model.map.blocks;

import com.devikaas.monoball.ingame.model.Steppable;
import com.devikaas.monoball.ingame.model.map.Row;
import owg.engine.util.Kryo;


public class SlidingBlock extends BasicBlock implements Steppable {
    public static final char TYPE = 'f';
    private static final float SPEED = 20;

    private int length = 0;
    private boolean directionRight = false;

    @Kryo
    private SlidingBlock() {
        super();
    }

    public SlidingBlock(Row row, float xOffset, float width){
        super(row, xOffset, width);
    }

    @Override
    public void step() {


    }

    public void setTrackLength(int length) {
        this.length = length;
    }
}
