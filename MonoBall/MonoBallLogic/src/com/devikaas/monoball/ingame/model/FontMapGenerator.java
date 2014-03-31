package com.devikaas.monoball.ingame.model;

import java.io.IOException;
import java.util.Random;

import owg.engine.util.Calc;
import owg.engine.util.Kryo;
import com.devikaas.monoball.ingame.model.map.BlockFactory;
import com.devikaas.monoball.ingame.model.map.MapGenerator;
import com.devikaas.monoball.ingame.model.map.MapModel;
import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.blocks.BasicBlock;

import static owg.engine.Engine.*;

public class FontMapGenerator implements MapGenerator {
	boolean[][][] blocks;
	String mapText;
	int[] index = new int[2];
	Random random;
	
	@Kryo
	private FontMapGenerator() {}
	
	public FontMapGenerator(String filepath, String mapText, int seed) {
		this.random = new Random(seed);
		this.mapText = mapText;
		String[] text;
		try {
			text = Calc.readFileArray(assets().open(filepath));
		} catch (IOException e) {
			throw new RuntimeException("No such file for "+this+": "+filepath, e);
		}
		blocks = new boolean[26][text.length][];
		int startx = 0;
		for(int character = 0; character < 26; character++) {
			//Find empty vertical space in ascii art
			int cend = findEmptySpace(text, startx);
			int clen = cend-startx;
			//Create a boolean array for each character
			for(int y = 0; y<text.length; y++) {
				blocks[character][y] = new boolean[clen];
				for(int x = startx; x<cend; x++) {
					Calc.println(character,y,x,startx, "textl", text.length, "blockl", blocks.length, blocks[character].length, blocks[character][y].length);
					blocks[character][y][x-startx] = text[y].charAt(x)!=' ';
				}
			}
			
			startx = cend+1;
		}
	}
	
	private int findEmptySpace(String[] text, int startx) {
		int end = -1;
		for(int x = startx; x<text[0].length(); x++) {
			boolean isEnd = true;
			for(int y = 0; y<text.length; y++) {
				if(text[y].charAt(x) != ' ')
					isEnd = false;
			}
			if(isEnd) {
				end = x;
				break;
			}
		}
		if(end == -1)
			return text[0].length();
		else
			return end;
	}

	@Override
	public void generateChunk(MapModel map, boolean bottom) {
		int i = bottom?0:1;
		int c = mapText.charAt(index[i])-'a';
		if(c < 0 || c >= blocks.length) {
			new Row(map, bottom);
		} else {
			boolean[][] character = blocks[c];
			int span = BlockFactory.BLOCKS_PER_LINE-character[0].length;
			int baseX = (int)(random.nextDouble()*(span+1));
			
			new Row(map, bottom);
			
			for(int y = 0; y<character.length; y++) {
				Row r = new Row(map, bottom);
				for(int x = 0; x<character[0].length; x++) {
					if(character[y][x])
						BlockFactory.createBlock(r, baseX+x, BasicBlock.TYPE);
				}
			}
		}
		index[i]++;
		if(index[i] >= mapText.length())
			index[i] = 0;
	}
}
