package nlu.conference;

/**
 * 音乐领域转换Style与ID
 * @author TCL
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import config.Config;

public class TransToId {
	// style转id
	private static HashMap<String, HashMap<String, String>> idmap;
	// id转style
	private static HashMap<String, HashMap<String, String>> stylemap;

	private static void init() {
		if (idmap == null) {
			idmap = new HashMap<String, HashMap<String, String>>();
			File list[] = new File(Config.get("entityIDDir")).listFiles();
			for (File f : list) {
				if (f.isDirectory())
					continue;
				HashMap<String, String> map = new HashMap<String, String>();
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(new FileInputStream(f),
									"UTF-8"));
					String tmp = "";

					while ((tmp = br.readLine()) != null) {
						String split[] = tmp.split("\t");
						map.put(split[0], split[1]);
					}
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				String name = f.getName();
				name = name.substring(0, name.indexOf(".txt"));
				idmap.put(name, map);
			}
		}

		if (stylemap == null) {
			stylemap = new HashMap<String, HashMap<String, String>>();
			File list[] = new File(Config.get("entityIDDir")).listFiles();
			for (File f : list) {
				if (f.isDirectory())
					continue;
				HashMap<String, String> map = new HashMap<String, String>();
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(new FileInputStream(f),
									"UTF-8"));
					String tmp = "";

					while ((tmp = br.readLine()) != null) {
						String split[] = tmp.split("\t");
						String tmpS = split[1];
						if (tmpS.contains("("))
							tmpS = tmpS.substring(0, tmpS.indexOf("("));
						map.put(tmpS, split[0]);
					}
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				String name = f.getName();
				name = name.substring(0, name.indexOf(".txt"));
				stylemap.put(name, map);
			}
		}
	}

	public static String transId(String word, String type) {
		init();
		if (!idmap.containsKey(type)) {
			return word;
		}
		return idmap.get(type).get(word);
	}

	public static String idTrans(String word, String type) {
		init();
		if (!stylemap.containsKey(type))
			return word;
		if (stylemap.get(type).containsKey(word))
			return stylemap.get(type).get(word);
		else
			return "";
	}

	public static void main(String args[]) {
		String style = "18(1)";
		System.out.println(idTrans(style, "style"));
	}
}
