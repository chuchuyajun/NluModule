package nlu.conference;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import config.Config;

public class FuzzyDate {
	private static Calendar cal;

	public String pattern;

	public String base;

	public int day;

	public int offset;
	
	private static ArrayList<ArrayList<String>> patterns;
	
	public static void init() {
		patterns = new ArrayList<ArrayList<String>>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(Config.get("fuzzypattern")), "UTF-8"));
			String tmp = "";
			while ((tmp = br.readLine()) != null) {
				String[] slist = tmp.split("\t");
				ArrayList<String> tmplist = new ArrayList<String>();
				for (int i = 0; i < slist.length; i ++) {
					tmplist.add(slist[i]);
				}
				patterns.add(tmplist);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void update() {
		cal = Calendar.getInstance();	
	}

	public FuzzyDate(String line) {
		String[] list = line.split("\t");
		if (list.length < 3) {
			System.out.println("error fuzzy date: " + line);
		}
		pattern = list[0];
		base = list[1];
		offset = Integer.valueOf(list[2]);
		if (base.equals("week")) {
			offset = offset - getWeekDay();
		}
		day = getDay() + offset;
		if (day > 31) {
			// TODO:
		}
	}
	
	public static String fuzzyDateProcess(String input) {
		init();
		update();
		for (int i = 0; i < patterns.size(); i ++) {
			ArrayList<String> pattern = patterns.get(i);
			String p = pattern.get(0);
			if (input.contains(p)) {
				int start = input.indexOf(p);
				int end = start + p.length();
				String type = pattern.get(1);
				int offset = Integer.valueOf(pattern.get(2));
				int month = getMonth(), day = getDay();
				if (type.equals("day")) {
					day += offset;
					if (day > 28) {
						if (month == 2) {
							month += 1;
							day -= 28;
						} else {
							if (day > 30) {
								if (month == 6 || month == 4 || month == 9 || month == 11) {
									month += 1;
									day -= 30;
								} else {
									if (day > 31) {
										month += 1;
										day -= 31;
									}
								}
							}
						}
					}
				} else if (type.equals("month")) {
					month += offset;
					day = 1;
				} else if (type.equals("weekday")) {
					offset = offset - getWeekDay() + 7;
					day += offset;
					if (day > 28) {
						if (month == 2) {
							month += 1;
							day -= 28;
						} else {
							if (day > 30) {
								if (month == 6 || month == 4 || month == 9 || month == 11) {
									month += 1;
									day -= 30;
								} else {
									if (day > 31) {
										month += 1;
										day -= 31;
									}
								}
							}
						}
					}
				} else if (type.equals("week")) {
					offset = 7 + 1 - getWeekDay();
					day += offset;
					if (day > 28) {
						if (month == 2) {
							month += 1;
							day -= 28;
						} else {
							if (day > 30) {
								if (month == 6 || month == 4 || month == 9 || month == 11) {
									month += 1;
									day -= 30;
								} else {
									if (day > 31) {
										month += 1;
										day -= 31;
									}
								}
							}
						}
					}
				}
				// replace the fuzzy date representation with formal ones
				String prefix = input.substring(0, start);
				String postfix = input.substring(end, input.length());
				input = prefix + month + "月" + day + "日" + postfix;
			}
			
		}
		System.out.println(getMonth() + ";" + getWeekDay() + ";" + getDay());
		return input;
	}

	/**
	 * 得到今天的日期
	 * 
	 * @return
	 */
	public static int getDay() {
		update();
		return cal.get(Calendar.DATE);
	}

	/**
	 * 得到今天的星期
	 * 
	 * @return
	 */
	public static int getWeekDay() {
		update();
		int tmp = cal.get(Calendar.DAY_OF_WEEK);
		tmp -= 1;
		if (tmp <= 0) {
			tmp = 7;
		}
		return tmp;
	}

	/**
	 * 得到今天的月数
	 * 
	 * @return
	 */
	public static int getMonth() {
		update();
		return cal.get(Calendar.MONTH) + 1;
	}

	public static int getYear() {
		update();
		return cal.get(Calendar.YEAR);
	}
}
