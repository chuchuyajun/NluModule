package core;

/**
 * 自动问答系统的后端，也是核心部分。 包括NLU, DM和Datebase
 * 
 * @author TCL
 */

public interface BackEnd {
	/**
	 * 对用户的数据作出回答。
	 * 
	 * @param question
	 *            用户的问话
	 * @return 回答
	 */
	public String answer(String question);

	/**
	 * 结束后端的对话。<br>
	 * 调用本函数之后，无法再使用这个实例。
	 */
	public void terminate();
}
