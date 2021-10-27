package pack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class NoteMap {
	private LinkedHashMap<Character, NoteDescMidi> map;
	private Scanner scanner;
	
	public class NoteDescMidi {
		private String description;
		private int Midi;
		
		public NoteDescMidi(String s_, int Midi_) {
			description = s_;
			Midi = Midi_;
		}
		
		@Override
		public String toString() {
			return description + ", " + Midi;
		}		
	}
	
	/**
	 * Constructor that opens mapCSV, and fills internal HashMap called map used for translating CompositionText 
	 * @param pathname_ Is location of mapCSV 
	 */
	public NoteMap(String pathname_) {
		map = new LinkedHashMap<Character, NoteMap.NoteDescMidi>();
		File mapCSV = new File(pathname_);
		//reading files using scanner
		try {
			scanner = new Scanner(mapCSV);
		} catch (FileNotFoundException e) {
			System.out.println("Error, could not find the file.");
		}
		findPatterns("([0-9a-zA-Z]?[@$%^*(.!?-]?),([CDEFGAB]#?[0-9]),([0-9][0-9])");
	}
	
	/**
	 * It finds pattern from mapCSV scanner and fills HashMap<String, NoteDescMidi> map
	 * @param regex Its a regular expresion
	 */
	private void findPatterns(String regex) {
		//way to use regex
		Pattern p = Pattern.compile(regex);
		
		while(scanner.hasNextLine()) {
			Matcher m = p.matcher(scanner.nextLine());			
			while(m.find()) {
				map.put(m.group(1).charAt(0), new NoteDescMidi(m.group(2), Integer.parseInt(m.group(3))));
			}
		}
	}
	
	public LinkedHashMap<Character, NoteMap.NoteDescMidi> getHashMap(){ 
		return map;
	}
	
	public void printHashMap() {
		map.entrySet().forEach((e)->{
		    System.out.println(e.getKey() + " " + e.getValue());  
		 });
	}
	
	public String getNoteDescription(char key_) {
		char defaultKey = 'y';
		String s = "";
		try {
		s = map.get(key_).description;
		} catch(NullPointerException e) {
			s = map.get(defaultKey).description;
		}
		return  s;
	}
	
	public int getNoteMidi(char key_) {
		int midi;
		try {
			midi = map.get(key_).Midi;	
		} catch (Exception e) {
			midi = 63; //default value, if there is error in input file
		}
		
		return midi;
	}
	
	public static void main(String[] args) {
		NoteMap m = new NoteMap("C:\\Users\\Ognjen\\Desktop\\POOPzad\\map.csv");
		m.printHashMap();
	}
}
