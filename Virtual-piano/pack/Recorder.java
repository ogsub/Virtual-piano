package pack;

//public class Recorder {
//
//}

import javax.sound.midi.*;

import java.awt.Panel;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Recorder {
	public static int NOTEON = 144;
	public static int NOTEOFF = 128;
	
	private Sequencer sequencer;
	private Sequence sequence;
	private Track track;

	public Recorder() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequence = new Sequence(Sequence.PPQ, 4);
		} catch (MidiUnavailableException | InvalidMidiDataException e) {
			System.out.println("Error while making sequncer or sequence");
		}
		track = sequence.createTrack();
	}

	/**
	 * Method that adds note to track
	 * 
	 * @param command  Note on/off
	 * @param note     MidiNumber for note
	 * @param velocity don't know whats this for
	 * @param tick     Time
	 */
	public void addNote(int command, int note, long tick) {
		// Adding some events to the track
		// Add Note On event
		track.add(makeEvent(command, 1, note, 100, tick));
	}

	public void play() {
		// Setting our sequence so that the sequencer can
		// run it on synthesizer
		try {
			sequencer.open();
			sequencer.setSequence(sequence);
		} catch (InvalidMidiDataException | MidiUnavailableException e) {
			e.printStackTrace();
		}

		// Specifies the beat rate in beats per minute.
		sequencer.setTempoInBPM(220);

		// Sequencer starts to play notes
		sequencer.start();

		while (true) {

			// Exit the program when sequencer has stopped playing.
			if (!sequencer.isRunning()) {
				sequencer.close();
				break;
				//System.exit(1);
			}
		}
	}

	public MidiEvent makeEvent(int command, int channel, int note, int velocity, long tick) {

		MidiEvent event = null;

		try {

			// ShortMessage stores a note as command type, channel,
			// instrument it has to be played on and its speed.
			ShortMessage a = new ShortMessage();
			a.setMessage(command, channel, note, velocity);

			// A midi event is comprised of a short message(representing
			// a note) and the tick at which that note has to be played
			event = new MidiEvent(a, tick/100);//(long)((tick*BMP*Sequence.PPQ)/60000));
		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return event;
	}

	public void save(String path) throws IOException {
		File f = new File(path);
		System.out.println("File created");
		MidiSystem.write(sequence, 1, f);
	}

	public static void main(String[] args) {
		Recorder player = null;

		player = new Recorder();

		//player.setUpPlayer(numOfNotes);
		player.addNote(NOTEON, 63, 0);
		player.addNote(NOTEOFF, 63, 2000);
		player.addNote(NOTEON, 80, 4000);
		player.addNote(NOTEOFF, 80, 8000);
		player.addNote(NOTEON, 100, 8000);
		player.addNote(NOTEOFF, 100, 10000);
		player.addNote(NOTEON, 30, 10000);
		player.addNote(NOTEOFF, 30, 12000);
		player.addNote(NOTEON, 40, 12000);
		player.addNote(NOTEOFF, 40, 14000);
		
		player.play();
		try {
			player.save("C:\\Users\\Ognjen\\Desktop\\midifile.mid");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}