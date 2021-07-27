package me.xiaosheng.chnlp.srl;

/**
 * 论元
 * @author Xusheng
 */
public class Arg implements Comparable<Arg> {
    
    private int senOffset;    // 句子在全文中的偏移量
    private String span;      // 论元文本
    private int localOffset;  // 句内偏移量
    private String label;     // SRL标签
    
    /**
     * 论元
     * @param argSpan 论元文本
     * @param localOffset 句内偏移量
     * @param label SRL标签
     */
    public Arg(String argSpan, int localOffset, String label) {
        this.senOffset = 0;
        this.span = argSpan;
        this.localOffset = localOffset;
        this.label = label;
    }
    
    /**
     * 设置句子在全文中的偏移量
     * @param senOffset
     */
    public void setSenOffset(int senOffset) {
        this.senOffset = senOffset;
    }
    
    /**
     * @return 论元文本
     */
    public String getSpan() {
        return this.span;
    }
    
    /**
     * @return 句内偏移量
     */
    public int getLocalOffset() {
        return this.localOffset;
    }
    
    /**
     * @return 句内索, [start, end]
     * , sentence[start, end+1]=argument
     */
    public int[] getLocalIdxs() {
        return new int[] {
            this.localOffset, //start index
            this.localOffset + this.span.length() - 1 //end index
        };
    }
    
    /**
     * @return 全文偏移量
     */
    public int getGlobalOffset() {
        return this.senOffset + this.localOffset;
    }
    
    /**
     * @return 全文索引, [start, end]
     * , content[start, end+1]=argument
     */
    public int[] getGlobalIdxs() {
        return new int[] {
            this.senOffset + this.localOffset, //start index
            this.senOffset + this.localOffset + this.span.length() - 1 //end index
        };
    }
    
    /**
     * @return SRL标签
     */
    public String getLabel() {
        return this.label;
    }
    
    @Override
    public int compareTo(Arg o) {
        if (this.getGlobalOffset() < o.getGlobalOffset())
            return -1;
        else if (this.getGlobalOffset() > o.getGlobalOffset())
            return 1;
        else
            return 0;
    }
}
