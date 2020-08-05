package nl.knokko.customitems.container;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class IndicatorDomain {

	private static final int MAX = 100;
	
	public static IndicatorDomain load(BitInput input) {
		int begin = input.readInt();
		int end = input.readInt();
		return new IndicatorDomain(begin, end);
	}
	
	private final int begin;
	private final int end;
	
	public IndicatorDomain() {
		this(0, MAX);
	}
	
	public IndicatorDomain(int begin, int end) {
		this.begin = begin;
		this.end = end;
	}
	
	public void save(BitOutput output) {
		output.addInt(begin);
		output.addInt(end);
	}
	
	public int getBegin() {
		return begin;
	}
	
	public int getEnd() {
		return end;
	}
	
	public int getStacksize(int currentProgress, int maxProgress) {
		int scaledBegin = begin * maxProgress / MAX;
		int scaledEnd = end * maxProgress / MAX;
		
		int computedResult = (currentProgress - scaledBegin) * 64 / (scaledEnd - scaledBegin);
		
		if (computedResult < 0) {
			return 0;
		}
		if (computedResult > 64) {
			return 64;
		}
		
		return computedResult;
	}
}
