package language;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import config.Config;

public class LanguagePackage {
	
	private static String languagefile = null;
	
	private static Random rand = null;
	
	private static Map<Integer, ArrayList<String>> stmts = null;
	
	public static void init() {
		if (languagefile == null) {
			languagefile = Config.get("lgpack");
		}
		if (stmts == null) {
			stmts = new HashMap<Integer, ArrayList<String>>();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(languagefile), "UTF-8"));
				String tmp = "";
				boolean start = false;
				int currentID = -1;
				ArrayList<String> stmt = null;
				while ((tmp = reader.readLine()) != null) {
					if (tmp.startsWith("//")) {
						continue;
					}
					if (tmp.trim().length() == 0) {
						continue;
					}
					
					if (tmp.startsWith("\t")) {
						stmt.add(tmp.trim());
					} else {
						if (start == true) {
							start = false;
							stmts.put(currentID, stmt);
						}
						start = true;
						System.out.println("tmp : " + tmp);
						currentID = Integer.parseInt(tmp.trim());
						stmt = new ArrayList<String>();
					}
				}
				if (start == true) {
					start = false;
					stmts.put(currentID, stmt);
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (rand == null) {
			rand = new Random();
		}
	}
	
	public static ArrayList<String> getStmtList(int id) {
		init();
		if (stmts.containsKey(id)) {
			return stmts.get(id);
		} else {
			return null;
		}
	}
	
	private static int getRand(int length) {
		init();
		return Math.abs(rand.nextInt() % length);
	}
	
	public static String getRandomStmt(int id) {
		init();
		ArrayList<String> _stmt = stmts.get(id);
		return _stmt.get(getRand(_stmt.size()));
	}
}
