package pack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import muzika.MidiPlayer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Piano extends Panel {
	public final static int width = 48;
	public final static int height = 240;
	public Vector<Key> keys;
	private NoteMap map;
	private Image dbImage; //doulbe buffer for eliminating flickering
	private Graphics dbGraphics;
	private Key currKey; // used for playing piano with mouse
	private MidiPlayer player;
	private boolean pressedKeysArr[] = new boolean[127];
	private ShiftingThread shiftThread = null;
	private Boolean recording = false;
	private Recorder recorder; 
	private boolean firstNoteRecording = true;
	private long startTime;	
	private TXTConverter txtConverter;


	public class ShiftingThread extends Thread {
		private List<String> lstKeys;
		private Vector<Character> buffer = new Vector<Character>();
		private GraphicsComposition gc;

		// thread for autoPlay
		public ShiftingThread(List<String> lstKeys_, GraphicsComposition gc_) {
			lstKeys = lstKeys_;
			gc = gc_;
			setDaemon(true);
			start();
		}

		public void addToBuffer(char c) {
			synchronized (buffer) {
				buffer.add(c);
			}
		}

		@Override
		public void run() {
			Runnable terminate = new Runnable() {
				@Override
				public void run() {
					//System.out.println("debug1");
					synchronized (buffer) {
						if(recording)
							txtConverter.write(buffer);
							
						AtomicBoolean same = new AtomicBoolean(true);
						try {
						if(buffer.size() == lstKeys.get(0).length()) {
							buffer.forEach((e) -> {
								if (lstKeys.get(0).indexOf(e) == -1) {
									//buffer.clear();
									same.set(false);
								}
							});
							if(!same.get())
								buffer.clear();
							
							//System.out.println("debug2");
							if (same.get() && !buffer.isEmpty())
								gc.shiftLeft();
						}
						}catch(Exception e){/*System.out.println("");*/}
						buffer.clear();
						//System.out.println("debug3");
					}
				}
			};

			ScheduledExecutorService timer2 = Executors.newSingleThreadScheduledExecutor();
			while (lstKeys.size() != 0) {
				ScheduledFuture<?> futureTask = timer2.schedule(terminate, 1000, TimeUnit.MILLISECONDS);
				try {
					futureTask.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				futureTask.cancel(true);
			}
		}

	}

	public void addShiftThread(List<String> lstKeys_, GraphicsComposition gc_) {
		shiftThread = new ShiftingThread(lstKeys_, gc_);
	}

	public ShiftingThread getShifThread() {
		return shiftThread;
	}

	public class AutoPlayThread extends Thread {
		private List<String> lstKeys;
		private List<Boolean> lstDur;
		private GraphicsComposition gc;

		// thread for autoPlay
		public AutoPlayThread(List<String> lstKeys_, List<Boolean> lstDur_, GraphicsComposition gc_) {
			lstKeys = lstKeys_;
			lstDur = lstDur_;
			gc = gc_;
			setDaemon(true);
			start();
		}

		@Override
		public void run() {
			while (!lstKeys.isEmpty()) {
				Boolean play = true;
				String s = lstKeys.get(0);
				Vector<Key> vctPianoKeys = new Vector<Key>();

				// adds all keys from this takt to vctPianoKeys
				for (char c : s.toCharArray()) {
					try {
						if (c == '|') {
							sleep(500);
							play = false;
						}
						if (c == '_') {
							sleep(250);
							play = false;
						}
					} catch (Exception e) {
					}
					Optional<Key> value = keys.stream().filter(a -> a.getMidiNumber() == map.getNoteMidi(c))
							.findFirst();
					vctPianoKeys.add(value.get());
				}

				if (play) {
					Vector<Thread> vctThread = new Vector<Thread>();
					for (Key k : vctPianoKeys) {
						if (lstDur.get(0))// i))
							vctThread.add(new PlayThread(k, 500, false));
						else
							vctThread.add(new PlayThread(k, 250, false));
					}
					vctPianoKeys.forEach((k) -> {
						k.setAutoPlay(true); //for color
					});
					vctThread.forEach((t) -> {
						t.start();
					});

					try {
					if(lstDur.get(0))
						Thread.sleep(500);
					else
						Thread.sleep(250);
					}catch (Exception e) {}
					
					vctPianoKeys.forEach((k) -> {	
						new PlayThreadReverse(k);
					});
					
					vctPianoKeys.forEach((k) -> {	
						k.setAutoPlay(false);
					});
				}
				try {
					if (lstDur.get(0)) // i))

						Thread.sleep(100);
					else
						Thread.sleep(50);
				} catch (Exception e) {
				}
				gc.shiftLeft();
			}
		}

	}

	public class PlayThread extends Thread {

		private Key k;
		private int sleepTime;

		public PlayThread(Key k_) {
			k = k_;
			sleepTime = 100;
			start();
		}

		// thread will be started by default if false value of boolean isnt passed
		public PlayThread(Key k_, int sleepTime_) {
			this(k_, sleepTime_, true);
		}

		public PlayThread(Key k_, int sleepTime_, Boolean start_) {
			k = k_;
			sleepTime = sleepTime_;
			if (start_)
				start();
		}

		@Override
		public void run() {
			turnOn(k);
		}

	}

	public class PlayThreadReverse extends Thread {
		private Key k;
		private int sleepTime;

		public PlayThreadReverse(Key k_) {
			k = k_;
			sleepTime = 100;
			start();
		}

		// thread will be started by default if false value of boolean isnt passed
		public PlayThreadReverse(Key k_, int sleepTime_) {
			this(k_, sleepTime_, true);
		}

		public PlayThreadReverse(Key k_, int sleepTime_, Boolean start_) {
			k = k_;
			sleepTime = sleepTime_;
			if (start_)
				start();
		}

		@Override
		public void run() {
			turnOff(k);
		}
	}

	public Piano(NoteMap map_) {
		try {
			player = new MidiPlayer();
		} catch (MidiUnavailableException e1) {
			e1.printStackTrace();
		}
		keys = new Vector<Key>();
		map = map_;

		// setSize(35 * width + 1, height + 1);
		setSize(1920, 540);
		setBackground(Color.DARK_GRAY);

		drawPiano();

		addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyTyped(KeyEvent e) {
//				// Thread p = new Thread((Runnable) this);
//				char c = e.getKeyChar();
//				System.out.println(c);
//				if (shiftThread != null)
//					shiftThread.addToBuffer(c);
//				keys.forEach((k) -> {
//					if (k.getKeyboard() == c) {
//						PlayThread p = new PlayThread(k);
//					}
//				});
//			}

			
			@Override
			public void keyPressed(KeyEvent e) {
				if(!pressedKeysArr[e.getKeyCode()]) {
						char c = e.getKeyChar();
						System.out.println(c);
						if (shiftThread != null) {
							if(((int)(c) >= 0 && (int)(c) <= 127))
								shiftThread.addToBuffer(c);
						}
						keys.forEach((k) -> {
							if (k.getKeyboard() == c) {
								PlayThread p = new PlayThread(k);
								
								if(recording) {
									if(firstNoteRecording) {
										startTime = System.currentTimeMillis();
										firstNoteRecording = false;
									}
									recorder.addNote(recorder.NOTEON, k.getMidiNumber(), System.currentTimeMillis() - startTime);
								}
								
							}
						});
					pressedKeysArr[e.getKeyCode()] = true;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
					char c = e.getKeyChar();
					keys.forEach((k) -> {
						if (k.getKeyboard() == c) {
							PlayThreadReverse p = new PlayThreadReverse(k);
							
							if(recording) {
								recorder.addNote(recorder.NOTEOFF, k.getMidiNumber(), System.currentTimeMillis() - startTime);
							}
							
						}
					});
				pressedKeysArr[e.getKeyCode()] = false;
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				mouseTurnOn(p);
			}

			private void mouseTurnOn(Point p_) {
				// Had to use AtomicBoolean wrapper becouse lambda doesn't allow non constant
				// values
				AtomicBoolean painted = new AtomicBoolean(false);
				keys.forEach((e) -> {
					if (e.contains(p_) && !e.getWhite()) {
						currKey = e;
						turnOn(e);
						painted.set(true);
					}
				});
				if (!painted.get()) {
					keys.forEach((e) -> {
						if (e.contains(p_)) {
							currKey = e;
							turnOn(e);
						}
					});
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (currKey != null)
					currKey.setKey(false);
				Point p = e.getPoint();
				mouseTurnOff(p);
			}

			private void mouseTurnOff(Point p_) {
				keys.forEach((e) -> {
					if (e.contains(p_)) {
						turnOff(e);
					}
				});
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (currKey != null)
					turnOff(currKey);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (currKey != null)
					turnOff(currKey);
			}

		});

	}

	public void setFirstNoteRecording(boolean mode) {
		firstNoteRecording = mode;
	}
	
	public boolean getRecording() {
		return recording;
	}

	public void startRecording(boolean value, String path) {
		recording = value;
		if (recording) {
			recorder = new Recorder();
			txtConverter = new TXTConverter(path);
		}
	}
	
	public void saveRecording(String path) {
		try {
			recording = false;
			recorder.save(path);
			txtConverter.close();
		} catch (IOException e) {
			System.out.println("Recording not saved");
		}
	}

	private void turnOn(Key k) {
		k.setKey(true);
		k.play(player.getMidiChannel());
		repaint();
	}

	private void turnOff(Key k) {
		k.setKey(false);
		repaint();
		k.release(player.getMidiChannel());
	}

	public void drawPiano() {
		int x = 20;
		int y = 20;

		Set<Entry<Character, NoteMap.NoteDescMidi>> s = map.getHashMap().entrySet();
		Iterator<Entry<Character, NoteMap.NoteDescMidi>> it = s.iterator();

		// drawing white keys
		Entry<Character, NoteMap.NoteDescMidi> elem = it.next();
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 7; j++) {
				keys.add(new Key(x, y, map.getNoteMidi(elem.getKey()), elem.getKey().toString(), true, width, height));
				if (it.hasNext())
					elem = it.next();
				if (j != 2 && j != 6)
					elem = it.next();
				x += width;
			}
		}

		// drawing black keys
		x = 20;
		y = 20;
		it = s.iterator();
		elem = it.next();
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (it.hasNext()) {
					elem = it.next();
					x += width;
				}
				keys.add(new Key(x - width / 4, y, map.getNoteMidi(elem.getKey()), elem.getKey().toString(), false,
						width / 2, height / 2));
				if (it.hasNext())
					elem = it.next();
				if (j == 1) {
					if (it.hasNext())
						elem = it.next();
					x += width;
				}
			}
			if (it.hasNext()) {
				elem = it.next();
				x += width;
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		dbImage = createImage(getWidth(), getHeight());
		dbGraphics = dbImage.getGraphics();
		paintComponent(dbGraphics);
		g.drawImage(dbImage, 0, 0, this);
	}

	/**
	 * To avoid delay and sloppy repainting I used double buffer
	 * 
	 * @param g
	 */
	public void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		keys.forEach(key -> {
			key.drawStringRect(g);
		});
	}

	public static void main(String[] args) {
		Frame f = new Frame();
		f.setLayout(new BorderLayout());
		f.setSize(35 * width + 100, height + 100);
		Piano p = new Piano(new NoteMap("C:\\Users\\Ognjen\\Desktop\\POOPzad\\map.csv"));
		f.add(p, BorderLayout.CENTER);
		f.setVisible(true);

		Composition c = new Composition(new CompositionText("C:\\Users\\Ognjen\\Desktop\\Q.txt"),
				new NoteMap("C:\\Users\\Ognjen\\Desktop\\POOPzad\\map.csv"));


		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				f.dispose();
			}
		});
	}

}
