package me.xiaosheng.chnlp.seg;

import java.util.HashSet;
import java.util.List;

import com.hankcs.hanlp.seg.common.Term;

public class POSSelector {

    private HashSet<String> keyPOSSet = null; // 关键词性集合
    
    public POSSelector() {
        keyPOSSet = new HashSet<String>();
    }

    /**
     * 添加关键词性
     * @param word
     */
    public void addKeyPOS(String pos) {
        keyPOSSet.add(pos);
    }

    /**
     * 添加关键词性
     * @param posList
     */
    public void addKeyPOS(List<String> posList) {
        keyPOSSet.addAll(posList);
    }

    /**
     * 删除关键词性
     * 
     * @param word
     */
    public void removeKeyPOS(String pos) {
        keyPOSSet.remove(pos);
    }

    /**
     * 保留关键词性
     * @param wordList
     */
    public void filterKeyPOS(List<Term> wordList) {
        for (int i = 0; i < wordList.size(); i++) {
            if (!keyPOSSet.contains(wordList.get(i).nature.toString())) {
                wordList.remove(i);
                i--;
            }
        }
    }
}
