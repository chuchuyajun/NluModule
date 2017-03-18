package config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * 用来存储配置信息，从./config.txt中导入
 * 
 * @author z-zhang
 *
 */
public class Config {
	private static HashMap<String, String> map;
	private static String configFile = "./config.txt";

	private static void init() {
		if (map == null) {
			System.out.println("start loading configurations...");
			map = new HashMap<String, String>();
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(configFile), "UTF-8"));
				String tmp = "";

				while ((tmp = br.readLine()) != null) {
					if (tmp.trim().equals("") || tmp.startsWith("//"))
						continue;
					String split[] = tmp.split("\t");
					String name = split[0];
					String value = split[1];
					// 嵌套Path
					if (value.startsWith("map(")) {
						String tmpName = value.substring(
								value.indexOf("(") + 1, value.indexOf(")"));
						value = map.get(tmpName)
								+ value.substring(value.indexOf(")") + 1);
					}
					map.put(name, value);
				}
				br.close();
				System.out.println("loading configurations complete.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 重置configFile
	public static void setConfigFile(String file) {
		configFile = file;
		init();
	}

	// 得到值
	public static String get(String name) {
		init();
		return (map.containsKey(name)) ? map.get(name) : null;
	}
}
