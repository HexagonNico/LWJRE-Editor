package gamma.editor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class EditorClassLoader extends ClassLoader {

	@Override
	public URL getResource(String name) {
		try {
			URL resource = super.getResource(name);
			return resource != null ? resource : new File("src/main/resources/" + name).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
