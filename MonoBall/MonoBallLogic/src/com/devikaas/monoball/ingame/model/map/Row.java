package com.devikaas.monoball.ingame.model.map;

import java.util.ArrayList;

import owg.engine.util.V3F;

public class Row {
	static final boolean TOP = false, BOTTOM = true;
	
	/**The height of a row in world space.*/
	public static final float ROW_HEIGHT = 16;
	/**An unstructured list of blocks.*/
	private final ArrayList<Block> blocks;
	/**The y offset in {@link #ROW_HEIGHT}s from the origin.*/
	private final int yOffset;
	
	public Row(MapModel map, boolean bottom) {
		map.pushRow(this, bottom);
		blocks = new ArrayList<>();
		if(bottom) {
			yOffset = map.getNumRows()-map.getOriginIndex()-1;
		} else {
			yOffset = -map.getOriginIndex();
		}
	}
	/**Returns the list of all blocks in the row.
	 * The returned list is not safe to modify, since it is the same as the row's internal list.*/
	public ArrayList<Block> getBlocks() {
		return blocks;
	}
	/**Returns the row's top-left location in world space.*/
	public V3F getLocation() {
		return new V3F(MapModel.MAP_X, yOffset * ROW_HEIGHT, 0);
	}
	public void pushBlock(Block block) {
		blocks.add(block);
	}
}
