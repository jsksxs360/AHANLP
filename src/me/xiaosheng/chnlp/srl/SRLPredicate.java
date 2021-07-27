package me.xiaosheng.chnlp.srl;

import java.util.ArrayList;
import java.util.List;

/**
 * 谓词
 * @author Xusheng
 */
public class SRLPredicate {
    
    private int senOffset;     // 全文偏移量
    private String sentence;   // 句子
    private String predicate;  // 谓词
    private int localOffset;   // 谓词的句内偏移量
    private List<Arg> args;    // 谓词对应的论元列表
    
    private boolean checkOffset(String sentence, int localOffset, String span) {
        return sentence.substring(localOffset, localOffset + span.length()).equals(span);
    }
    
    /**
     * 谓词
     * @param sentence 句子
     * @param predicate 谓词
     * @param localOffset 谓词的句内偏移量
     * @throws Exception 句子为空、偏移量有问题
     */
    public SRLPredicate(String sentence, String predicate, int localOffset) throws Exception {
        if (sentence.isEmpty())
            throw new Exception("sentence is empty!");
        if (!checkOffset(sentence, localOffset, predicate))
            throw new Exception("predicate local offset is wrong!");
        this.senOffset = 0;
        this.sentence = sentence;
        this.predicate = predicate;
        this.localOffset = localOffset;
        this.args = new ArrayList<Arg>();
    }
    
    /**
     * 谓词
     * @param sentence 句子
     * @param predicate 谓词
     * @param localOffset 谓词的句内偏移量
     * @param argments 论元列表
     * @throws Exception 句子为空、谓词或论元偏移量有问题
     */
    public SRLPredicate(String sentence, String predicate, int localOffset, List<Arg> argments) throws Exception {
        if (sentence.isEmpty())
            throw new Exception("sentence is empty!");
        if (!checkOffset(sentence, localOffset, predicate))
            throw new Exception("predicate local offset is wrong!");
        this.senOffset = 0;
        this.sentence = sentence;
        this.predicate = predicate;
        this.localOffset = localOffset;
        this.args = argments;
        for (Arg arg : argments) {
            if (!checkOffset(sentence, arg.getLocalOffset(), arg.getSpan()))
                throw new Exception("argment " + arg.getSpan() + " local offset is wrong!");
        }
    }
    
    /**
     * 设置句子的全文偏移量
     * @param senOffset
     */
    public void setSenOffset(int senOffset) {
        this.senOffset = senOffset;
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
     * @return 谓词的句内偏移量
     */
    public int getLocalOffset() {
        return this.localOffset;
    }
    
    /**
     * @return 谓词的句内索引, [start, end]
     * , sen[start, end] = predicate
     */
    public int[] getLocalIdxs() {
        return new int[] {
            this.localOffset, // start
            this.localOffset + this.predicate.length() - 1 //end
        };
    }
    
    /**
     * @return 谓词的全文偏移量
     */
    public int getGlobalOffset() {
        return this.senOffset + this.localOffset;
    }
    
    /**
     * @return 谓词的全文索引, [start, end]
     * , content[start, end]=predicate
     */
    public int[] getGlobalIdxs() {
        return new int[] {
                this.senOffset + this.localOffset,
                this.senOffset + this.localOffset + this.predicate.length() - 1
        };
    }
    
    /**
     * 增加论元
     * @param arg 论元
     */
    public void addArg(Arg arg) {
        if (!checkOffset(this.sentence, arg.getLocalOffset(), arg.getSpan()))
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
     * @param localOffset 论元的句内偏移量
     */
    public void removeArgByLocalOffset(int localOffset) {
        int idx = -1;
        for (int i = 0; i < this.args.size(); i++) {
            if (this.args.get(i).getLocalOffset() == localOffset) {
                idx = i;
                break;
            }
        }
        if (idx != -1)
            this.args.remove(idx);
    }
    
    /**
     * 删除论元
     * @param globalOffset 论元的全文偏移量
     */
    public void removeArgByGlobalOffset(int globalOffset) {
        int idx = -1;
        for (int i = 0; i < this.args.size(); i++) {
            if (this.args.get(i).getGlobalOffset() == globalOffset) {
                idx = i;
                break;
            }
        }
        if (idx != -1)
            this.args.remove(idx);
    }

    /**
     * @return 论元列表
     */
    public List<Arg> getArguments() {
        return this.args;
    }
    
    /**
     * 获取特定类型的论元
     * @param label SRL标签
     * @return 论元列表
     */
    public List<Arg> getArgments(String label) {
        List<Arg> results = new ArrayList<Arg>();
        for (Arg arg : this.args) {
            if (arg.getLabel().equals(label))
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
        sb.append("Predicate: " + this.predicate + 
                  "\t[" + this.getLocalIdxs()[0] + ", " + this.getLocalIdxs()[1] + "]\n");
        for (Arg arg : this.args) {
            sb.append("\t" + arg.getLabel() + " : " + arg.getSpan());
            sb.append("\t[" + arg.getLocalIdxs()[0] + ", " + arg.getLocalIdxs()[1] + "]\n\n");
        }
        return sb.toString();
    }
}
