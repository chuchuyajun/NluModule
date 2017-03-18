package nlu.conference;

/**
 * 
 * @author z-zhang
 *
 */
public class NewPattern {
	public String pattern;
	public String target;
	public int level;
	public String action;
	public String sort;
	public String theme_type;

	public NewPattern(String pattern, String target, int level, String action,
			String theme_type) {
		this.pattern = pattern;
		this.target = target;
		this.level = level;
		this.action = action;
		this.theme_type = theme_type;
	}

	public NewPattern(String input) {
		System.out.println("input: " + input);
		String split[] = input.split("\t");
		System.out.println("input split: " + input);
		pattern = split[0];
		target = split[1];
		level = new Integer(split[2]);
		if (split.length >= 4 && !split[3].equals("null"))
			action = split[3];
		else
			action = "";
		if (split.length >= 5 && !split[4].equals("null")) {
			sort = split[4];
		} else
			sort = "";
		if (split.length >= 6)
			theme_type = split[5];
		else
			theme_type = "";
	}
}
