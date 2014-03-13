package owg.engine.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import owg.engine.AssetProducer;
import owg.engine.util.NamedInputStream;

public class AssetsDesktop extends AssetProducer {
	public AssetsDesktop() {
		super();
	}
	
	@Override
	public String[] listAssets(String subDirName) {
		final File dir = new File("assets/"+subDirName);
    	if(!dir.isDirectory()) {
    		System.err.println("Warning: "+subDirName+" is not a subdirectory of assets...");
    		return new String[0];
    	}
    	File[] fileList = dir.listFiles();
    	String[] nameList = new String[fileList.length];
    	for (int i = 0; i<fileList.length; i++) {
    		nameList[i] = fileList[i].getName();
    	}
		return nameList;
	}

	@Override
	public NamedInputStream open(String assetName) throws IOException {
		return new NamedInputStream(new FileInputStream("assets/"+assetName), assetName);
	}

}
