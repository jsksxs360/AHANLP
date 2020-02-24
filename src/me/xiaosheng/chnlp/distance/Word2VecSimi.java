package me.xiaosheng.chnlp.distance;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.ansj.vec.domain.WordEntry;

import me.xiaosheng.chnlp.Config;
import me.xiaosheng.chnlp.AHANLP;
import me.xiaosheng.word2vec.Word2Vec;

public class Word2VecSimi {

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
    
    /**
     * 词语相似度
     * @param word1 词语1
     * @param word2 词语2
     * @return 词语相似度
     */
    public static float wordSimilarity(String word1, String word2) {
        return word2vec.wordSimilarity(word1, word2);
    }
    
    /**
     * 获取相似词语
     * @param word 词语
     * @param maxReturnNum 最大返回词数
     * @return 相似词语
     */
    public static Set<WordEntry> getSimilarWords(String word, int maxReturnNum) {
        return word2vec.getSimilarWords(word, maxReturnNum);
    }

    /**
     * 句子相似度
     * @param sentence1Words 句子1中的词语
     * @param sentence2Words 句子2中的词语
     * @return 句子相似度
     */
    public static float sentenceSimilarity(List<String> sentence1Words, List<String> sentence2Words) {
        return word2vec.sentenceSimilarity(sentence1Words, sentence2Words);
    }
    
    /**
     * 句子相似度
     * @param sentence1 句子1
     * @param sentence2 句子2
     * @return 句子相似度
     */
    public static float sentenceSimilarity(String sentence1, String sentence2) {
    	return sentenceSimilarity("Standard", sentence1, sentence2);
    }
    
    /**
     * 句子相似度
     * @param segType 分词器类型（Standard 或 NLP）
     * @param sentence1 句子1
     * @param sentence2 句子2
     * @return 句子相似度
     */
    public static float sentenceSimilarity(String segType, String sentence1, String sentence2) {
    	List<String> sentence1Words = null;
    	List<String> sentence2Words = null;
    	if ("Standard".equals(segType) || "标准分词".equals(segType)) {
    		sentence1Words = AHANLP.getWordList(AHANLP.StandardSegment(sentence1, true));
    		sentence2Words = AHANLP.getWordList(AHANLP.StandardSegment(sentence2, true));
    	} else if ("NLP".equals(segType) || "NLP分词".equals(segType)) {
    		sentence1Words = AHANLP.getWordList(AHANLP.NLPSegment(sentence1, true));
    		sentence2Words = AHANLP.getWordList(AHANLP.NLPSegment(sentence2, true));
    	} else {
    		throw new IllegalArgumentException(String.format("非法参数 segType == %s", segType));
    	}
        return word2vec.sentenceSimilarity(sentence1Words, sentence2Words);
    }
}
