package beans;

/**
 * 一种简单的数据类型，包含两个（一对）类型可定义的数据。
 * 
 * @author hwp
 *
 * @param <T1>
 *            第一项的类型
 * @param <T2>
 *            第二项的类型
 */
public class Pair<T1, T2> {
	public T1 first;
	public T2 second;

	public Pair(T1 first, T2 second) {
		super();
		this.first = first;
		this.second = second;
	}
}
