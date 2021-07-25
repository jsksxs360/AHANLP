package me.xiaosheng.chnlp.srl;

/**
 * 论元
 * @author Xusheng
 */
public class Arg implements Comparable<Arg> {
	
	private String span;   // 论元文本片段
	private int offset;    // 论元在句子中的偏移位置
	private String label;  // 论元的SRL标签
	
	/**
	 * 论元
	 * @param argSpan 论元文本片段
	 * @param offset 论元在句子中的偏移位置
	 * @param label 论元的SRL标签
	 */
	public Arg(String argSpan, int offset, String label) {
		this.span = argSpan;
		this.offset = offset;
		this.label = label;
	}
	
	/**
	 * @return 论元文本片段
	 */
	public String getSpan() {
		return this.span;
	}
	
	/**
	 * @return 论元在句子中的偏移位置
	 */
	public int getOffset() {
		return this.offset;
	}
	
	/**
	 * @return 论元在句子中的位置，[start, end]。sen[start, end+1]=span
	 */
	public int[] getIdx() {
		return new int[] {this.offset, this.offset + this.span.length() - 1 };
	}
	
	/**
	 * @return 论元的SRL标签
	 */
	public String getLabel() {
		return this.label;
	}
	
	@Override
	public int compareTo(Arg o) {
		if (this.offset < o.getOffset())
			return -1;
		else if (this.offset > o.getOffset())
			return 1;
		else
			return 0;
	}
}
