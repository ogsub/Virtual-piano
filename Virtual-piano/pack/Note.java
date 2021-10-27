package pack;

public class Note extends Symbol {
	public enum Pitch {C, D, E, F, G, A, B};
	private Pitch pitch;
	private int octave;
	private boolean sharp;
	
	public Note(boolean dur_, Pitch pitch_, int octave_, boolean sharp_) {
		super(dur_);
		pitch = pitch_;
		octave = octave_;
		sharp = sharp_;
	}
	
	public Note(String description, boolean dur_) {
		super(dur_);
		switch(description.charAt(0)) {
		case 'C':
			pitch = Pitch.C;
			break;
		case 'D':
			pitch = Pitch.D;
			break;
		case 'E':
			pitch = Pitch.E;
			break;
		case 'F':
			pitch = Pitch.F;
			break;
		case 'G':
			pitch = Pitch.G;
			break;
		case 'A':
			pitch = Pitch.A;
			break;
		case 'B':
			pitch = Pitch.B;
			break;
		}
		if(description.length() == 3) {
			sharp = true;
			octave = (int) description.charAt(2) - 48;
		}
		else {
			sharp = false;
			octave = (int) description.charAt(1) - 48;
		}
	}
	
	

	public Pitch getPitch() {
		return pitch;
	}

	public int getOctave() {
		return octave;
	}

	public boolean isSharp() {
		return sharp;
	}

	@Override
	public String toString() {
		String s = "Trajanje:" + duration + " " + pitch;
		if(sharp)
			s = s + "#";
		s = s + octave;
		return s;
	}

	@Override
	public String getDescription() {
		String s = "" + pitch;
		if(sharp)
			s = s + "#";
		s = s + octave;
		return s;
	}
	
	
}
