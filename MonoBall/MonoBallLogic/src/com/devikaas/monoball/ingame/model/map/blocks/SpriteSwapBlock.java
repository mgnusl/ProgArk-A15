package com.devikaas.monoball.ingame.model.map.blocks;

import com.devikaas.monoball.ingame.model.BallModel;
import com.devikaas.monoball.ingame.model.map.Collidable;
import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.SolidLine;
import owg.engine.Engine;
import owg.engine.graphics.SpriteLib;
import owg.engine.util.Kryo;

import java.util.ArrayList;

public class SpriteSwapBlock extends BasicBlock{
    public static final char TYPE = 'c';
    private static final int SCORE = 10;

    private static String[] sprites = null;
    private boolean hasChanged = false;

    @Kryo
    private SpriteSwapBlock() {
        super();
    }

    public SpriteSwapBlock(Row row, float xOffset, float width){
        super(row, xOffset, width);
        if (sprites == null) {
            getAllSprites();
        }
    }

    private void getAllSprites() {

        String[] allSprites = Engine.assets().listAssets(SpriteLib.SPRITE_LIB);

        ArrayList<String> spritesArray = new ArrayList<>(allSprites.length);

        for (int i = 0; i < allSprites.length; i++) {
            if (allSprites[i].contains("ball")) {
                spritesArray.add(allSprites[i].substring(0, allSprites[i].length()-4));
            }
        }
        sprites = new String[spritesArray.size()];

        spritesArray.toArray(sprites);
    }

    @Override
    public void evaluateSurface(Collidable subject) {
        // verify subject is ball
        if (subject instanceof BallModel) {
            BallModel ball = (BallModel)subject;

            // Check for collisions
            for(SolidLine l : lines){
                if(l.evaluateLine(subject)) {
                    if (!hasChanged) {

                        // Pick random sprites (+1 for an extra index, the default sprite)
                        int index = (int)(Math.random() * (sprites.length + 1));

                        if (index == 0)
                            ball.setSprite(ball.DEFAULT_SPRITE);
                        else
                            ball.setSprite(sprites[index-1]);

                        ball.subtractScoreForActivePlayer(SCORE);


                        System.out.println("Surface: Sprite changed");
                        hasChanged = true;
                    }
                }
            }
        }
    }

    @Override
    public void evaluateEndpoints(Collidable subject) {
        // Check for collisions
        for(SolidLine l : lines){
            l.evaluateEndpoints(subject);
        }
    }

    @Override
    public String getSprite() {
        return "block-swap";
    }
}
