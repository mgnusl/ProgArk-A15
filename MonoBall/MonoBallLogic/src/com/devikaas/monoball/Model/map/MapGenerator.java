package com.devikaas.monoball.model.map;

import owg.engine.util.NamedInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: bvx89
 * Date: 17/03/14
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
abstract class MapGenerator {
    // The seed in which to build the map
    protected Random rand;

    // Name of all the chunks to generate from
    protected String[] chunks;

    /*
     * This is the start chunk that will be used as center
     * for all the other chunks that's being created
     */
    protected Chunk initChunk;


    /*
     * Stores information about how far the chunks expands in each
     * direction, so the generator knows when to create more chunks.
     */
    protected int maxY;
    protected int minY;

    /*
     * The limit before the map generator will try to create a new chunk
     */
    private final static int LIMIT = 10;


    private final static char BLANK = ' ';

    /**
     *
     * @param seed - Used to decide which chunks to generate
     * @param chunks - List of available chunks
     */
    public MapGenerator(int seed, String[] chunks) {
        rand = new Random(seed);
        this.chunks = chunks;

        // Generate new empty blocks
        Block[] blocks = new Block[Row.ROW_WIDTH];
        for (int i = 0; i < Row.ROW_WIDTH; i++) {
            blocks[i] = new Block(i, BLANK);
        }

        // Append blocks to two Row objects and create a new initial chunk
        List<Row> rows = new ArrayList<>();
        rows.add(new Row(0, blocks));
        rows.add(new Row(1, blocks));
        initChunk = new Chunk(rows);

        // Load the previous and next chunks
        Chunk next = loadNextChunk();
        Chunk prev = loadPrevChunk();

        // Set the current length of the world
        maxY = 2 + next.getRows().size();
        minY = prev.getRows().size();
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinY() {
        return maxY;
    }

    protected abstract Chunk loadNextChunk();
    protected abstract Chunk loadPrevChunk();

    protected Chunk getChunkByStream(NamedInputStream nis) {

        // Objects inside the chunk
        List<Row> rows = new ArrayList<>();
        Block[] blocks;

        String line;
        int y = 0;

        try (InputStreamReader isr = new InputStreamReader(nis);
             BufferedReader br = new BufferedReader(isr);){

            while ((line = br.readLine()) != null) {

                blocks = new Block[Row.ROW_WIDTH];
                char[] chars = line.toCharArray();

                // Be sure not to hit EOL on iteration
                int min = Math.min(chars.length, Row.ROW_WIDTH);

                for (int i = 0; i < min; i++) {
                    blocks[i] = new Block(i, chars[i]);
                }

                // Insert blank spaces if EOL before ROW_WIDTH
                if (min < Row.ROW_WIDTH) {
                    for (int i = min; i < Row.ROW_WIDTH - min; i++) {
                        blocks[i] = new Block(i, BLANK);
                    }
                }

                rows.add(new Row(y++, blocks));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return new Chunk(rows);
        }
    }

    public Chunk getInitChunk() {
        return initChunk;
    }
}
