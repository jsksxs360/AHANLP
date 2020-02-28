package me.xiaosheng.chnlp.summary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.utility.TextUtility;

import me.xiaosheng.chnlp.Config;
import me.xiaosheng.chnlp.seg.Segment;
import me.xiaosheng.word2vec.Word2Vec;

/**
 * TextRank提取关键句和自动摘要
 * @author Xusheng
 * @author hankcs
 */
public class TextRankSentence {

    final static float d = 0.85f; // 阻尼系数
    final static int max_iter = 200; // 最大迭代次数
    final static float min_diff = 0.001f;

    static Word2Vec word2vec = new Word2Vec();
    static {
        try {
            Logger.getGlobal().info("loading Word2Vector model...");
            word2vec.loadGoogleModel(Config.word2vecModelPath());
            Logger.getGlobal().info("load model successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> sentenceList = null; // 句子列表
    private List<List<String>> senWordList = null; // 句子中的词语列表
    private TreeMap<Float, Integer> top; // 排序后的最终结果 rank <-> index
    private int senNum; // 句子数目
    private float[][] weight; // 句子和其他句子的相关程度
    private float[] weight_sum; // 句子和其他句子相关程度之和
    private float[] SenRank; // 迭代之后收敛的权重
    
    private TextRankSentence(String segType, List<String> sentenceList) {
        this.sentenceList = sentenceList;
        senWordList = new ArrayList<List<String>>(sentenceList.size());
        for(List<Term> senWords : Segment.splitWordInSentences(segType, sentenceList, true)) {
        	senWordList.add(Segment.getWordList(senWords));
        }
        senNum = sentenceList.size();
        weight = new float[senNum][senNum];
        weight_sum = new float[senNum];
        SenRank = new float[senNum];
        top = new TreeMap<Float, Integer>(Collections.reverseOrder());
        initParam();
        calSenRanks();
        for (int i = 0; i < senNum; ++i) // 按rank值排序
            top.put(SenRank[i], i);
    }
    
    private TextRankSentence(String segType, String document, String splitReg) {
        sentenceList = splitSentence(document, splitReg);
        senWordList = new ArrayList<List<String>>(sentenceList.size());
        for(List<Term> senWords : Segment.splitWordInSentences(segType, sentenceList, true)) {
        	senWordList.add(Segment.getWordList(senWords));
        }
        senNum = sentenceList.size();
        weight = new float[senNum][senNum];
        weight_sum = new float[senNum];
        SenRank = new float[senNum];
        top = new TreeMap<Float, Integer>(Collections.reverseOrder());
        initParam();
        calSenRanks();
        for (int i = 0; i < senNum; ++i) // 按rank值排序
            top.put(SenRank[i], i);
    }
    
    /**
     * 分句
     * @param document 文本
     * @param splitReg 切分符号(正则表达式)，如 [。:？?！!；;]
     * @return 句子列表
     */
    public static List<String> splitSentence(String document, String splitReg) {
        String[] lines = document.split("[\r\n]");
        List<String> sentences = new ArrayList<String>();
        for (String line : lines) {
            line = line.trim();
            if (line.length() == 0)
                continue;
            for (String sent : line.split(splitReg)) {
                sent = sent.trim();
                if (sent.length() == 0)
                    continue;
                sentences.add(sent);
            }
        }
        return sentences;
    }

    /**
     * 简单求和
     * @param array
     * @return
     */
    private static float sum(float[] array) {
        float total = 0;
        for (float v : array)
            total += v;
        return total;
    }

    /**
     * 初始化参数
     */
    private void initParam() {
        for (int i = 0; i < senNum; i++) {
            SenRank[i] = 1; // 所有句子初始值为1
            weight[i][i] = 0; // 不考虑自己到自己的边
            for (int j = i + 1; j < senNum; j++) {
                // 计算句子相似度
                float senSimi = word2vec.sentenceSimilarity(senWordList.get(i), senWordList.get(j));
                weight[i][j] = senSimi;
                weight[j][i] = senSimi;
            }
            weight_sum[i] = sum(weight[i]);
        }
    }

    /**
     * 计算句子rank值
     */
    private void calSenRanks() {
        for (int iter = 0; iter < max_iter; iter++) {
            float[] m = new float[senNum];
            float max_diff = 0;
            for (int i = 0; i < senNum; ++i) {
                m[i] = 1 - d;
                for (int j = 0; j < senNum; ++j) {
                    if (j == i || weight_sum[j] == 0)
                        continue;
                    m[i] += (d * weight[j][i] / weight_sum[j] * SenRank[j]);
                }
                max_diff = Math.max(max_diff, Math.abs(m[i] - SenRank[i]));
            }
            SenRank = m;
            if (max_diff <= min_diff)
                break;
        }
    }

    /**
     * 获取所有句子的rank值
     * @param document 文档
     * @return 句子的rank值
     */
    public static Map<String, Float> getSentenceRanks(String document) {
        return getSentenceRanks("Standard", document, "[，,。:：“”？?！!；;]");
    }
    
    /**
     * 获取所有句子的rank值
     * @param segType 分词器类型（Standard 或 NLP）
     * @param document 文档
     * @return 句子的rank值
     */
    public static Map<String, Float> getSentenceRanks(String segType, String document) {
    	return getSentenceRanks(segType, document, "[，,。:：“”？?！!；;]");
    }
    
    /**
     * 获取所有句子的rank值
     * @param segType 分词器类型（Standard 或 NLP）
     * @param document 文档
     * @param splitReg 句子切分符号(正则表达式)
     * @return 句子的rank值
     */
    public static Map<String, Float> getSentenceRanks(String segType, String document, String splitReg) {
        TextRankSentence trSen = new TextRankSentence(segType, document, splitReg);
        Map<Float, Integer> values = trSen.top;
        Map<String, Float> sentenceRanks = new HashMap<String, Float>();
        for (Map.Entry<Float, Integer> entry : values.entrySet()) {
            sentenceRanks.put(trSen.sentenceList.get(entry.getValue()), entry.getKey());
        }
        return sentenceRanks;
    }
    
    /**
     * 获取所有句子的rank值
     * @param sentenceList 句子列表
     * @return 句子的rank值
     */
    public static Map<String, Float> getSentenceRanks(List<String> sentenceList) {
    	return getSentenceRanks("Standard", sentenceList);
    }
    
    /**
     * 获取所有句子的rank值
     * @param segType 分词器类型（Standard 或 NLP）
     * @param sentenceList 句子列表
     * @return 句子的rank值
     */
    public static Map<String, Float> getSentenceRanks(String segType, List<String> sentenceList) {
        TextRankSentence trSen = new TextRankSentence(segType, sentenceList);
        Map<Float, Integer> values = trSen.top;
        Map<String, Float> sentenceRanks = new HashMap<String, Float>();
        for (Map.Entry<Float, Integer> entry : values.entrySet()) {
            sentenceRanks.put(trSen.sentenceList.get(entry.getValue()), entry.getKey());
        }
        return sentenceRanks;
    }
    
    /**
     * 提取关键句
     * @param document 文档
     * @param num 句子数目
     * @return 关键句列表
     */
    public static List<String> getTopSentenceList(String document, int num) {
        return getTopSentenceList("Standard", document, num, "[，,。:：“”？?！!；;]");
    }
    
    /**
     * 提取关键句
     * @param segType 分词器类型（Standard 或 NLP）
     * @param document 文档
     * @param num 句子数目
     * @return 关键句列表
     */
    public static List<String> getTopSentenceList(String segType, String document, int num) {
        return getTopSentenceList(segType, document, num, "[，,。:：“”？?！!；;]");
    }
    
    /**
     * 提取关键句
     * @param segType 分词器类型（Standard 或 NLP）
     * @param document 文档
     * @param num 句子数目
     * @param splitReg 句子切分符号(正则表达式)
     * @return 关键句列表
     */
    public static List<String> getTopSentenceList(String segType, String document, int num, String splitReg) {
        TextRankSentence trSen = new TextRankSentence(segType, document, splitReg);
        List<String> resultList = new ArrayList<String>();
        Collection<Integer> values = trSen.top.values();
        num = Math.min(num, values.size());
        Iterator<Integer> it = values.iterator();
        for (int i = 0; i < num; ++i)
            resultList.add(trSen.sentenceList.get(it.next()));
        return resultList;
    }
    
    /**
     * 提取关键句
     * @param sentenceList 句子列表
     * @param num 句子数目
     * @return 关键句列表
     */
    public static List<String> getTopSentenceList(List<String> sentenceList, int num) {
    	return getTopSentenceList("Standard", sentenceList, num);
    }
    
    /**
     * 提取关键句
     * @param segType 分词器类型（Standard 或 NLP）
     * @param sentenceList 句子列表
     * @param num 句子数目
     * @return 关键句列表
     */
    public static List<String> getTopSentenceList(String segType, List<String> sentenceList, int num) {
        TextRankSentence trSen = new TextRankSentence(segType, sentenceList);
        List<String> resultList = new ArrayList<String>();
        Collection<Integer> values = trSen.top.values();
        num = Math.min(num, values.size());
        Iterator<Integer> it = values.iterator();
        for (int i = 0; i < num; ++i)
            resultList.add(trSen.sentenceList.get(it.next()));
        return resultList;
    }

    /**
     * 按原文中出现顺序对句子排序
     * @param resultList 结果句子列表
     * @param sentenceList 原文句子列表
     * @return
     */
    private static List<String> permutation(List<String> resultList, List<String> sentenceList) {
        Collections.sort(resultList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Integer num1 = sentenceList.indexOf(o1);
                Integer num2 = sentenceList.indexOf(o2);
                return num1.compareTo(num2);
            }
        });
        return resultList;
    }

    /**
     * 提取关键句
     * @param resultList 结果句子列表
     * @param maxLength 最大长度
     * @return
     */
    private static List<String> pickSentences(List<String> resultList, int maxLength) {
        List<String> summary = new ArrayList<String>();
        int count = 0;
        for (String result : resultList) {
            if (count + result.length() <= maxLength) {
                summary.add(result);
                count += result.length();
            }
        }
        return summary;
    }
    
    /**
     * 提取摘要
     * @param document 文档
     * @param maxLength 最大长度
     * @return 摘要
     */
    public static String getSummary(String document, int maxLength) {
    	return getSummary("Standard", document, maxLength, "[，,。:：“”？?！!；;]");
    }

    /**
     * 提取摘要
     * @param segType 分词器类型（Standard 或 NLP）
     * @param document 文档
     * @param maxLength 最大长度
     * @return 摘要
     */
    public static String getSummary(String segType, String document, int maxLength) {
    	return getSummary(segType, document, maxLength, "[，,。:：“”？?！!；;]");
    }
    
    /**
     * 提取摘要
     * @param segType 分词器类型（Standard 或 NLP）
     * @param document 文档
     * @param maxLength 最大长度
     * @param splitReg 句子切分符号(正则表达式)
     * @return 摘要
     */
    public static String getSummary(String segType, String document, int maxLength, String splitReg) {
        TextRankSentence trSen = new TextRankSentence(segType, document, splitReg);
        List<String> sentenceList = trSen.sentenceList;
        int avgSentenceLength = document.length() / sentenceList.size();
        int size = maxLength / avgSentenceLength + 1;
        List<String> resultList = new ArrayList<String>();
        Collection<Integer> values = trSen.top.values();
        size = Math.min(size, values.size());
        Iterator<Integer> it = values.iterator();
        for (int i = 0; i < size; ++i)
            resultList.add(trSen.sentenceList.get(it.next()));
        resultList = permutation(resultList, sentenceList);
        resultList = pickSentences(resultList, maxLength);
        return TextUtility.join("。", resultList);
    }
    
    /**
     * 提取摘要
     * @param sentenceList 句子列表
     * @param maxLength 最大长度
     * @return 摘要
     */
    public static String getSummary(List<String> sentenceList, int maxLength) {
    	return getSummary("Standard", sentenceList, maxLength);
    }
    
    /**
     * 提取摘要
     * @param segType 分词器类型（Standard 或 NLP）
     * @param sentenceList 句子列表
     * @param maxLength 最大长度
     * @return 摘要
     */
    public static String getSummary(String segType, List<String> sentenceList, int maxLength) {
        TextRankSentence trSen = new TextRankSentence(segType, sentenceList);
        int documentLength = 0;
        for (String sentence : sentenceList)
            documentLength += sentence.length();
        int avgSentenceLength = documentLength / sentenceList.size();
        int size = maxLength / avgSentenceLength + 1;
        List<String> resultList = new ArrayList<String>();
        Collection<Integer> values = trSen.top.values();
        size = Math.min(size, values.size());
        Iterator<Integer> it = values.iterator();
        for (int i = 0; i < size; ++i)
            resultList.add(trSen.sentenceList.get(it.next()));
        resultList = permutation(resultList, sentenceList);
        resultList = pickSentences(resultList, maxLength);
        return TextUtility.join("。", resultList);
    }
}
