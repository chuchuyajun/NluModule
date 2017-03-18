package core;

import beans.Pair;
import core.FrontEnd.Type;

/**
 * 一个完整自动问答系统的简单实现<br>
 * 连接实例化的{@link FrontEnd}和{@link BackEnd}。 调用{@link #start()}开始对话。
 * 
 * @author hwp
 * @see FrontEnd
 * @see BackEnd
 */
public class QAThread extends Thread {
	private FrontEnd f;
	private BackEnd b;

	public QAThread(FrontEnd f, BackEnd b) {
		super();
		this.f = f;
		this.b = b;
	}

	public void run() {
		Pair<Type, String> i;
		do {
			i = f.input();
			f.output(b.answer(i.second));
		} while (i.first != Type.EXIT);
		f.output("Goodbye!");
	}
}
