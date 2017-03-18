package nlu.conference;

/**
 * 利用Tire树名词实体识别
 * 返回List个结果，保存所有可能性
 * 
 * @author TCL
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

import config.Config;
import sun.launcher.resources.launcher;

//排序
class EntitySpanSort implements Comparator<EntitySpan> {
	@Override
	public int compare(EntitySpan o1, EntitySpan o2) {
		if (o1.start < o2.start)
			return -1;
		else if (o1.start > o2.start)
			return 1;
		else
			return 0;
	}
}

public class GetEntityAuto {
	private static HashMap<String, PosterWordTree> treeMap;

	private static HashMap<String, PosterWordTree> treeMapValue;

	private static HashMap<String, PosterWordTree> treeMapLabel;

	private static HashMap<String, PosterWordTree> treeMapAsk;

	private static HashMap<String, PosterWordTree> treeMapNega;

	private static HashMap<String, PosterWordTree> treeMapValueAdd;

	private static HashMap<String, PosterWordTree> treeMapLabelAdd;

	private static HashMap<String, String> unitMap;

	private static HashMap<String, String> unitaddMap;
	
	private static Map<String, String> dict = null;

	private static void init() {
		if (treeMap == null) {
			treeMap = new HashMap<String, PosterWordTree>();
			if (dict == null) {
				File file = new File(Config.get("entityInputDir"));
				File list[] = file.listFiles();
				for (File f : list) {
					if (f.isDirectory())
						continue;
					String name = f.getName();
					name = name.substring(0, name.indexOf(".txt"));
					PosterWordTree tree = new PosterWordTree(
							PosterWordTree.genWords(f.getAbsolutePath()), name);
					treeMap.put(name, tree);
				}
			} else {
				Iterator<Map.Entry<String, String>> ite = dict.entrySet().iterator();
				while (ite.hasNext()) {
					Map.Entry<String, String> entry = ite.next();
					String key = entry.getKey(), value = entry.getValue();
					String name = key;
					String[] slist = value.trim().split("\n");
					ArrayList<String> words = new ArrayList<>();
					for (int i = 0; i < slist.length; i ++) {
						words.add(slist[i].trim());
					}
					PosterWordTree tree = new PosterWordTree(
							PosterWordTree.genWords(words), name);
					treeMap.put(name, tree);
				}
			}
		}
		if (treeMapValue == null) {
			treeMapValue = new HashMap<String, PosterWordTree>();
			File file = new File(Config.get("valuedir"));
			File list[] = file.listFiles();
			for (File f : list) {
				if (f.isDirectory())
					continue;
				String name = f.getName();
				name = name.substring(0, name.indexOf(".txt"));
				PosterWordTree tree = new PosterWordTree(
						PosterWordTree.genWords(f.getAbsolutePath()), name);
				treeMapValue.put(name, tree);
			}
		}
		if (treeMapValueAdd == null) {
			treeMapValueAdd = new HashMap<String, PosterWordTree>();
			File file = new File(Config.get("valueadddir"));
			File list[] = file.listFiles();
			for (File f : list) {
				if (f.isDirectory())
					continue;
				String name = f.getName();
				name = name.substring(0, name.indexOf(".txt"));
				PosterWordTree tree = new PosterWordTree(
						PosterWordTree.genWords(f.getAbsolutePath()), name);
				treeMapValueAdd.put(name, tree);
			}
		}
		if (treeMapLabel == null) {
			treeMapLabel = new HashMap<String, PosterWordTree>();
			File file = new File(Config.get("labeldir"));
			File list[] = file.listFiles();
			for (File f : list) {
				if (f.isDirectory())
					continue;
				String name = f.getName();
				name = name.substring(0, name.indexOf(".txt"));
				PosterWordTree tree = new PosterWordTree(
						PosterWordTree.genWords(f.getAbsolutePath()), name);
				treeMapLabel.put(name, tree);
			}
		}
		if (treeMapLabelAdd == null) {
			treeMapLabelAdd = new HashMap<String, PosterWordTree>();
			File file = new File(Config.get("labeladddir"));
			File list[] = file.listFiles();
			for (File f : list) {
				if (f.isDirectory())
					continue;
				String name = f.getName();
				name = name.substring(0, name.indexOf(".txt"));
				PosterWordTree tree = new PosterWordTree(
						PosterWordTree.genWords(f.getAbsolutePath()), name);
				treeMapLabelAdd.put(name, tree);
			}
		}
		if (treeMapAsk == null) {
			treeMapAsk = new HashMap<String, PosterWordTree>();
			File file = new File(Config.get("askdir"));
			File list[] = file.listFiles();
			for (File f : list) {
				if (f.isDirectory())
					continue;
				String name = f.getName();
				name = name.substring(0, name.indexOf(".txt"));
				PosterWordTree tree = new PosterWordTree(
						PosterWordTree.genWords(f.getAbsolutePath()), name);
				treeMapAsk.put(name, tree);
			}
		}
		if (treeMapNega == null) {
			treeMapNega = new HashMap<String, PosterWordTree>();
			File file = new File(Config.get("negadir"));
			File list[] = file.listFiles();
			for (File f : list) {
				if (f.isDirectory())
					continue;
				String name = f.getName();
				name = name.substring(0, name.indexOf(".txt"));
				PosterWordTree tree = new PosterWordTree(
						PosterWordTree.genWords(f.getAbsolutePath()), name);
				treeMapNega.put(name, tree);
			}
		}
		if (unitMap == null) {
			unitMap = new HashMap<String, String>();
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(
								Config.get("unit")), "UTF-8"));
				String tmp = "";
				while ((tmp = reader.readLine()) != null) {
					if (tmp.trim().startsWith("//")) {
						continue;
					}
					String[] _list = tmp.trim().split(":");
					if (_list.length != 2) {
						System.out.println("unit.txt syntax error!");
					}
					String unittype = _list[0].trim();
					String[] _unitvalues = _list[1].trim().split(" ");
					for (int i = 0; i < _unitvalues.length; i++) {
						unitMap.put(_unitvalues[i].trim(), unittype);
					}
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (unitaddMap == null) {
			unitaddMap = new HashMap<String, String>();
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(
								Config.get("unitadd")), "UTF-8"));
				String tmp = "";
				while ((tmp = reader.readLine()) != null) {
					if (tmp.trim().startsWith("//")) {
						continue;
					}
					String[] _list = tmp.trim().split(":");
					if (_list.length != 2) {
						System.out.println("unit.txt syntax error!");
					}
					String unittype = _list[0].trim();
					String[] _unitvalues = _list[1].trim().split(" ");
					for (int i = 0; i < _unitvalues.length; i++) {
						unitaddMap.put(_unitvalues[i].trim(), unittype);
					}
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static HashMap<String, String> getUnitAdd() {
		init();
		return unitaddMap;
	}

	public static String getUnit(String input) {
		init();
		return unitMap.get(input);
	}

	public static ArrayList<EntitySpan> getSpamListValueAdd(String input) {
		init();
		input = input.toLowerCase().trim();
		ArrayList<EntitySpan> spamList = new ArrayList<EntitySpan>();

		// 遍历HashMap，得到spam信息
		Iterator<Map.Entry<String, PosterWordTree>> iter = treeMapValueAdd
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, PosterWordTree> entry = (Map.Entry<String, PosterWordTree>) iter
					.next();
			String name = (String) entry.getKey();
			PosterWordTree tree = (PosterWordTree) entry.getValue();
			String tmp = PosterWordTree.detect(input, tree);
			getSpam(tmp, input, name, spamList);
		}
		return spamList;
	}

	public static ArrayList<EntitySpan> getSpamListValue(String input) {
		init();
		input = input.toLowerCase().trim();
		ArrayList<EntitySpan> spamList = new ArrayList<EntitySpan>();

		// 遍历HashMap，得到spam信息
		Iterator<Map.Entry<String, PosterWordTree>> iter = treeMapValue
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, PosterWordTree> entry = (Map.Entry<String, PosterWordTree>) iter
					.next();
			String name = (String) entry.getKey();
			PosterWordTree tree = (PosterWordTree) entry.getValue();
			String tmp = PosterWordTree.detect(input, tree);
			getSpam(tmp, input, name, spamList);
		}
		return spamList;
	}

	public static ArrayList<EntitySpan> getSpamListNega(String input) {
		init();
		input = input.toLowerCase().trim();
		ArrayList<EntitySpan> spamList = new ArrayList<EntitySpan>();

		// 遍历HashMap，得到spam信息
		Iterator<Map.Entry<String, PosterWordTree>> iter = treeMapNega
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, PosterWordTree> entry = (Map.Entry<String, PosterWordTree>) iter
					.next();
			String name = (String) entry.getKey();
			PosterWordTree tree = (PosterWordTree) entry.getValue();
			String tmp = PosterWordTree.detect(input, tree);
			getSpam(tmp, input, name, spamList);
		}
		return spamList;
	}

	public static ArrayList<EntitySpan> getSpamListLabel(String input) {
		init();
		input = input.toLowerCase().trim();
		ArrayList<EntitySpan> spamList = new ArrayList<EntitySpan>();

		// 遍历HashMap，得到spam信息
		Iterator<Map.Entry<String, PosterWordTree>> iter = treeMapLabel
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, PosterWordTree> entry = (Map.Entry<String, PosterWordTree>) iter
					.next();
			String name = (String) entry.getKey();
			PosterWordTree tree = (PosterWordTree) entry.getValue();
			String tmp = PosterWordTree.detect(input, tree);
			getSpam(tmp, input, name, spamList);
		}
		return spamList;
	}

	public static ArrayList<EntitySpan> getSpamListLabelAdd(String input) {
		init();
		input = input.toLowerCase().trim();
		ArrayList<EntitySpan> spamList = new ArrayList<EntitySpan>();

		// 遍历HashMap，得到spam信息
		Iterator<Map.Entry<String, PosterWordTree>> iter = treeMapLabelAdd
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, PosterWordTree> entry = (Map.Entry<String, PosterWordTree>) iter
					.next();
			String name = (String) entry.getKey();
			PosterWordTree tree = (PosterWordTree) entry.getValue();
			String tmp = PosterWordTree.detect(input, tree);
			getSpam(tmp, input, name, spamList);
		}
		return spamList;
	}

	public static ArrayList<EntitySpan> getSpamListAsk(String input) {
		init();
		input = input.toLowerCase().trim();
		ArrayList<EntitySpan> spamList = new ArrayList<EntitySpan>();

		// 遍历HashMap，得到spam信息
		Iterator<Map.Entry<String, PosterWordTree>> iter = treeMapAsk
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, PosterWordTree> entry = (Map.Entry<String, PosterWordTree>) iter
					.next();
			String name = (String) entry.getKey();
			PosterWordTree tree = (PosterWordTree) entry.getValue();
			String tmp = PosterWordTree.detect(input, tree);
			getSpam(tmp, input, name, spamList);
		}
		return spamList;
	}

	// 获取spam信息，得到所有可能方案
	public static ArrayList<String> getEntityList(String input) {
		init();
		input = input.toLowerCase().trim();
		ArrayList<EntitySpan> spamList = new ArrayList<EntitySpan>();

		// 遍历HashMap，得到spam信息
		Iterator<Map.Entry<String, PosterWordTree>> iter = treeMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, PosterWordTree> entry = (Map.Entry<String, PosterWordTree>) iter
					.next();
			String name = (String) entry.getKey();
			PosterWordTree tree = (PosterWordTree) entry.getValue();
			String tmp = PosterWordTree.detect(input, tree);
			getSpam(tmp, input, name, spamList);
		}

		// spamlist按start由大到小排序
		Collections.sort(spamList, new EntitySpanSort());
		ArrayList<String> ret = getResultList(input, spamList);
		return ret;
	}
	
	// 获取spam信息，得到所有可能方案
		public static ArrayList<String> getEntityList(String input, Map<String, String> worddict) {
			dict = worddict;
			init();
			input = input.toLowerCase().trim();
			ArrayList<EntitySpan> spamList = new ArrayList<EntitySpan>();

			// 遍历HashMap，得到spam信息
			Iterator<Map.Entry<String, PosterWordTree>> iter = treeMap.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, PosterWordTree> entry = (Map.Entry<String, PosterWordTree>) iter
						.next();
				String name = (String) entry.getKey();
				PosterWordTree tree = (PosterWordTree) entry.getValue();
				String tmp = PosterWordTree.detect(input, tree);
				getSpam(tmp, input, name, spamList);
			}

			// spamlist按start由大到小排序
			Collections.sort(spamList, new EntitySpanSort());
			ArrayList<String> ret = getResultList(input, spamList);
			return ret;
		}

	// 得到Entity spam
	private static void getSpam(String tmp, String input, String type,
			ArrayList<EntitySpan> spamList) {
		String split[] = tmp.split("##");
		int start = -1, end = -1;
		for (String s : split) {
			if (s.contains("/" + type)) {
				String word = s.substring(0, s.indexOf("/" + type));
				start = input.indexOf(word, end);
				end = start + word.length();
				spamList.add(new EntitySpan(start, end, type, word));
			}
		}
	}

	// 根据spam计算所有可能结果
	private static ArrayList<String> getResultList(String input,
			ArrayList<EntitySpan> spamList) {
		int size = input.length();
		ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
		// 初始化
		for (int i = 0; i < size + 1; i++) {
			resultList.add(new ArrayList<String>());
		}
		/*
		 * 递推 |--- "x" + list[i+1]; list[i] = |--- spam + list[spam.end]
		 * spam的start是i |
		 */
		// size处的list为""
		ArrayList<String> tmpList = new ArrayList<String>();
		tmpList.add("");
		resultList.set(size, tmpList);
		for (int i = size - 1; i >= 0; i--) {
			ArrayList<String> newList = new ArrayList<String>();
			tmpList = resultList.get(i + 1);
			char word = input.charAt(i);
			// 增加 "x" + list[i+1]
			for (String line : tmpList) {
				newList.add(word + line);
			}
			// 是否有spam的start是i
			for (EntitySpan spam : spamList) {
				if (spam.start > i) {
					continue;
				} else if (spam.start < i)
					break;
				else {
					tmpList = resultList.get(spam.end);
					// 增加 "x" + list[i+1]
					for (String line : tmpList) {
						newList.add("##" + spam.word + "/" + spam.type + "##"
								+ line);
					}
				}
			}
			resultList.set(i, newList);
		}
		ArrayList<String> ret = resultList.get(0);
		return replaceList(ret);
	}

	private static ArrayList<String> replaceList(ArrayList<String> list) {
		ArrayList<String> ret = new ArrayList<String>();

		for (String line : list) {
			// 遍历HashMap，替换每一个entity
			Iterator<Map.Entry<String, PosterWordTree>> iter = treeMap
					.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, PosterWordTree> entry = (Map.Entry<String, PosterWordTree>) iter
						.next();
				String name = (String) entry.getKey();

				ArrayList<String> tmpList = new ArrayList<String>();
				String split[] = line.split("##");
				for (String word : split) {
					if (word.equals(""))
						continue;
					if (word.contains("/" + name)) {
						word = word.substring(0, word.indexOf("/" + name));

						// 部分转成id
						String transword = TransToId.transId(word, name);
						if (transword.contains("("))
							transword = transword.substring(0,
									transword.indexOf("("));
						tmpList.add(transword);

						line = line.replaceAll("##" + word + "/" + name + "##",
								name);
					}
				}
				String condition = "";
				if (line.contains("\tcondition:")) {
					condition = line.substring(line.indexOf("\tcondition:"));
					line = line.substring(0, line.indexOf("\tcondition:"));
				} else {
					condition = "\tcondition:";
				}
				if (tmpList.size() != 0) {
					condition += " \t" + name + ":" + merge(tmpList);
				}
				line = line + condition;
			}
			ret.add(line);
		}
		// return styleProcess(ret);
		return ret;
	}

	// merge array
	private static String merge(ArrayList<String> list) {
		String ret = "";
		for (String word : list) {
			ret += word + ",";
		}
		ret = ret.substring(0, ret.length() - 1);
		return ret;
	}

	// 单独处理style情况，包含父结点
	// private static ArrayList<String> styleProcess(ArrayList<String> list){
	// ArrayList<String> ret = new ArrayList<String>();
	// Pattern pattern =
	// Pattern.compile("style:(([0-9]{1,3}\\([0-9]{1,3}\\))|,)+",
	// Pattern.DOTALL);
	// for (String input : list){
	// Matcher m = pattern.matcher(input);
	// boolean find = false;
	//
	// while (m.find()){
	// find = true;
	// String style = m.group();
	// style = style.substring(style.indexOf(" ")+1);
	// String split[] = style.split(",");
	//
	// style = style.replace("(", "\\(");
	// style = style.replace(")", "\\)");
	// int len = split.length;
	//
	// ArrayList<String> tmp = new ArrayList<String>();
	// int retLen = ret.size();
	// // 2^n种可能
	// for (int i=0; i<Math.pow(2, len); i++){
	// tmp.clear();
	// for (Integer j=0; j<len; j++){
	// String tmpStyle = split[j];
	// int tmpInt = (i >> (len-j-1)) & 0x1;
	// //用本身style
	// if (tmpInt == 0){
	// String add = tmpStyle.substring(0, tmpStyle.indexOf("("));
	// // if (!tmp.contains(add))
	// tmp.add(add);
	// }
	// //用父style
	// else {
	// String add = "style:" + tmpStyle.substring(tmpStyle.indexOf("(")+1,
	// tmpStyle.indexOf(")"));
	// // if (!tmp.contains(add))
	// tmp.add(add);
	// }
	// }
	// String replace = merge(tmp);
	// String newInput = input.replaceAll(style, replace);
	// ret.add(retLen, newInput);
	// }
	// }
	// if (!find)
	// ret.add(input);
	// }
	// return ret;
	// }

	public static void main(String args[]) {
		String input = "最火爆的情歌网络歌曲";
		System.out.println(GetEntityAuto.getEntityList(input));
	}

}
