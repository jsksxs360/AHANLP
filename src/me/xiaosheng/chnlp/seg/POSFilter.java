package me.xiaosheng.chnlp.seg;

import java.util.Arrays;
import java.util.List;

import com.hankcs.hanlp.seg.common.Term;

public class POSFilter {

    /**
     * 移除标点符号
     * @param segResult 分词结果
     */
    public static void removePunc(List<Term> segResult) {
        for (int i = 0; i < segResult.size(); i++) {
            if (segResult.get(i).nature.toString().startsWith("w")) {
                segResult.remove(i);
                i--;
            }
        }
    }
    
    /**
     * 过滤词性
     * @param segResult 分词结果
     * @param removePOS 需要移除的词性
     */
    public static void removePOS(List<Term> segResult, List<String> removePOS) {
        for (int i = 0; i < segResult.size(); i++) {
            if (removePOS.contains(segResult.get(i).nature.toString())) {
                segResult.remove(i);
                i--;
            }
        }
    }
    
	/**
     * 保留实词
     * @param segResult 分词结果
     */
    public static void selectRealWords(List<Term> segResult) {
        List<String> selectPOS = Arrays.asList("n", "ns", "nr", "nt", "nz", "v", "vd", "vn", "a", "ad", "an", "d");
        for (int i = 0; i < segResult.size(); i++) {
            if (!selectPOS.contains(segResult.get(i).nature.toString())) {
                segResult.remove(i);
                i--;
            }
        }
    }
    
    /**
     * 保留词性
     * @param segResult 分词结果
     * @param selectPOS 需要保留的词性
     */
    public static void selectPOS(List<Term> segResult, List<String> selectPOS) {
        for (int i = 0; i < segResult.size(); i++) {
            if (!selectPOS.contains(segResult.get(i).nature.toString())) {
                segResult.remove(i);
                i--;
            }
        }
    }
}
