package pack;

public class Pause extends Symbol {

	public Pause(boolean dur_) {
		super(dur_);
	}

	@Override
	public String toString() {
		if(duration.getDuration())
			return "1/4";
		return "1/8";
	}
	
	@Override
	public String getDescription() {
		return toString();
	}
	
}
