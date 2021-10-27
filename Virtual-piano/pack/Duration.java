package pack;

/**
 * This class represents the duration of the simbol. Since duration is only 1/4 or 1/8, 
 * boolean is used to tell which one it is, true - 1/4 false - 1/8 
 * @author Ognjen
 *
 */
public class Duration {
	private boolean duration;
	
	public Duration(boolean dur_) {
		duration = dur_;
	}
	
	public boolean getDuration() {
		return duration;
	}
	@Override
	public String toString() {
		if(duration)
			return "1/4";
		return "1/8";
	}
}
