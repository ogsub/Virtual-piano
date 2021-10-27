package pack;

public abstract class Symbol {
	protected Duration duration;
	
	public Symbol(boolean dur_) {
		duration = new Duration(dur_);
	}
	
	@Override
	public abstract String toString();
	
	public abstract String getDescription();
}
