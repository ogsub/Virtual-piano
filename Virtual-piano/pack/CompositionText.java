package pack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains imput file with constructor and getter
 * @author Ognjen
 *
 */
public class CompositionText {
	private File file;

	
	public CompositionText(String pathToComp_) {
		file = new File(pathToComp_);
	}

	public File getFile() {
		return file;
	}
	
	
}
