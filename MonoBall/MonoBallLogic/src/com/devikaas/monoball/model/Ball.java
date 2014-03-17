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
public class Ball {
    private static Ball instance = null;
    private V3F position;

    private Ball() {
        position = new V3F();
    }

    public static Ball getInstance() {
        if (instance == null)
            instance = new Ball();

        return instance;
    }

    public void setPosition(float x, float y) {
        position.set(x, y, position.z());
    }

    public V3F getPosition() {
        return position.clone();
    }
}