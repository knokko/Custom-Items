package nl.knokko.customitems;

import static nl.knokko.customitems.MCVersions.VERSION1_12;
import static nl.knokko.customitems.MCVersions.VERSION1_13;
import static nl.knokko.customitems.MCVersions.VERSION1_14;

import java.util.Locale;

public class NameHelper {
	
	public static String getNiceEnumName(String name) {
		
		// The charAt(0) and substring(1) would be out of bounds for an empty string
		if (name.isEmpty())
			return name;
		return name.charAt(0) + name.substring(1).toLowerCase(Locale.ROOT).replaceAll("_", " ");
	}
	
	public static String getNiceEnumName(String name, int mcVersion) {
		String niceName= getNiceEnumName(name);
		switch(mcVersion) {
		case VERSION1_12: return niceName;
		case VERSION1_13: return niceName + " (1.13+)";
		case VERSION1_14: return niceName + " (1.14+)";
		default: throw new Error("Unknown minecraft version: " + mcVersion);
		}
	}
}
