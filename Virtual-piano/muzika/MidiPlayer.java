package muzika; 
 
import javax.sound.midi.MidiChannel; 
import javax.sound.midi.MidiSystem; 
import javax.sound.midi.MidiUnavailableException; 
import javax.sound.midi.Synthesizer; 
import java.util.Scanner; 
 
public class MidiPlayer {     
	private static final int DEFAULT_INSTRUMENT = 1;     
	private MidiChannel channel; 
 
    public MidiPlayer() throws MidiUnavailableException {         
    	this(DEFAULT_INSTRUMENT);     
    } 
 
    public MidiPlayer(int instrument) throws MidiUnavailableException {         
    	channel = getChannel(instrument);
    } 
 
    public MidiChannel getMidiChannel() {
    	return channel;
    }
    
//    public void play(final int note) {         
//    	channel.noteOn(note, 50);     
//    } 
// 
//    public void release(final int note) {         
//    	channel.noteOff(note, 50);     
//    } 
// 
//    public void play(final int note, final long length) throws InterruptedException {         
//    	play(note);         
//    	Thread.sleep(length);         
//    	release(note);     
//    }     
    private static MidiChannel getChannel(int instrument) throws MidiUnavailableException {         
    	Synthesizer synthesizer = MidiSystem.getSynthesizer();         
    	synthesizer.open();         
    	return synthesizer.getChannels()[instrument];     
    } 
 
//    public static void main(String[] args) throws Exception {         
//    	MidiPlayer player = new MidiPlayer();         
//    	Scanner scanner = new Scanner(System.in);         
//    	int note;         
//    	while (!Thread.currentThread().isInterrupted()) {             
//    		System.out.print("Note (1..127) : ");             
//    		note = scanner.nextInt();             
//    		player.play(note, 200);         
//    	}         
//    	scanner.close();     
//    } 
}