package pack;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class GraphicsComposition extends Panel {
	private Vector<ImgField> imgFieldsKeys;
	private int x = 20, y = 20;
	private int numSymulNotes = 0; //number of simultanious notes, used while drawing
	private Checkbox lettersCb;
	private Checkbox descriptionCb;
	private boolean graphicsMode = true;
	private List<String> keysList;
	private List<ArrayList<Symbol>> descriptionsList;
	private List<Boolean> durationList;
	
	public GraphicsComposition(List<String> keysList_, List<ArrayList<Symbol>> descriptionsList_, List<Boolean> durationList_) {//List<String> keysList, List<ArrayList<Symbol>> descriptionsList) {
		setLayout(null);
		keysList = keysList_;
		descriptionsList = descriptionsList_;
		setSize(1920, 340);

		setBackground(Color.DARK_GRAY);
		imgFieldsKeys = new Vector<ImgField>();
		setFont(new Font("Arial", Font.PLAIN, 15));
		CheckboxGroup grp = new CheckboxGroup();
		lettersCb = new Checkbox("Letters", true, grp);
		descriptionCb = new Checkbox("Description", false, grp);
		durationList = durationList_;
		
		lettersCb.addItemListener((e)->{
			if(e.getStateChange() == e.SELECTED)
				graphicsMode = true;
			repaint();
		});
		
		descriptionCb.addItemListener((e)->{
			if(e.getStateChange() == e.SELECTED)
				graphicsMode = false;
			repaint();
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		lettersCb.setBounds(740, 130, 70, 30);
		add(lettersCb, gbc);
		
		descriptionCb.setBounds(820, 130, 90, 30);
		add(descriptionCb, gbc);
		repaint();
		validate();
		
		setSize(1920, 1040);
	}
	
	public synchronized GraphicsComposition fillImgFields() {
		if(keysList.isEmpty()) {
			imgFieldsKeys.clear();
			return this;
		}
		Iterator<String> it1 = keysList.iterator();	
		Iterator<ArrayList<Symbol>> it2 = descriptionsList.iterator();
		imgFieldsKeys.clear();
		x = y = 20;
		while(it1.hasNext() && it2.hasNext()) {
			String currKey = it1.next();
			if(currKey.length() > numSymulNotes)
				numSymulNotes = currKey.length();
			ArrayList<Symbol> currDescription = it2.next();
			imgFieldsKeys.add(new ImgField(x, y, currDescription.get(0).duration.getDuration(), currKey, currDescription, this));
			if(currDescription.get(0).duration.getDuration())
				x += ImgField.quarter;
			else
				x += ImgField.eighth;
		}
		return this;
	}
	
	@Override
	public void paint(Graphics g) {
		imgFieldsKeys.forEach((e)->{
			e.draw(g, graphicsMode);
		});
		x = 20;
		y = numSymulNotes * (getFontMetrics(getFont()).getHeight() + 3) + 20; //here is why we needed numSymulNotes
		g.setColor(Color.BLACK);
		
		//drawing tacts
		for(int i = 0; i < imgFieldsKeys.size(); i++) {
			g.drawLine(x, y, x, y + 20);
			x += ImgField.quarter;
		}
	}
	
	public void shiftLeft() {
		if(!keysList.isEmpty()) {
			keysList.remove(0);
			descriptionsList.remove(0);
			durationList.remove(0);
		
		fillImgFields();
		repaint();
		}
	}
	
	public static void main(String[] args) {
		Frame f = new Frame();
		f.setSize(35 * 40 + 100, 60 + 200);
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				f.dispose();
			}
		});
		Composition c = new Composition(new CompositionText("C:\\Users\\Ognjen\\Desktop\\Q.txt"), new NoteMap("C:\\Users\\Ognjen\\Desktop\\POOPzad\\map.csv"));
		GraphicsComposition gc = new GraphicsComposition(c.getKeysList(), c.getDescriptionsList(), c.getDurationList());
		f.add(gc.fillImgFields());
		f.setVisible(true);
		for(int i = 0; i < 5; i++) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			gc.shiftLeft();
		}
	}
}
