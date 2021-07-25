package me.xiaosheng.chnlp.srl;

import java.util.ArrayList;
import java.util.List;

/**
 * 谓词
 * @author Xusheng
 */
public class Predicate {
	
	private String sentence;   // 句子
	private String predicate;  // 谓词
	private int offset;        // 谓词在句子中的偏移位置
	private List<Arg> args;    // 谓词对应的论元
	
	private boolean checkOffset(String sentence, int offset, String span) {
		return sentence.substring(offset, offset + span.length()).equals(span);
	}
	
	/**
	 * 谓词
	 * @param sentence 句子
	 * @param predicate 谓词
	 * @param offset 谓词在句子中的偏移位置
	 * @throws Exception 句子为空、偏移位置有问题
	 */
	public Predicate(String sentence, String predicate, int offset) throws Exception {
		if (sentence.isEmpty())
			throw new Exception("sentence is empty!");
		if (!checkOffset(sentence, offset, predicate))
			throw new Exception("predicate offset is wrong!");
		this.sentence = sentence;
		this.predicate = predicate;
		this.offset = offset;
		this.args = new ArrayList<Arg>();
	}
	
	/**
	 * 谓词
	 * @param sentence 句子
	 * @param predicate 谓词
	 * @param offset 谓词在句子中的偏移位置
	 * @param argments 论元列表
	 * @throws Exception 句子为空、谓词或论元偏移位置有问题
	 */
	public Predicate(String sentence, String predicate, int offset, List<Arg> argments) throws Exception {
		if (sentence.isEmpty())
			throw new Exception("sentence is empty!");
		if (!checkOffset(sentence, offset, predicate))
			throw new Exception("predicate offset is wrong!");
		this.sentence = sentence;
		this.predicate = predicate;
		this.offset = offset;
		this.args = argments;
		for (Arg arg : argments) {
			if (!checkOffset(sentence, arg.getOffset(), arg.getSpan()))
				throw new Exception("argment " + arg.getSpan() + " offset is wrong!");
		}
	}
	
	/**
	 * 增加论元
	 * @param arg 论元
	 */
	public void addArg(Arg arg) {
		if (!checkOffset(this.sentence, arg.getOffset(), arg.getSpan()))
			return;
		if (this.args.isEmpty())
			this.args.add(arg);
		else
			if (!this.args.contains(arg))
				this.args.add(arg);
	}
	
	/**
	 * 增加论元
	 * @param args 论元列表
	 */
	public void addArgs(List<Arg> args) {
		for (Arg arg : args) {
			this.addArg(arg);
		}
	}
	
	/**
	 * 删除论元
	 * @param offset 论元在句子中的偏移位置
	 */
	public void removeArg(int offset) {
		int idx = -1;
		for (int i = 0; i < this.args.size(); i++) {
			if (this.args.get(i).getOffset() == offset) {
				idx = i;
				break;
			}
		}
		if (idx != -1)
			this.args.remove(idx);
	}
	
	/**
	 * @return 句子
	 */
	public String getSentence() {
		return this.sentence;
	}
	
	/**
	 * @return 谓词
	 */
	public String getPredicate() {
		return this.predicate;
	}
	
	/**
	 * @return 谓词在句子中的偏移位置
	 */
	public int getOffset() {
		return this.offset;
	}
	
	/**
	 * @return 论元列表
	 */
	public List<Arg> getArguments() {
		return this.args;
	}
	
	/**
	 * 获取特定类型的论元
	 * @param label 论元SRL标签
	 * @return 论元列表
	 */
	public List<Arg> getArgments(String label) {
		List<Arg> results = new ArrayList<Arg>();
		for (Arg arg : this.args) {
			if (arg.getLabel() == label)
				results.add(arg);
		}
		return results;
	}
	
	/**
	 * @return 是否包含论元
	 */
	public boolean isEmpty() {
		return this.args.isEmpty();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Sentence: " + this.sentence + "\n");
		sb.append("Predicate: " + this.predicate + "\n");
		for (Arg arg : this.args) {
			sb.append("\t" + arg.getLabel() + " : " + arg.getSpan());
			int[] idxs = arg.getIdx();
			sb.append("\t[" + idxs[0] + ", " + idxs[1] + "]\n");
		}
		return sb.toString();
	}
}
