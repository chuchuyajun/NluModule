package core;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 自然语言理解单元 得到用户问题，String字符串，将用户问题转成机器理解的语言 传递给DM单元
 * 
 * @author z-zhang
 */
public interface NLUUnit {

	/**
	 * 将自然语言的输入翻译成机器可以理解的形式。
	 * 
	 * @param input
	 *            自然语言
	 * @return HashMap的List
	 */
	public ArrayList<HashMap<String, String>> process(String input);

}