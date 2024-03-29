package me.xiaosheng.chnlp.seg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

/**
 * 分词器
 * 
 * @author Xusheng
 */
public class Segment {
    
    /**
     * 分词
     * @param segType 分词器类型（Standard 或 NLP）
     * @param content 文本
     * @param filterStopWord 滤掉停用词
     * @return 分词结果
     */
    public static List<Term> segment(String segType, String content, boolean filterStopWord) {
        List<Term> results = null;
        if ("Standard".equals(segType) || "标准分词".equals(segType)) {
            results = StandardSegment(content, filterStopWord);
        } else if ("NLP".equals(segType) || "NLP分词".equals(segType)) {
            results = NLPSegment(content, filterStopWord);
        } else {
            throw new IllegalArgumentException(String.format("非法参数 segType == %s", segType));
        }
        return results;
    }
    
    /**
     * 标准分词<br>
     * HMM-Bigram<br>
     * 最短路分词，最短路求解采用Viterbi算法
     * @param content 文本
     * @return 分词结果
     */
    public static List<Term> StandardSegment(String content) {
        return StandardSegment(content, false);
    }

    /**
     * 标准分词<br>
     * HMM-Bigram<br>
     * 最短路分词，最短路求解采用Viterbi算法
     * @param content 文本
     * @param filterStopWord 滤掉停用词
     * @return 分词结果
     */
    public static List<Term> StandardSegment(String content, boolean filterStopWord) {
        List<Term> result = StandardTokenizer.segment(content);
        if (filterStopWord)
            CoreStopWordDictionary.apply(result);
        return result;
    }

    /**
     * NLP分词<br>
     * 感知机分词<br>
     * 执行词性标注和命名实体识别，更重视准确率
     * @param content 文本
     * @return 分词结果
     */
    public static List<Term> NLPSegment(String content) {
        return NLPSegment(content, false);
    }

    /**
     * NLP分词<br>
     * 感知机分词<br>
     * 执行词性标注和命名实体识别，更重视准确率
     * @param content 文本
     * @param filterStopWord 滤掉停用词
     * @return 分词结果
     */
    public static List<Term> NLPSegment(String content, boolean filterStopWord) {
        List<Term> result = NLPTokenizer.segment(content);
        if (filterStopWord)
        	CoreStopWordDictionary.apply(result);
        return result;
    }

    /**
     * 分词断句<br>
     * 按句号、问好、感叹号分隔句子，逗号、分号不分隔
     * @param segType 分词器类型（Standard 或 NLP）
     * @param content 文本
     * @param filterStopWord 滤掉停用词
     * @return 句子列表，每个句子由一个单词列表组成
     */
    public static List<List<Term>> seg2sentence(String segType, String content, boolean filterStopWord) {
    	return seg2sentence(segType, false, content, filterStopWord);
    }
    
    /**
     * 分词断句
     * @param segType 分词器类型（Standard 或 NLP）
     * @param shortest 是否断句为最细的子句（将逗号、分号也视作分隔符）
     * @param content 文本
     * @param filterStopWord 滤掉停用词
     * @return 句子列表，每个句子由一个单词列表组成
     */
    public static List<List<Term>> seg2sentence(String segType, boolean shortest, String content, boolean filterStopWord) {
    	List<List<Term>> results = null;
    	if ("Standard".equals(segType) || "标准分词".equals(segType)) {
    		results = StandardTokenizer.seg2sentence(content, shortest);
    	} else if ("NLP".equals(segType) || "NLP分词".equals(segType)) {
    		results = NLPTokenizer.seg2sentence(content, shortest);
    	} else {
    		throw new IllegalArgumentException(String.format("非法参数 segType == %s", segType));
    	}
    	if (filterStopWord)
    		for (List<Term> res : results)
    			CoreStopWordDictionary.apply(res);
		return results;
    }

    private static boolean isWhitespace(char[] string) {
        for (char c : string)
            if (!Character.isWhitespace(c) && c != '　')
                return false;
        return true;
    }
    
