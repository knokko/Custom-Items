package nl.knokko.customitems.effect;

public class PotionEffect {
	protected int duration;
	protected int level;
	protected EffectType effect;
	
	PotionEffect(EffectType effect, int duration, int level) {
		this.effect = effect;
		this.duration = duration;
		this.level = level;
	}
	
	public EffectType getEffect () {
		return this.effect;
	}
	
	public int getDuration () {
		return this.duration;
	}
	
	public int getLevel () {
		return this.level;
	}
	
	public void setLevel (int level) {
		this.level = level;
	}
	
	public void setDuration (int duration) {
		this.duration = duration;
	}
	
	public void setEffect (EffectType effect) {
		this.effect = effect;
	}
}
