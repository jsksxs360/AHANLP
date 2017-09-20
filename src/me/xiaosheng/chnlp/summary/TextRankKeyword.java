package me.xiaosheng.chnlp.summary;

import me.xiaosheng.chnlp.seg.POSFilter;
import me.xiaosheng.chnlp.seg.Segment;

import java.util.*;

import com.hankcs.hanlp.algorithm.MaxHeap;
import com.hankcs.hanlp.seg.common.Term;

/**
 * TextRank提取关键词
 * @author Xusheng
 * @author hankcs
 */
public class TextRankKeyword {
    final static float d = 0.85f; // 阻尼系数
    final static int max_iter = 200; // 最大迭代次数
    final static float min_diff = 0.001f;

    private Map<String, Float> wordRanks = null;

    private TextRankKeyword(List<String> orderedWords) {
        calWordRank(orderedWords);
    }
    
    private TextRankKeyword(String document) {
        List<String> wordList = getSegResult(document); // 获取实词列表
        calWordRank(wordList);
    }

    /**
     * 获得分词结果 去除停用词、保留实词
     * @param content 文本
     * @return 词语列表
     */
    private static List<String> getSegResult(String content) {
        List<Term> segResult = Segment.StandardSegment(content, true);
        POSFilter.selectRealWords(segResult);
        return Segment.getWordList(segResult);
    }

    /**
     * 计算每个词语的rank值
     * @param wordList 词语列表
     */
    private void calWordRank(List<String> wordList) {
        // 词语 <-> 同窗口内的其他词语
        Map<String, Set<String>> words = new TreeMap<String, Set<String>>();
        Queue<String> window = new LinkedList<String>(); // 窗口
        for (String w : wordList) {
            if (!words.containsKey(w))
                words.put(w, new TreeSet<String>());
            window.offer(w);
            if (window.size() > 5)
                window.poll();
            for (String w1 : window) {
                for (String w2 : window) {
                    if (w1.equals(w2))
                        continue;
                    words.get(w1).add(w2);
                }
            }
        }
        wordRanks = new HashMap<String, Float>();
        for (int i = 0; i < max_iter; i++) {
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            for (Map.Entry<String, Set<String>> entry : words.entrySet()) {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - d);
                for (String element : value) {
                    int size = words.get(element).size();
                    if (key.equals(element) || size == 0)
                        continue;
                    m.put(key, m.get(key) + d / size * (wordRanks.get(element) == null ? 0 : wordRanks.get(element)));
                }
                max_diff = Math.max(max_diff,
                        Math.abs(m.get(key) - (wordRanks.get(key) == null ? 0 : wordRanks.get(key))));
            }
            wordRanks = m;
            if (max_diff <= min_diff)
                break;
        }
    }

    /**
     * 获取所有词语的rank值
     * @param document 文档
     * @return
     */
    public static Map<String, Float> getWordRanks(String document) {
        TextRankKeyword trKeyWord = new TextRankKeyword(document);
        return trKeyWord.wordRanks;
    }
    
    /**
     * 获取所有词语的rank值
     * @param orderedWords 有语序的词语序列
     * @return
     */
    public static Map<String, Float> getWordRanks(List<String> orderedWords) {
        TextRankKeyword trKeyWord = new TextRankKeyword(orderedWords);
        return trKeyWord.wordRanks;
    }
    
    /**
     * 获取关键词rank值
     * @param document 文档
     * @param num 关键词数量
     * @return
     */
    public static Map<String, Float> getKeywordRanks(String document, int num) {
        TextRankKeyword trKeyWord = new TextRankKeyword(document);
        Map<String, Float> result = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> entry : new MaxHeap<Map.Entry<String, Float>>(num,
                new Comparator<Map.Entry<String, Float>>() {
                    @Override
                    public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                        return o1.getValue().compareTo(o2.getValue());
                    }
                }).addAll(trKeyWord.wordRanks.entrySet()).toList()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    /**
     * 获取关键词rank值
     * @param orderedWords 有语序的词语序列
     * @param num 关键词数量
     * @return
     */
    public static Map<String, Float> getKeywordRanks(List<String> orderedWords, int num) {
        TextRankKeyword trKeyWord = new TextRankKeyword(orderedWords);
        Map<String, Float> result = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> entry : new MaxHeap<Map.Entry<String, Float>>(num,
                new Comparator<Map.Entry<String, Float>>() {
                    @Override
                    public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                        return o1.getValue().compareTo(o2.getValue());
                    }
                }).addAll(trKeyWord.wordRanks.entrySet()).toList()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 提取关键词
     * @param document 文档
     * @param num 关键词数量
     * @return
     */
    public static List<String> getKeywordList(String document, int num) {
        Map<String, Float> wordRanks = getKeywordRanks(document, num);
        List<String> keywordList = new ArrayList<String>();
        for (Map.Entry<String, Float> wordRank : wordRanks.entrySet()) {
            keywordList.add(wordRank.getKey());
        }
        return keywordList;
    }
    
    /**
     * 提取关键词
     * @param orderedWords 有语序的词语序列
     * @param num 关键词数量
     * @return
     */
    public static List<String> getKeywordList(List<String> orderedWords, int num) {
        Map<String, Float> wordRanks = getKeywordRanks(orderedWords, num);
        List<String> keywordList = new ArrayList<String>();
        for (Map.Entry<String, Float> wordRank : wordRanks.entrySet()) {
            keywordList.add(wordRank.getKey());
        }
        return keywordList;
    }
}
