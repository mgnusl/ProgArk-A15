package com.devikaas.monoball.model;

import owg.engine.game.Entity;
import owg.engine.game.SortedInstanceList;
import owg.engine.util.V3F;

/**
 * Created with IntelliJ IDEA.
 * User: bvx89
 * Date: 17/03/14
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class Ball extends Entity {
    private static final int DEPTH = 1;

    public Ball(SortedInstanceList sil) {
        super(sil, DEPTH);
    }
}