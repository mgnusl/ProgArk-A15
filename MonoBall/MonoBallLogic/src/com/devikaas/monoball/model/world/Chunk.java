package com.devikaas.monoball.model.world;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bvx89
 * Date: 17/03/14
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
public class Chunk {
    private List<Row> rows;

    private Chunk next;
    private Chunk prev;

    public Chunk(List<Row> rows) {
        this.rows = rows;
    }

    public void setNext(Chunk c) {
        next = c;
    }

    public void setPrev(Chunk c) {
        prev = c;
    }
}
