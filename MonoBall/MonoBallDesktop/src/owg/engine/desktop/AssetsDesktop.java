package owg.engine.desktop;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import owg.engine.AssetProducer;
import owg.engine.util.NamedInputStream;

public class AssetsDesktop extends AssetProducer {
	public AssetsDesktop() {
		super();
	}

	@Override
	public String[] listAssets(String subDirName) {
		try {
			if(!subDirName.endsWith("/"))
				subDirName = subDirName+"/";

			URL dirURL = ClassLoader.getSystemClassLoader().getResource(subDirName);
			if(dirURL == null)
				System.err.println("Warning: "+subDirName+" does not seem to exist on the class path.");
			
			if (dirURL != null && dirURL.getProtocol().equals("file")) {
				/* A file path: easy enough */
				return new File(dirURL.toURI()).list();
			} 

			if (dirURL == null) {
				/* 
				 * In case of a jar file, we can't actually find a directory.
				 * Have to assume the same jar as class.
				 */
				String me = getClass().getName().replace(".", "/")+".class";
				dirURL = getClass().getClassLoader().getResource(me);
			}
			
			// A JAR path (may fail) 
			String jarPath = dirURL.getPath();
			int exclIndex = jarPath.indexOf("!");
			if(exclIndex >= 0)
				jarPath = jarPath.substring(5, exclIndex); //strip out only the JAR file
			if(!jarPath.endsWith(".jar")) {
				//Find the directory we're _actually_ running from(double clicky jar on linux yields a nonsense working directory):
				String jarDirectory = URLDecoder.decode(ClassLoader.getSystemResource("").getPath(),"UTF-8");
				int jPos  = jarDirectory.lastIndexOf(".jar");
				jarDirectory = jarDirectory.substring(0,jPos+4);//Ensure we didn't bring the file name with us
				if(!jarPath.endsWith(".jar"))
					throw new RuntimeException("It is not possible to retrieve the jar name for this platform.");
			}
			
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
			while(entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(subDirName)) { //filter according to the path
					String entry = name.substring(subDirName.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}
					result.add(entry);
				}
			}
			return result.toArray(new String[result.size()]);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public NamedInputStream open(String assetName) throws IOException {
		return new NamedInputStream(ClassLoader.getSystemClassLoader().getResourceAsStream(assetName), assetName);
	}
}
