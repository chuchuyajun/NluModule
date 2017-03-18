package nlu.conference;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import config.Config;

/**
 * 根据wordList和DomainPattern生成pattern
 * 
 * @author TCL
 *
 */
public class PatternGen {
	public HashMap<String, ArrayList<String>> wordList;

	public PatternGen() {
		wordList = new HashMap<String, ArrayList<String>>();
		try {
			System.out.println(Config.get("wordList"));
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(Config.get("wordList")), "UTF-8"));
			String str = "";

			while ((str = br.readLine()) != null) {
				String[] words = str.split(" ");

				String word = words[0].substring(1, words[0].length() - 1);
				if (wordList.containsKey(word)) {
					wordList.get(word).add(words[1]);
				} else {
					ArrayList<String> list = new ArrayList<String>();
					list.add(words[1]);
					wordList.put(word, list);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 根据domainmodel 生成 pattern
	public void generate() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(Config.get("domainModel")), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(Config.get("patternModel")), "UTF-8"));

			String tmp = "";
			while ((tmp = br.readLine()) != null) {
				if (tmp.trim().equals(""))
					continue;
				System.out.println(tmp);
				String split[] = tmp.split("\t");
				String pattern = split[0];
				String target = split[1];
				int level = new Integer(split[2]);
				String action = "";
				String sort = "";
				String theme_type = "";
				if (split.length >= 4)
					action = split[3];
				if (split.length >= 5)
					sort = split[4];
				if (split.length >= 6)
					theme_type = split[5];

				// 替换condition(target)
				String type = "";
				ArrayList<String> replaceList = new ArrayList<String>();

				while (pattern.contains("<condition&")) {
					type = pattern.substring(pattern.indexOf("<condition&")
							+ "<condition&".length(), pattern.indexOf(">",
							pattern.indexOf("<condition&")));
					replaceList = wordList.get(type);
					if (replaceList != null && replaceList.size() != 0) {
						String replace = merge(replaceList);
						pattern = pattern.replaceAll(
								"<condition&" + type + ">", replace);
					}
				}
				pw.println(pattern + "\t" + target + "\t" + level + "\t"
						+ action + "\t" + sort + "\t" + theme_type);
				pw.flush();
			}

			br.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String merge(ArrayList<String> replaceList) {
		String ret = "(";
		for (String r : replaceList) {
			ret += r + "|";
		}
		ret = ret.substring(0, ret.length() - 1) + ")";
		return ret;
	}

	public static void main(String args[]) {
		PatternGen gen = new PatternGen();
		gen.generate();
	}
}
