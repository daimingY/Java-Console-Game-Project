package hw4;

import java.io.Serializable;

public class Brawler implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private Stats stats;
	private Ability[] abilities;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Stats getStats() {
		return stats;
	}
	public void setStats(Stats stats) {
		this.stats = stats;
	}
	public Ability[] getAbilities() {
		return abilities;
	}
	public void setAbilities(Ability[] abilities) {
		this.abilities = abilities;
	}
}
