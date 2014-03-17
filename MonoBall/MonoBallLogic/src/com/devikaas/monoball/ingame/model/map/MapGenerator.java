package com.devikaas.monoball.ingame.model.map;
/**An object which generates new map chunks on demand.*/
public interface MapGenerator {
	/**
	 * Generate a new chunk of non-zero size.
	 * @param map The map object originating the request. New rows should be bound to this map.<br/>
	 * The implementation may perform queries on the map state if necessary.
	 * @param bottom Whether the chunk will be added at the bottom.<br/>
	 * If at the bottom, the rows must be added top-to-bottom, so the first created row appears on the top of the new chunk.<br/>
	 * Otherwise, the rows must be added bottom-to-top, so the first created row appears on the bottom of the new chunk.
	 */
	public void generateChunk(MapModel map, boolean bottom);
}
