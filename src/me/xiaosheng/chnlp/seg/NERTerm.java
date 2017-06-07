package me.xiaosheng.chnlp.seg;

public class NERTerm {

    /**
     * 词语
     */
    public String word;
    /**
     * 实体类型
     */
    public String type;
    
    public NERTerm(String word, String type) {
        this.word = word;
        this.type = type;
    }
    
    @Override
    public String toString() {
        return word + "/" + type;
    }
    
    public int length() {
        return word.length();
    }
}
