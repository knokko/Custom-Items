package nl.knokko.customitems.container;

public class IndicatorDomain {

	private static final int MAX = 100;
	
	private final int begin;
	private final int end;
	
	public IndicatorDomain() {
		this(0, MAX);
	}
	
	public IndicatorDomain(int begin, int end) {
		this.begin = begin;
		this.end = end;
	}
	
	public int getBegin() {
		return begin;
	}
	
	public int getEnd() {
		return end;
	}
}
