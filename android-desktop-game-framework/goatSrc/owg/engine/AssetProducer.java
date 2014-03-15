package owg.engine;

import java.io.IOException;
import owg.engine.util.NamedInputStream;
/**Facilitates directory listings and input streams for files in the assets folder.*/
public abstract class AssetProducer {
	/**List the names of all assets in the given subdirectory of the assets folder.
	 * @param subDirName The relative folder path from the assets folder, without any leading or trailing slashes.
	 * @return The names of the files found in the directory.<br/>
	 * The returned file names do not contain the directory path.<br/>
	 * e.g. listAssets("textures") might return {a.png, b.png}*/
	public abstract String[] listAssets(String subDirName);
	/**Return an input stream for the given file path relative to the assets folder.<br/>
	 * e.g. "textures/example.png"*/
	public abstract NamedInputStream open(String assetName) throws IOException;
}
