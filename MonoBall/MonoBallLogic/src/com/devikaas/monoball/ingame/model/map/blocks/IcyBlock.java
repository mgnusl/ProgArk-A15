package com.devikaas.monoball.ingame.model.map.blocks;

import com.devikaas.monoball.ingame.model.BallModel;
import com.devikaas.monoball.ingame.model.Steppable;
import com.devikaas.monoball.ingame.model.map.Collidable;
import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.SolidLine;
import owg.engine.util.Kryo;
import owg.engine.util.V3F;


public class IcyBlock extends BasicBlock {
    public static final char TYPE = 'm';
    private static final float FRICTION = 0f;
    private static final String SPRITE = "block-ice";

    @Kryo
    private IcyBlock() {
        super();
    }

    public IcyBlock(Row row, float xOffset, float width){
        super(row, xOffset, width);
        setFriction(FRICTION);
    }

    @Override
    public String getSprite() {
        return SPRITE;
    }
}
