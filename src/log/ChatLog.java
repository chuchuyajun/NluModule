package log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import config.Config;

public class ChatLog {
	
	private static String logdir = null;
	
	private static void init() {
		if (logdir == null) {
			logdir = Config.get("logDir");
		}
	}
	
	public static void makeFile(String time) {
		init();
		String dir = logdir + "/" + time + ".txt";
		File file = new File(dir);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void writeFile(String time, String content) {
		init();
		File file = new File(logdir + "/" + time + ".txt");
        try {
    		FileWriter writer = new FileWriter(file);
    		writer.write(content);
    		writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String t = df.format(new Date());
		t = t.replaceAll(":", "-");
		t = t.replaceAll(" ", "-");
		return t;
	}
	
	public static ArrayList<File> getFileList() {
		init();
		ArrayList<File> ret = new ArrayList<File>();
		File file = new File(logdir);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i ++) {
				String name = files[i].getName();
				if (!name.endsWith("txt")) {
					continue;
				}
				ret.add(files[i]);
			}
		}
		return ret;
	}
	
	public static String getFileContent(String name) {
		String ret = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(name), "UTF-8"));

			
			String tmp = "";
			while((tmp = br.readLine()) != null) {
				ret += tmp;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void main(String args[]) {
		System.out.println(getCurrentTime());
	}
}
