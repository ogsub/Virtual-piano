package pack;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.sound.midi.MidiChannel;

/**
 * Key class presents one key on piano
 * @author Ognjen
 *
 */
public class Key extends Rectangle {
	private int midiNumb;
	private boolean whiteKey;
	private boolean keyOn; //default value for boolean is false(for Boolean is null). True if key is pressed
	private String keyboard;
	private boolean autoPlay;

	/**
	 * Constructor for Key class
	 * @param x_		Position x on the panel
	 * @param y_		Position y on the panel
	 * @param midiNumb_	MidiNumber of key
	 * @param keyboard_	Keybord key that correspond to piano key
	 * @param whiteKey_	True for white key and false for black
	 */
	public Key(int x_, int y_, int midiNumb_, String keyboard_, boolean whiteKey_, int width, int height) {
		super(x_, y_, width, height);
		midiNumb = midiNumb_;
		whiteKey = whiteKey_;
		keyboard = keyboard_;
	}

	public int getMidiNumber() {
		return midiNumb;
	}
	
	public void drawStringRect(Graphics g) {
		if (whiteKey) {
			g.setColor(Color.BLACK); 			//set outline colour
			g.drawRect(x, y, width, height);
			g.setColor(Color.WHITE); 			//set fill colour
			g.fillRect(x+1, y+1, width-2, height-2);
			if (keyOn) {
				if(!autoPlay)
					g.setColor(Color.DARK_GRAY);	//set fill colour
				else
					g.setColor(Color.RED);
				g.fillRect(x, y, width, height);
			}
		} else {
			g.setColor(Color.BLACK); 			//set outline colour
			g.drawRect(x, y, width, height);
			g.setColor(Color.BLACK); 			//set fill colour
			g.fillRect(x+1, y+1, width-1, height-1);
			if (keyOn) {
				if(!autoPlay)
					g.setColor(Color.BLUE);			//set fill colour
				else
					g.setColor(Color.RED);
				g.fillRect(x, y, width, height);
			}
		}

		FontMetrics fm = g.getFontMetrics();

		Color stringColor;
		int startX;
		int startY;
		if (whiteKey) {
			stringColor = Color.BLACK;
			startX = x + ((width - fm.stringWidth(keyboard)) / 2);
			startY = y + ((height + fm.getHeight()) / 2 + 70);
		}
		else {
			stringColor = Color.WHITE;
			startX = x + ((width - fm.stringWidth(keyboard)) / 2);
			startY = y + ((height + fm.getHeight()) / 2 + 30);
		}
		g.setColor(stringColor);

		
		g.drawString(keyboard, startX, startY);
	}

	public boolean getKeyOn() {
		return keyOn;
	}

	/**
	 * Sets key on or off
	 * @param mode_ True for setting the key on, and false for setting it off
	 */
	public void setKey(boolean mode_) {
		keyOn = mode_;
	}
	
	public void setAutoPlay(Boolean mode) {
		autoPlay = mode;
	}
	
	public void play(MidiChannel channel_, final long length) {
		play(channel_);
		try {
			Thread.sleep(length);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		release(channel_);
	}
	
	public void play(MidiChannel channel_) {
		channel_.noteOn(midiNumb, 50);
	}

	public void release(MidiChannel channel_) {
		channel_.noteOff(midiNumb, 50);
	}
	
	public boolean getWhite() {
		return whiteKey;
	}
	
	public char getKeyboard() {
		return keyboard.charAt(0);
	}
}
