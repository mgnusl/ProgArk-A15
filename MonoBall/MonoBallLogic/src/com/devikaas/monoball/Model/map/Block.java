package com.devikaas.monoball.model.map;

import owg.engine.game.Entity;
import owg.engine.game.SortedInstanceList;

/**
 * Created with IntelliJ IDEA.
 * User: bvx89
 * Date: 17/03/14
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */
public class Block {
    private int x;
    private char property;

    public Block(int x, char prop) {
        this.x = x;
        this.property = prop;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public char getProperty() {
        return property;
    }

}
