package me.xiaosheng.chnlp.seg;

import java.util.ArrayList;
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
     * 标准分词
     * @param content 文本
     * @return
     */
    public static List<Term> StandardSegment(String content) {
        return StandardSegment(content, false);
    }

    /**
     * 标准分词
     * @param content 文本
     * @param filterStopWord 滤掉停用词
     * @return
     */
    public static List<Term> StandardSegment(String content, boolean filterStopWord) {
        List<Term> result = StandardTokenizer.segment(content);
        if (filterStopWord)
            CoreStopWordDictionary.apply(result);
        return result;
    }

    /**
     * NLP分词
     * 执行全部命名实体识别和词性标注
     * @param content 文本
     * @return
     */
    public static List<Term> NLPSegment(String content) {
        return NLPSegment(content, false);
    }

    /**
     * NLP分词
     * 执行全部命名实体识别和词性标注
     * @param content 文本
     * @param filterStopWord 滤掉停用词
     * @return
     */
    public static List<Term> NLPSegment(String content, boolean filterStopWord) {
        List<Term> result = NLPTokenizer.segment(content);
        if (filterStopWord)
        	CoreStopWordDictionary.apply(result);
        return result;
    }

    /**
     * 获得词语列表
     * @param termList 分词结果
     * @return
     */
    public static List<String> getWordList(List<Term> termList) {
        List<String> wordList = new ArrayList<String>();
        for (Term term : termList)
            wordList.add(term.word);
        return wordList;
    }

    /**
     * 获取词性列表
     * @param termList 分词结果
     * @return
     */
    public static List<String> getNatureList(List<Term> termList) {
        List<String> NatureList = new ArrayList<String>();
        for (Term term : termList)
            NatureList.add(term.nature.toString());
        return NatureList;
    }

    /**
     * 分句
     * @param document 文本
     * @return 句子列表
     */
    public static List<String> splitSentence(String document) {
        return splitSentence(document, "[，,。:：“”？?！!；;]");
    }

    /**
     * 分句
     * @param document 文本
     * @param splitReg 切分符号(正则表达式)
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
     * 对句子列表分词
     * @param sentenceList 句子列表
     * @param filterStopWord 滤掉停用词
     * @return
     */
    public static List<List<String>> splitWordInSentences(List<String> sentenceList, boolean filterStopWord) {
        List<List<String>> docs = new ArrayList<List<String>>(sentenceList.size());
        for (String sentence : sentenceList) {
            List<Term> termList = StandardSegment(sentence, filterStopWord);
            List<String> wordList = getWordList(termList);
            docs.add(wordList);
        }
        return docs;
    }
}
