package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 用于记录日志的类<br>
 * 注意：每一个实例在销毁前都需要调用{@link #close()}。
 * 
 * @author hwp
 *
 */

public class Log {
	private static final DateFormat df = DateFormat.getDateTimeInstance(
			DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.CHINA);

	private static String now() {
		return df.format(new Date());
	}

	private PrintStream out;

	/**
	 * 在logdir目录下创建一个日志。文件名由时间决定。
	 * 
	 * @param logdir
	 *            日志所在目录
	 * @throws FileNotFoundException
	 *             目录不存在
	 */
	public Log(File logdir) throws FileNotFoundException {
		this(logdir, now().replaceAll("[ :]", "-"));
	}

	/**
	 * 在logdir目录下创建一个指定文件名的日志。
	 * 
	 * @param logdir
	 *            日志所在目录
	 * @param name
	 *            文件名
	 * @throws FileNotFoundException
	 *             目录不存在
	 */
	public Log(File logdir, String name) throws FileNotFoundException {
		if (!logdir.exists()) {
			logdir.mkdirs();
		}
		File f = new File(logdir, name);
		out = new PrintStream(new FileOutputStream(f));
	}

	/**
	 * 关闭文件输出流
	 */
	public void close() {
		out.close();
	}

	private void log(String tag, String content) {
		out.print(now());
		out.print("\t: ");
		out.print(tag);
		out.print("\t : ");
		// out.println(content.replaceAll("[\\n\\r]", ""));
	}

	/**
	 * 记录一项用户输入信息
	 * 
	 * @param line
	 *            内容，建议单行数据
	 */
	public void inputLog(String line) {
		log("INPUT", line);
	}

	/**
	 * 记录一项系统输出信息
	 * 
	 * @param line
	 *            内容，建议单行数据
	 */
	public void outputLog(String line) {
		log("OUTPUT", line);
	}

	/**
	 * 记录一项NLU输出信息
	 * 
	 * @param line
	 *            内容，建议单行数据
	 */
	public void nluoutLog(String line) {
		log("NLU_OUT", line);
	}

	/**
	 * 记录一项系统备注
	 * 
	 * @param line
	 *            内容，建议单行数据
	 */
	public void note(String line) {
		log("NOTE", line);
	}
}
