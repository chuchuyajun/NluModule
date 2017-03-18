package nlu.conference;

public class EntitySpan {
	public int start;
	public int end;
	public String type;
	public String word;

	public EntitySpan() {
	}

	public EntitySpan(int start, int end, String type, String word) {
		this.start = start;
		this.end = end;
		this.type = type;
		this.word = word;
	}
}
