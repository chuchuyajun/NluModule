package core;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 对话管理单元 处理NLU单元传来的输入 {@link NLUUnit#process(String)} 遵循当前对话系统，产生String答案返回给用户
 * 
 * @author TCL
 */

public interface DialogManager {

	/**
	 * 处理{@link NLUUnit#process(String) NLU的输出}，做出答复。
	 * 
	 * @return 自然语言表示的对话回复
	 */
	public String process(ArrayList<HashMap<String, String>> input);
}
