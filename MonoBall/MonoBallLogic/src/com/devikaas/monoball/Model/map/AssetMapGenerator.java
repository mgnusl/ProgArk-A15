package com.devikaas.monoball.model.map;

import owg.engine.Engine;
import owg.engine.util.NamedInputStream;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: bvx89
 * Date: 17/03/14
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
public class AssetMapGenerator extends MapGenerator {
    private static final String PATH = "chunk";

    public AssetMapGenerator(int seed) {
        super(seed, Engine.assets().listAssets(PATH));
    }

    @Override
    protected Chunk loadNextChunk() {
        Chunk c = loadChunk();

        initChunk.setNext(c);
        c.setPrev(initChunk);

        return c;
    }

    @Override
    protected Chunk loadPrevChunk() {
        Chunk c = loadChunk();

        initChunk.setPrev(c);
        c.setNext(initChunk);

        return c;
    }

    private Chunk loadChunk() {
        // Get name of chunk to load
        String s = chunks[rand.nextInt(chunks.length)];

        Chunk c = null;
        try (NamedInputStream nis = Engine.assets().open(PATH + '/' + s)){
            c = getChunkByStream(nis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return c;
    }
}
