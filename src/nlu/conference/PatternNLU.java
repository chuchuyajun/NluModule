package nlu.conference;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;





//import beans.NewPattern;
import config.Config;
import core.NLUUnit;

//排序
class PatternSort implements Comparator<NewPattern> {
	@Override
	public int compare(NewPattern p1, NewPattern p2) {
		if (p1.level < p2.level)
			return 1;
		else if (p1.level > p2.level)
			return -1;
		else
			return 0;
	}
}

// 保存中间结果的类
class PatternMap {
	public int length;
	public HashMap<String, String> map;

	public PatternMap(int len, HashMap<String, String> map) {
		this.length = len;
		this.map = map;
	}
}

/**
 * 会议领域语义理解系统 输入自然语言，输出HashMap
 * 
 * @author z-zhang
 *
 */
public class PatternNLU implements NLUUnit {
	// 存储pattern
	private static List<NewPattern> patternList;

	private HashMap<String, String> wordList;
	
	private static HashMap<String, String> alias;
	
	private static ArrayList<String> names;
	
	private static ArrayList<String> fuzzywords;
	
	private static HashMap<String, String> aliasdic;
	
	private static Map<String, String> dict;

	// 构造函数，初始化
	public PatternNLU(String patternStr, Map<String, String> worddict) {
		
		dict = worddict;
//		patternList = PatternDAO.getInstance().getCombine();
//		Collections.sort(patternList, new PatternSort());
		
		if (patternList == null) {
			patternList = new ArrayList<NewPattern>();
			String[] slist = patternStr.split("\n");
			for (int i = 0; i < slist.length; i ++) {
				String tmp = slist[i];
				if (tmp == null || tmp.trim().length() <= 0) {
					continue;
				} else {
					patternList.add(new NewPattern(tmp.trim()));
				}
			}
			Collections.sort(patternList, new PatternSort());
		}

		wordList = new HashMap<String, String>();
		try {
			BufferedReader wordInput = new BufferedReader(
					new InputStreamReader(new FileInputStream(
							Config.get("wordList")), "UTF-8"));
			String str;
			while ((str = wordInput.readLine()) != null) {
				String[] words = str.split(" ");
				String word = words[0].substring(1, words[0].length() - 1);
				wordList.put(words[1], word);
			}
			wordInput.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private ArrayList<HashMap<String, String>> innerProcess(String inputStr) {
		// 记录多个匹配
		ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
		if (ret.size() != 0)
			return ret;

		// 名词实体识别，2^n
		ArrayList<String> inputList = GetEntityAuto.getEntityList(inputStr, dict);
		int best = 0;
		// 遍历每一种可能性
		for (int i = inputList.size() - 1; i >= 0; i--) {
			String input = inputList.get(i);
			String condition = "";
			if (input.contains("\tcondition:")) {
				condition = input
						.substring(
								input.indexOf("\tcondition:")
										+ "\tcondition:".length()).trim();
				input = input.substring(0, input.indexOf("\tcondition:"));
			}
			// 遍历pattern
			for (NewPattern pattern : patternList) {
				// System.out.println(pattern.pattern + "\t" + pattern.level);
				Pattern p = Pattern.compile(pattern.pattern, Pattern.DOTALL);
				Matcher m = p.matcher(input);

				// 匹配
				if (m.matches()) {
					System.out.println("find pattern : " + pattern.pattern);
					// System.out.println(pattern.pattern);
					System.out.println(m.group());
					PatternMap pMap = restorePattern(input, m.group(),
							condition, m.start());
					int current = pMap.length;
					if (current > best) {
						best = current;
//						ret.clear();

						HashMap<String, String> map = pMap.map;
						// map.put("cqatrans", inputList.get(i));
						map.put("input", inputStr);
						map.put("target", pattern.target);
						map.put("level", Integer.toString(pattern.level));
						map.put("pattern", pattern.pattern);
						map.put("act_type", pattern.target);

						if (!pattern.action.equals("")) {
							map.put("action", pattern.action);
						}
						if (!pattern.sort.equals("")) {
							map.put("sort", pattern.sort);
						}
						if (!pattern.theme_type.equals(""))
							map.put("theme_type", pattern.theme_type);
						if (!ret.contains(map)) {
							int size = ret.size();
							;
							for (int x = 0; x < size; x++) {
								if (pattern.level > new Integer(ret.get(x).get(
										"level"))) {
									ret.add(x, map);
								}
							}
							if (size == ret.size())
								ret.add(map);
						}
					} else if (current == best) {
						HashMap<String, String> map = pMap.map;
						// map.put("cqatrans", inputList.get(i));
						map.put("input", inputStr);
						map.put("target", pattern.target);
						map.put("level", Integer.toString(pattern.level));

						if (!pattern.action.equals("")) {
							map.put("action", pattern.action);
						}
						if (!pattern.sort.equals("")) {
							// map.put("sort", pattern.sort);
							// 判断sort有没有区间[m,n]
							String sort = pattern.sort;
							if (sort.contains("[") && sort.contains("]"))
								map.put("sort", pattern.sort);
							else {

							}
						}
						if (!pattern.theme_type.equals(""))
							map.put("theme_type", pattern.theme_type);
						if (!ret.contains(map)) {
							int size = ret.size();
							;
							for (int x = 0; x < size; x++) {
								if (pattern.level > new Integer(ret.get(x).get(
										"level"))) {
									ret.add(x, map);
								}
							}
							if (size == ret.size())
								ret.add(map);
						}
					}
				}
			}
		}

		if (ret.size() != 0) {
			for (int i = 0; i < ret.size(); i++) {
				HashMap<String, String> tmap = ret.get(i);
				if (tmap.containsKey("level")) {
					tmap.remove("level");
				}
			}
			return ret;
		}
		return null;
	}

	@Override
	public ArrayList<HashMap<String, String>> process(String inputStr) {

		ArrayList<HashMap<String,String>> result = null;
		// 去除两端空格
		inputStr = inputStr.trim();
		// 去除引号和书名号
		inputStr = inputStr.replaceAll("\"|“|”|《|》", "");

		String oriinput = inputStr;

		if (innerProcess(inputStr) != null) {
			result = innerProcess(inputStr);
		}

		inputStr = getNewInput(inputStr);

		if (innerProcess(inputStr) != null) {
			result = innerProcess(inputStr);
		}
		
		
		
		return result;
	}
	
	/**
	 * 返回json格式的结果
	 * @param inputStr
	 * @return json字符串
	 */
	public String processJson(String inputStr) {
		String prefix = "{\"patternlist\":[";
		ArrayList<HashMap<String, String>> result = this.process(inputStr);
		for (HashMap<String, String> map : result) {
			prefix += "{";
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey(), value = entry.getValue();
				prefix += "\"" + key + "\": " + "\"" + value + "\",";
			}
			prefix += "},";
		}
		prefix = prefix.substring(0, prefix.length()-1);
		String postfix = "]}";
		prefix += postfix;
		return prefix;
	}
	
	class Word {
		int start;
		int end;
		String word;
		Word(int s, int e, String w) {
			start = s;
			end = e;
			word = w;
		}
	}
	
	class WordSort implements Comparator<Word> {
		@Override
		public int compare(Word o1, Word o2) {
			if (o1.start < o2.start) {
				return -1;
			} else if (o1.start > o2.start) {
				return 1;
			} else {
				if (o1.word.length() > o2.word.length()) {
					return -1;
				} else if (o1.word.length() < o2.word.length()) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
	
	/**
	 * 利用关键词（“月初”、“月末”等进行模糊日期匹配）
	 * @param result
	 * @return
	 */
	private ArrayList<HashMap<String, String>> fuzzyDateProcess(ArrayList<HashMap<String, String>> result) {
		if (result.size() == 1) {
			HashMap<String, String> map = result.get(0);
			String input = map.get("input");
			if (input == null) {
				return result;
			}
			if (!map.containsKey("date")) {
				ArrayList<Word> targets = new ArrayList<Word>();
				// 构建targets
				for (int i = 0; i < fuzzywords.size(); i ++) {
					String fw = fuzzywords.get(i);
					if (input.contains(fw)) {
						Word word = new Word(input.indexOf(fw), input.indexOf(fw) + fw.length(), fw);
						targets.add(word);
					}
				}
				if (targets.size() == 0) {
					return result;
				}
				Collections.sort(targets, new WordSort());
				// 清理targets
				for (int i = 0; i < targets.size(); i ++) {
					int s = targets.get(i).start, e = targets.get(i).end;
					for (int j = i + 1; j < targets.size(); j ++) {
						int _s = targets.get(j).start, _e = targets.get(j).end;
						if (_s >= s && _e <= e) {
							targets.remove(j);
							j --;
						}
					}
				}
				
				String fdate = null;
				for (int i = 0; i < targets.size(); i ++) {
					if (fdate == null) {
						fdate = targets.get(i).word;
					} else {
						if (targets.get(i).start - targets.get(i-1).end < 2) {
							fdate += targets.get(i).word;
						}
					}
				}
				result.get(0).put("fdate", fdate);
				
				return result;
			} else {
				return result;
			}
		} else {
			System.out.println("result size > 1!");
			return result;
		}
	}
	
	/**
	 * 处理酒店的别名问题，将自然语言里的酒店别名转化为正式名称
	 * @return
	 */
	private String alias2Name(String name) {
		System.out.println("alias start");
		initAlias();
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String current = (String) it.next();
			if (name.contains(current)) {
				if (current.trim().length() <=1) {
					continue;
				}
				String x = alias.get(current);
				name = name.replaceAll(current, x);
				break;
			}
		}
		if (name.contains("元一间")) {
			name = name.replaceAll("元一间", "元");
		}
		return name;
	}
	
	/**
	 * alia2Name的预处理函数，建立数据结构
	 */
	private void initAlias() {
		if (alias == null) {
			alias = new HashMap<String, String>();
			names = new ArrayList<String>();
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(
								Config.get("alias")), "UTF-8"));
				String tmp = "";
				while ((tmp = reader.readLine()) != null) {
					tmp = tmp.trim();
					//System.out.println(tmp);
					String[] list = tmp.split("\t");
					if (list.length <= 2) {
						continue;
					}
					String[] alialist = list[2].split(";");
					String x = list[1];
					for (int i = 0; i < alialist.length; i ++) {
						alias.put(alialist[i], x);
						names.add(alialist[i]);
					}
				}
				reader.close();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将中文数字替换为阿拉伯数字
	 * 
	 * @param input
	 * @return
	 */
	private String getNewInput(String input) {
		ArrayList<EntitySpan> numberspam = getNumberSpans(input);
		ArrayList<EntitySpan> valuespam = GetEntityAuto.getSpamListValue(input);
		for (int i = 0; i < numberspam.size(); i++) {
			EntitySpan es = numberspam.get(i);
			for (int j = 0; j < valuespam.size(); j++) {
				EntitySpan ess = valuespam.get(j);
				if (es.start >= ess.start && es.start < ess.end
						|| es.end > ess.start && es.end < ess.end) {
					numberspam.remove(i);
					i--;
					break;
				}
			}
		}
		return getNewInput(numberspam, input);
	}

	private String getNewInput(ArrayList<EntitySpan> numbers, String inputStr) {
		int offset = 0;
		for (int i = 0; i < numbers.size(); i++) {
			EntitySpan es = numbers.get(i);
			inputStr = inputStr.substring(0, es.start - offset) + es.word
					+ inputStr.substring(es.end - offset, inputStr.length());
			int tmp = (es.end - es.start) - es.word.length();
			es.start -= offset;
			es.end -= offset;
			es.end -= tmp;
			offset += tmp;
		}
		return inputStr;
	}

	private ArrayList<EntitySpan> getNumberSpans(String inputStr) {
		String[] digitCharacters = { "两", "零", "一", "二", "三", "四", "五", "六",
				"七", "八", "九", "十", "百", "千", "万", "亿", "1", "2", "3", "4",
				"5", "6", "7", "8", "9", "0" };
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		String[] numbers = { "两", "零", "一", "二", "三", "四", "五", "六", "七", "八",
				"九", "十", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		HashMap<String, Boolean> tmap = new HashMap<String, Boolean>();
		for (int i = 0; i < digitCharacters.length; i++) {
			map.put(digitCharacters[i], true);
		}
		for (int i = 0; i < numbers.length; i++) {
			tmap.put(numbers[i], true);
		}

		ArrayList<EntitySpan> value = new ArrayList<EntitySpan>();
		String tmp = "";
		boolean started = false;
		int start = -1, end = -1;
		for (int i = 0; i < inputStr.length() + 1; i++) {
			if (i == inputStr.length()) {
				if (started) {
					started = false;
					end = i;
					String d = DigitUtil.parseDigits(tmp) + "";
					EntitySpan es = new EntitySpan();
					es.type = "number";
					es.start = start;
					es.end = end;
					es.word = d;
					value.add(es);
				}
				break;
			}
			String c = inputStr.substring(i, i + 1);
			if (map.get(c) != null) {
				if (started) {
					tmp += c;
				} else {
					// 限定必须从中文数字开始
					if (tmap.get(c) == null) {
						continue;
					}
					started = true;
					start = i;
					tmp += c;
					if (c.equals("十")) {
						tmp = "一" + tmp;
					}
				}
			} else {
				if (started) {
					started = false;
					end = i;
					String d = DigitUtil.parseDigits(tmp) + "";
					EntitySpan es = new EntitySpan();
					es.type = "number";
					es.start = start;
					es.end = end;
					es.word = d;
					value.add(es);
					start = end = -1;
					tmp = "";
				} else {
					continue;
				}
			}
		}
		Collections.sort(value, new EntitySpanSort());
		return value;
	}

	// 还原pattern，如：singer的song -> 周杰伦的七里香
	private PatternMap restorePattern(String input, String match,
			String condition, int start) {
		int len = match.length();
		String conList[] = { "singer", "song", "style", "type", "album",
				"area", "language" };
		HashMap<String, String> map = new HashMap<String, String>();

		// 处理condition
		if (!condition.equals("")) {
			String split[] = condition.split(" \t");
			for (String unit : split) {
				if (unit.trim().equals(""))
					continue;
				String name = unit.substring(0, unit.indexOf(":"));
				String params = unit.substring(unit.indexOf(":") + 1);
				if (match.contains(name))
					map.put(name, params);
			}
		}

		for (String co : conList) {
			String tmpmatch = match;
			int index = getNum(input, co, start);
			String newMapContent = "";
			while (tmpmatch.contains(co)) {
				newMapContent += map.get(co).split(",")[index] + ",";
				int tmplen = TransToId.idTrans(map.get(co).split(",")[index],
						co).length();
				len = len - co.length() + tmplen;
				tmpmatch = tmpmatch.substring(tmpmatch.indexOf(co)
						+ co.length());
			}
			if (newMapContent.endsWith(","))
				newMapContent = newMapContent.substring(0,
						newMapContent.length() - 1);
			if (!newMapContent.equals(""))
				map.put(co, newMapContent);
		}
		return new PatternMap(len, map);
	}

	// start之前有多少相同的type
	private int getNum(String input, String type, int start) {
		int count = 0;
		input = input.substring(0, start);
		while (input.contains(type)) {
			count++;
			input = input.substring(input.indexOf(type) + type.length());
		}
		return count;
	}

	private String getTarget(String input) {
		if (wordList.containsKey(input)) {
			return wordList.get(input);
		} else
			return null;
	}

	// Target匹配
	@SuppressWarnings("unused")
	private String findTarget(String input) {
		if (input.trim().equals(""))
			return input;
		String target = "";
		String lastWord = "";

		String splitedSen = MySplit.testICTCLAS_ParagraphProcess(input, 1, 0);
		// 从splitedSen中提取关键词
		String[] words = splitedSen.split(" ");

		for (String word : words) {
			String[] pair = word.split("/");

			if (pair.length < 2)
				continue;
			if (pair[1].startsWith("T")) {
				String getTar = getTarget(pair[0]);
				if (getTar != null) {
					target = getTar;
					lastWord = pair[0];
				}
			}
		}
		return target + "\t\t" + lastWord;
	}

	public static void main(String args[]) {
		String patternStr = "";
		HashMap<String, String> map = new HashMap<String, String>();
		PatternNLU nlu = new PatternNLU(patternStr, map);

		ArrayList<String> inputList = new ArrayList<String>();
		String input = "你好么";
		inputList.add(input);
		// input = "他的歌";
		// inputList.add(input);
		// input = "周杰伦的叶惠美有什么歌";
		// inputList.add(input);

		for (String in : inputList) {
			System.out.println(nlu.process(in));
		}
	}

}
