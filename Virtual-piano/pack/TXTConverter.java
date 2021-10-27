package pack;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Vector;

public class TXTConverter {
    private BufferedWriter writer;
    private String path;
    
    public TXTConverter(String path_) {
		path = path_;
		try {
			writer = new BufferedWriter(new FileWriter(path, false));
		} catch (IOException e) {
			System.out.println("File not created");
		}
	}
    
	private void convertPauses() throws IOException {
		Path pathToFile = Paths.get(path);
		Charset charset = StandardCharsets.UTF_8;

		String content;
		content = new String(Files.readAllBytes(pathToFile));
		
		CharacterIterator it = new StringCharacterIterator(content);
		while(it.current() != CharacterIterator.DONE && it.current() == ' ') {
			it.next();
		}
		int startIndex = it.getIndex();
		
		it.setIndex(it.getEndIndex() - 1);
		while(it.current() == ' ') {
			it.previous();
		}
		int endIndex = it.getIndex() + 1;
		
		content = content.substring(startIndex, endIndex);				
		content = content.replaceAll("   ", "|");
		Files.write(pathToFile, content.getBytes(charset));
	}	
	
	public void write(Vector<Character> input) {
		try {
		if(input.size() == 1) {
			writer.append(input.elementAt(0));
		}
		else if(!input.isEmpty()) {
			writer.append('[');
			input.forEach((e)->{
				try {
					writer.append(e);
				} catch (IOException e1) {System.out.println("Error write -> TXTConverter in lamba");}
			});
			writer.append(']');
		}
		else {
			writer.append(' ');
		}
		}catch(Exception e) {System.out.println("Error write -> TXTConverter");}
	}
	
	public void close() {
		try {
			writer.close();
			convertPauses();
			System.out.println("Txt file saved");
		} catch (IOException e1) {
			System.out.println("Baga");
			//e1.printStackTrace();
		}		
	}
	
	public static void main(String[] args) {

	}
}
