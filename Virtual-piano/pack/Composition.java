package pack;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextField;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.standard.DialogOwner;

/**
 * This class contains composition text as well as converted music simbols
 * @author Ognjen Subaric
 *
 */
public class Composition {
	private CompositionText inputComp;
	private NoteMap map;
	private List<String> keysList;
	private List<ArrayList<Symbol>> descriptionsList;
	private List<Boolean> durationList;	//Duration list isn't needed becouse its already integrated in symbols in descriptionsList

	public Composition(CompositionText txt_, NoteMap map_) {
		inputComp = txt_;
		map = map_;
		keysList = new ArrayList<String>(); //its list of strings so we can store multiple keys that are played at the same time
		descriptionsList = new ArrayList<ArrayList<Symbol>>();	//all note descriptions will be separated with ",". Use String[] array = String.split(",").
		durationList = new ArrayList<Boolean>();	//true - 1/4 | false - 1/8 //this isn't needed, becouse there is already duration field in Symbol class
		
		fillLists();
	}
	
	public void printKeysList() {
		for(String s: keysList)
			System.out.print(s + " ");
	}
	
	public void printSimbols() {
		for(ArrayList<Symbol> ar : descriptionsList) {
			for(Symbol s : ar) {
				System.out.print(s + " ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Takes the imput file and fills keysList, descriptionsList and durationList
	 */
	private void fillLists() {
		//reading file using streams
		try {
			FileInputStream fis = new FileInputStream(inputComp.getFile());
			char sign;
			
			while(fis.available() > 0) {
				sign = (char) fis.read();
				switch(sign) {
				case '[':
					List<Character> lstChars = new ArrayList<Character>();
					while (fis.available() > 0) {
						sign = (char) fis.read();
						if (sign == ']')
							break;
						lstChars.add(sign);
					}
					
					//If it contains blank space, it means their duration is 1/8, and they are played 
					//one after the other
					if(lstChars.contains(' ')) {
						for(char c : lstChars) {
							if(c!=' ') {
								durationList.add(false);
								keysList.add("" + c);
								descriptionsList.add(new ArrayList<Symbol>());
								descriptionsList.get(descriptionsList.size() - 1).add(new Note(map.getNoteDescription(c), false));
							}
						}
					}
					//If it doesn't contain blank space, it means their duration is 1/4 and they are played at the
					//same time and all notes played at the same time are in one string
					else {
						durationList.add(true); //duration - 1/4, all at the same time
						String s = "";
						for(char c : lstChars) {
								s = s + c;
						}
						keysList.add(s);
						s = "";
						descriptionsList.add(new ArrayList<Symbol>());
						for(char c : lstChars) {
							descriptionsList.get(descriptionsList.size() - 1).add(new Note(map.getNoteDescription(c), true));
						}
					}
					break;
				case ' ':
					durationList.add(false);
					keysList.add("_"); //prilikom crtanja, ostavljamo prazno, samo je crtamo uze jer je 1/8
					descriptionsList.add(new ArrayList<Symbol>());
					descriptionsList.get(descriptionsList.size() - 1).add(new Pause(false));
					break;
				case '|':
					durationList.add(true);
					keysList.add("|"); //prilikom crtanja, ostavljamo prazno, crtamo je normalno jer je 1/4
					descriptionsList.add(new ArrayList<Symbol>());
					descriptionsList.get(descriptionsList.size() - 1).add(new Pause(true));
					break;
				default: //trazi notu i ubacuje je
					durationList.add(true);
					keysList.add("" + sign);
					descriptionsList.add(new ArrayList<Symbol>());
					descriptionsList.get(descriptionsList.size() - 1).add(new Note(map.getNoteDescription(sign), true));
				}
			}
			
			fis.close();
		} catch (IOException e) {
			System.out.println("Error, could not find the file.");
		}
	}
	
	
	
	public List<String> getKeysList() {
		return keysList;
	}

	public List<ArrayList<Symbol>> getDescriptionsList() {
		return descriptionsList;
	}

	public List<Boolean> getDurationList() {
		return durationList;
	}

	public static void main(String[] args) {
		Composition c = new Composition(new CompositionText("C:\\Users\\Ognjen\\Desktop\\Q.txt"), new NoteMap("C:\\Users\\Ognjen\\Desktop\\POOPzad\\map.csv"));
		c.printKeysList();
		System.out.println("\n_______________________________");
		c.printSimbols();
	}
}
