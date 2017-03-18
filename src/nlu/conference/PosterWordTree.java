package nlu.conference;

/**
 * Trie树
 * 用于名词实体识别
 * 
 * @author TCL
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PosterWordTree {
	public boolean isEnd;
	public HashMap<String, PosterWordTree> children;
	public String type;

	public String toString() {
		String s = this.tag;
		if (!isEnd)
			for (String ttag : children.keySet()) {
				s += "[" + children.get(ttag) + "]";
			}
		return s;
	}

	public String tag;

	PosterWordTree(boolean isEnd, HashMap<String, PosterWordTree> children) {
		this.isEnd = isEnd;
		this.children = children;
	}

	public PosterWordTree(Collection<String> words, String type) {
		this.type = type;
		this.isEnd = false;
		this.children = new HashMap<String, PosterWordTree>();
		PosterWordTree root = this;
		System.out.println("begin:" + type);
		int num = 0;
		int total = words.size();
		int per = 0;
		for (String word : words) {
			addWord(root, word, word);
			num++;
			int lastper = per;
			per = num * 100 / total;
			if (per > lastper)
				System.out.println(per + "%");
		}
	}

	public static void addWord(PosterWordTree t, String word, String origin) {
		if (word.length() == 0) {
			PosterWordTree leaf = new PosterWordTree(true, null);
			leaf.tag = origin;
			t.children.put("$end", leaf);
			return;
		}
		String tmp = getUnit(word, 0);
		if (t.children.containsKey(tmp.trim())) {
			addWord(t.children.get(tmp.trim()), word.substring(tmp.length()),
					origin);
		} else {
			PosterWordTree subt = new PosterWordTree(false,
					new HashMap<String, PosterWordTree>());
			subt.tag = tmp.trim();
			t.children.put(subt.tag, subt);
			addWord(subt, word.substring(tmp.length()), origin);
		}
	}

	private static String getUnit(String word, int index) {

		StringBuffer sb = new StringBuffer();
		boolean isEng = false;
		boolean isSpace = false;
		for (int i = index; i < word.length(); i++) {
			char c = word.charAt(i);
			// 是英文
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				sb.append(c);
				isEng = true;
			} else if ((c == ' ' || c == '\t') && !isEng) {
				isSpace = true;
				sb.append(c);
				continue;
			} else {
				break;
			}
		}
		if (isEng)
			return sb.toString();
		if (isSpace)
			return word.substring(index, index + 2);
		return word.substring(index, index + 1);
	}

	public static Set<String> genWords(String file) {
		Set<String> words = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "utf-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				/*
				 * if ((file.contains("song.txt") || file.contains("album")) &&
				 * line.length() > 1) words.add(line.trim().toLowerCase()); else
				 * if (!(file.contains("song.txt") || file.contains("album"))){
				 * words.add(line.trim().toLowerCase()); }
				 */
				words.add(line.trim().toLowerCase());
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return words;
	}
	
	public static Set<String> genWords(ArrayList<String> words0) {
		Set<String> words = new HashSet<String>();
		for (String line : words0) {
			words.add(line.trim().toLowerCase());
		}
		return words;
	}

	public static String find(String s, int index, PosterWordTree t) {
		String best = null;
		if (t.children.containsKey("$end"))
			best = t.children.get("$end").tag;
		if (index == s.length()) {
			return best;
		} else {
			String tag = getUnit(s, index);
			if (!t.children.containsKey(tag.trim()))
				return best;
			else {
				String next = find(s, index + tag.length(),
						t.children.get(tag.trim()));
				if (next != null)
					best = next;
			}
			return best;
		}
	}

	public static String detect(String s, PosterWordTree r) {
		s = s.replaceAll(" +", " ");
		String condition = "";
		if (s.contains("\tcondition")) {
			condition = s.substring(s.indexOf("\tcondition"));
			s = s.substring(0, s.indexOf("\tcondition:"));
		}
		StringBuilder sbuilder = new StringBuilder();
		int i = 0;
		while (i < s.length()) {
			String next = find(s, i, r);
			if (next != null && !next.equals("")) {
				// String _next = ChartNormal.chartNormalize(next);
				sbuilder.append("##" + next + "/" + r.type + "##");
				i += next.length();
			} else {
				sbuilder.append(s.substring(i, i + 1));
				i++;
			}
		}
		return sbuilder.toString() + condition;
	}

	public static void main(String[] args) throws Exception {
		PosterWordTree album = new PosterWordTree(
				genWords("res/entity/input/song.txt"), "song");
		String input = "杨钰莹唱过你若安好 便是晴天吗";
		input = detect(input, album);
		System.out.println(input);
	}
}
