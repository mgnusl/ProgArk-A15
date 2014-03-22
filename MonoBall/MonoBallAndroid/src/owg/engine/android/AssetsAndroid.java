package owg.engine.android;

import java.io.IOException;
import android.content.Context;
import android.content.res.AssetManager;
import owg.engine.AssetProducer;
import owg.engine.util.NamedInputStream;

public class AssetsAndroid extends AssetProducer {
	
	AssetManager manager;
	
	public AssetsAndroid(Context ctx) {
		manager = ctx.getAssets();
	}
	
	public String[] listAssets(String subDirName) {
		try {
			return manager.list(subDirName);
		} catch (IOException e) {
			System.err.println("Warning: Could not open directory "+subDirName+"... ");
			e.printStackTrace();
			return new String[0];
		}
	}
	public NamedInputStream open(String assetName) throws IOException {
		return new NamedInputStream(manager.open(assetName), assetName);
	}

	public AssetManager getAssetManager()
		{
		return manager;
		}
}
