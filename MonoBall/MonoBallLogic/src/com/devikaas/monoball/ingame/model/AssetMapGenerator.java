package com.devikaas.monoball.ingame.model;

import com.devikaas.monoball.ingame.model.map.*;
import owg.engine.Engine;
import owg.engine.util.NamedInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import com.devikaas.monoball.ingame.model.map.BlockFactory;

public class AssetMapGenerator implements MapGenerator {
    private static final String PATH = "chunks";
    private Random random;

    private String[][] cachedChunks;
    private String[] availableChunks;

    public AssetMapGenerator(int seed) {
        random = new Random(seed);
        availableChunks = Engine.assets().listAssets(PATH);
        cachedChunks = new String[availableChunks.length][];
    }

    @Override
    public void generateChunk(MapModel map, boolean bottom) {
        // Get name of chunk to load

        int index = random.nextInt(availableChunks.length);
        if (cachedChunks[index] != null) {

        } else {
            String chunkName = availableChunks[index];
            try (NamedInputStream nis = Engine.assets().open(PATH + '/' + chunkName);
                 InputStreamReader isr = new InputStreamReader(nis);
                 BufferedReader br = new BufferedReader(isr);){

                String line;
                ArrayList<String> chunk = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    chunk.add(line);
                }

                cachedChunks[index] = new String[chunk.size()];
                chunk.toArray(cachedChunks[index]);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //TODO: Reverse row pushing when bottom is false?
        
        
        //Mandatory clear row
        new Row(map, bottom);
        
        for (int i = 0; i < cachedChunks[index].length; i++) {
            String line = cachedChunks[index][i];

            Row r = new Row(map, bottom);

            char blockType;
            for (int j = 0; j < line.length(); j++) {
                blockType = line.charAt(j);

                if (blockType == ' ') continue;

                BlockFactory.createBlock(r, j, blockType);
            }
        }
    }
}