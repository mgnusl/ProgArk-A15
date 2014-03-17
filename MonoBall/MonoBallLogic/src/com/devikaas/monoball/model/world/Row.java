package com.devikaas.monoball.model.world;

/**
 * Created with IntelliJ IDEA.
 * User: bvx89
 * Date: 17/03/14
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */
public class Row {
    public static final int ROW_WIDTH = 16;

    private Block[] blocks;
    private int y;

    public Row(int y, Block[] blocks) {
        this.blocks = blocks;
        this.y = y;
    }

}
