package pack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.util.ArrayList;

/**
 * This class represents one graphical field, graphical
 * presentation of composition text
 * @author Ognjen
 *
 */
public class ImgField {
	public static int quarter = 48;
	public static int eighth = 24;
	
	private int x;
	private int y;
	private int width;
	private int height;
	private Color color;
	private boolean duration; //true = 1/4 false = 1/8
	private String key;
	ArrayList<Symbol> description;
	private int fontHeight;
	private int fontWidth;
	
	public ImgField(int x_, int y_, boolean duration_, String key_,ArrayList<Symbol> description_, Panel p) {
		x = x_;
		y = y_;
		duration = duration_;
		key = key_;
		description = description_;
		if(duration) {
			color = Color.RED;
			width = quarter;
		}
		else {
			color = Color.GREEN;
			width = eighth;
		}
		fontHeight = p.getFontMetrics(p.getFont()).getHeight();
		fontWidth = p.getFontMetrics(p.getFont()).getWidths()[0] + 2;
		height = fontHeight * key.length();
	}
	
	/**
	 * 
	 * @param g Graphics on which is drawn
	 * @param mode True for key and false for note description
	 */
	public void draw(Graphics g, boolean mode) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
		
		g.setColor(Color.BLACK);
		int signX = x, signY = y;
		
		//I tryed different formulas for letter coordinates, and this two work the best
		signX += width / 2 - fontWidth / 2 - 1;
		for(int i = 0; i < key.length(); i++) {
			signY = (i + 1) * fontHeight + y - 5;
			if(mode)
				g.drawString(String.valueOf(key.charAt(i)), signX, signY);
			else
				g.drawString(description.get(i).getDescription(), signX - 6, signY);
		}
	}
	
	@Override
	public String toString() {
		return key;
	}
	
}
