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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof NERTerm) {
            NERTerm other = (NERTerm) obj;
            if (this.word.equals(other.word))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.word.hashCode();
    }
}
