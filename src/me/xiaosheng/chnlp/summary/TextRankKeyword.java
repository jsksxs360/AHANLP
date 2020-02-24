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
    
    private TextRankKeyword(String segType, String document) {
        List<String> wordList = getSegResult(segType, document); // 获取实词列表
        calWordRank(wordList);
    }

    /**
     * 获得分词结果 去除停用词、保留实词
     * @param segType 分词器，Standard 或者 NLP
     * @param content 文本
     * @return 词语列表
     */
    private static List<String> getSegResult(String segType, String content) {
    	List<Term> segResult = null;
    	if ("Standard".equals(segType) || "标准分词".equals(segType)) {
    		segResult = Segment.StandardSegment(content, true);
    	} else if ("NLP".equals(segType) || "NLP分词".equals(segType)) {
    		segResult = Segment.NLPSegment(content, true);
    	} else {
    		throw new IllegalArgumentException(String.format("非法参数 segType == %s", segType));
    	}
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
            if (window.size() >= 5)
                window.poll();
            for (String qWord : window) {
            	if (w.equals(qWord))
                    continue;
            	//既然是邻居,那么关系是相互的,遍历一遍即可
                words.get(w).add(qWord);
                words.get(qWord).add(w);
            }
            window.offer(w);
        }
        wordRanks = new HashMap<String, Float>();
        //依据TF来设置初值
        for (Map.Entry<String, Set<String>> entry : words.entrySet())
            wordRanks.put(entry.getKey(),sigMoid(entry.getValue().size()));  
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
     * sigmoid函数
     * @param value
     * @return
     */
    public static float sigMoid(float value) {
    	return (float)(1d/(1d+Math.exp(-value)));
    }   

    /**
     * 获取所有词语的rank值
     * @param document 文档
     * @return 词语和对应的rank值
     */
    public static Map<String, Float> getWordRanks(String document) {
        return getWordRanks("Standard", document);
    }

    /**
     * 获取所有词语的rank值
     * @param segType 分词器类型，Standard或NLP
     * @param document 文档
     * @return 词语和对应的rank值
     */
    public static Map<String, Float> getWordRanks(String segType, String document) {
        TextRankKeyword trKeyWord = new TextRankKeyword(segType, document);
        return trKeyWord.wordRanks;
    }

    /**
     * 获取所有词语的rank值
     * @param orderedWords 有语序的词语序列
     * @return 词语和对应的rank值
     */
    public static Map<String, Float> getWordRanks(List<String> orderedWords) {
        TextRankKeyword trKeyWord = new TextRankKeyword(orderedWords);
        return trKeyWord.wordRanks;
    }

    /**
     * 获取关键词rank值
     * @param document 文档
     * @param num 关键词数量
     * @return 关键词和对应的rank值
     */
    public static Map<String, Float> getKeywordRanks(String document, int num) {
    	return getKeywordRanks("Standard", document, num);
    }

    /**
     * 获取关键词rank值
     * @param segType 分词器类型，Standard或NLP
     * @param document 文档
     * @param num 关键词数量
     * @return 关键词和对应的rank值
     */
    public static Map<String, Float> getKeywordRanks(String segType, String document, int num) {
        TextRankKeyword trKeyWord = new TextRankKeyword(segType, document);
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
     * @return 关键词和对应的rank值
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
     * @return 关键词列表
     */
    public static List<String> getKeywordList(String document, int num) {
        return getKeywordList("Standard", document, num);
    }

    /**
     * 提取关键词
     * @param segType 分词器类型，Standard或NLP
     * @param document 文档
     * @param num 关键词数量
     * @return 关键词列表
     */
    public static List<String> getKeywordList(String segType, String document, int num) {
        Map<String, Float> wordRanks = getKeywordRanks(segType, document, num);
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
     * @return 关键词列表
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