    /**
     * 获得词语列表
     * @param termList 分词结果
     * @return 词语列表
     */
    public static List<String> getWordList(List<Term> termList) {
        List<String> wordList = new ArrayList<String>();
        for (Term term : termList) {
            String worStr = term.word.trim();
            if (worStr.isEmpty() || isWhitespace(worStr.toCharArray()))
                continue;
            wordList.add(term.word);
        }
        return wordList;
    }

    /**
     * 获取词性列表
     * @param termList 分词结果
     * @return 词性列表
     */
    public static List<String> getNatureList(List<Term> termList) {
        List<String> NatureList = new ArrayList<String>();
        for (Term term : termList) {
            String worStr = term.word.trim();
            if (worStr.isEmpty() || isWhitespace(worStr.toCharArray()))
                continue;
            NatureList.add(term.nature.toString());
        }
        return NatureList;
    }

    /**
     * 分隔句子<br>
     * 按句号、问好、感叹号分隔句子，逗号、分号不分隔
     * @param document 文本
     * @return 句子列表
     */
    public static List<String> splitSentence(String document) {
        return splitSentence(document.toCharArray(), false);
    }
    
    private static void insertIntoList(StringBuilder sb, List<String> sentences) {
        String content = sb.toString().trim();
        if (content.length() > 0) {
            sentences.add(content);
        }
    }
    
    private static List<String> splitSentence(char[] chars, boolean shortest) {
        StringBuilder sb = new StringBuilder();
        List<String> sentences = new LinkedList<String>();
        for (int i = 0; i < chars.length; i++) {
            if (sb.length() == 0 && (Character.isWhitespace(chars[i]) || chars[i] == ' ' || chars[i] == '　'))
                continue;
            sb.append(chars[i]);
            switch (chars[i]) {
                case '.':
                    if (i < chars.length - 1 && chars[i + 1] > 128) {
                        insertIntoList(sb, sentences);
                        sb = new StringBuilder();
                    }
                    break;
                case '…':
                    if (i < chars.length - 1 && chars[i + 1] == '…') {
                        sb.append('…');
                        ++i;
                        insertIntoList(sb, sentences);
                        sb = new StringBuilder();
                    }
                    break;
                case '，':
                case ',':
                case ';':
                case '；':
                    if (!shortest)
                        continue;
                case '。':
                case '!':
                case '！':
                case '?':
                case '？':
                case '\n':
                case '\r':
                    insertIntoList(sb, sentences);
                    sb = new StringBuilder();
                    break;
            }
        }
        if (sb.length() > 0)
            insertIntoList(sb, sentences);
        return sentences;
    }
    
    /**
     * 分隔句子<br>
     * @param document 文本
     * @param shortest 是否断句为最细的子句（将逗号、分号也视作分隔符）
     * @return 句子列表
     */
    public static List<String> splitSentence(String document, boolean shortest) {
        return splitSentence(document.toCharArray(), shortest);
    }
    
    /**
     * 对句子列表分词
     * @param segType 分词器类型（Standard 或 NLP）
     * @param sentenceList 句子列表
     * @param filterStopWord 滤掉停用词
     * @return 句子列表，每个句子由一个单词列表组成
     */
    public static List<List<Term>> splitWordInSentences(String segType, List<String> sentenceList, boolean filterStopWord) {
    	List<List<Term>> docs = new ArrayList<List<Term>>(sentenceList.size());
    	for (String sentence : sentenceList) {
    		if ("Standard".equals(segType) || "标准分词".equals(segType)) {
    			List<Term> termList = StandardSegment(sentence, filterStopWord);
    			docs.add(termList);
    		} else if ("NLP".equals(segType) || "NLP分词".equals(segType)) {
    			List<Term> termList = NLPSegment(sentence, filterStopWord);
    			docs.add(termList);
    		} else {
    			throw new IllegalArgumentException(String.format("非法参数 type == %s", segType));
    		}
        }
        return docs;
    }
}
