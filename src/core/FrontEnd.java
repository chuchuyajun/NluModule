package core;

import beans.Pair;

/**
 * 自动问答系统的前端，功能是获取输入并输出后端结果
 * 
 * @author hwp
 */
public interface FrontEnd {
	public enum Type {
		/**
		 * 普通消息，即用户的话
		 */
		MESSAGE,

		/**
		 * 退出
		 */
		EXIT
	}

	/**
	 * 显示后端的结果
	 * 
	 * @param message
	 *            需要显示的消息
	 * 
	 */
	public void output(String message);

	/**
	 * 获取输入，返回输入的内容。类型可能是{@link Type#MESSAGE},{@link Type#FEEDBACK},
	 * {@link Type#EXIT}
	 * 
	 * @return 一个（类型, 内容）的对
	 * @see Type
	 */
	public Pair<Type, String> input();
}