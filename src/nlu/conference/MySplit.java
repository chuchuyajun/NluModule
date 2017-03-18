package nlu.conference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import config.Config;

/**
 * 分词程序
 * 
 * @author LiuChunyang
 *
 */
public class MySplit {
	public static Vector<String> vs_t = new Vector<String>();

	static {
		// 清空相关的数据结构。
		vs_t.clear();
		try {
			String ts = "";
			// 读取特殊词汇信息。
			BufferedReader br_t = new BufferedReader(new FileReader(new File(
					Config.get("targetPath"))));
			while ((ts = br_t.readLine()) != null) {
				vs_t.add(ts.trim());
			}
			br_t.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 主函数
	public static void main(String[] args) {
		try {
			// 字符串分词
			String sInput = "被风吹过的夏天是谁的歌？";
			System.out.println(testICTCLAS_ParagraphProcess(sInput, 1, 1));// 同testimportuserdict和testSetPOSmap
		} catch (Exception ex) {
		}
	}

	public static void dic() {

	}

	// 利用实验室的服务器进行分词程序.
	public static String splitString(String input) {
		try {
			Socket server = null;
			server = new Socket(Config.get("splitServer"), new Integer(
					Config.get("splitPort")));

			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					server.getOutputStream(), "utf-8"));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					server.getInputStream(), "utf-8"));
			out.println(false);
			out.println(input);
			out.println("#END#");
			out.flush();

			StringBuilder sb = new StringBuilder();
			String line;
			while (true) {
				line = in.readLine();
				if (line == null || line.equals("#END#"))
					break;
				sb.append(line);
				sb.append('\n');
			}
			server.close();
			return sb.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// 分词函数，dic设置为1代表需要重新导入词典,通常dic为0
	public static String testICTCLAS_ParagraphProcess(String sInput, int tag,
			int dic) {
		String ssplit = splitString(sInput);
		String assplit[] = ssplit.split(" ");
		StringBuilder sb = new StringBuilder();

		// 对分词结果进行处理，达到满足相关结构的分词结果。
		for (String ss : assplit) {
			if (ss.trim().equals(""))
				continue;
			String[] pair = ss.split("/");

			if (vs_t.contains(pair[0])) // 修正特征词汇.
			{
				sb.append(pair[0]);
				sb.append("/");
				sb.append("Theight");
				sb.append(" ");
			} else // 正常的分析结果
			{
				sb.append(pair[0]);
				sb.append("/");
				sb.append(pair[1]);
				sb.append(" ");
			}
		}

		return sb.toString();

	}
}
