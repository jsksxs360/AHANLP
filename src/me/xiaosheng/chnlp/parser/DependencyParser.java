package me.xiaosheng.chnlp.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dependency.IDependencyParser;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;
import com.hankcs.hanlp.seg.common.Term;

public class DependencyParser {
    
    /**
     * 依存句法分析
     * @param segResult 分词结果
     * @return CONLL格式分析结果
     */
    public static CoNLLSentence parse(List<Term> segResult) {
        return parse(segResult, false);
    }
    
    /**
     * 依存句法分析
     * @param segResult 分词结果
     * @param englishTag 使用英语标签
     * @return CONLL格式分析结果
     */
    public static CoNLLSentence parse(List<Term> segResult, boolean englishTag) {
        IDependencyParser parser = new NeuralNetworkDependencyParser();
        if (englishTag)
            parser.enableDeprelTranslator(false);
        return parser.parse(segResult);
    }
    
    /**
     * 依存句法分析
     * @param sentence 句子
     * @return CONLL格式分析结果
     */
    public static CoNLLSentence parse(String sentence) {
        return parse(sentence, false);
    }
    
    /**
     * 依存句法分析
     * @param sentence 句子
     * @param englishTag 使用英语标签
     * @return CONLL格式分析结果
     */
    public static CoNLLSentence parse(String sentence, boolean englishTag) {
        IDependencyParser parser = new NeuralNetworkDependencyParser();
        if (englishTag)
            parser.enableDeprelTranslator(false);
        return parser.parse(sentence);
    }
    
    public static List<Term> getWordsInPath(CoNLLWord word) {
        return getWordsInPath(word, Integer.MAX_VALUE);
    }
    
    /**
     * 获得词语依存路径中的词语
     * @param word 词语
     * @param maxReturn 最大路径长度
     * @return 依存路径词语列表
     */
    public static List<Term> getWordsInPath(CoNLLWord word, int maxReturn) {
        List<Term> words = new ArrayList<Term>();
        if (word == CoNLLWord.ROOT || maxReturn < 1) return words;
        while (word != CoNLLWord.ROOT) {
            words.add(new Term(word.LEMMA, Nature.fromString(word.POSTAG)));
            word = word.HEAD;
            if (--maxReturn < 1) break;
        }
        return words;
    }
    
    /**
     * 获得词语依存路径
     * @param segResult 分词结果
     * @return 依存路径列表
     */
    public static List<List<Term>> getWordPaths(List<Term> segResult) {
        CoNLLWord[] wordArray = parse(segResult).getWordArray();
        List<List<Term>> wordPaths = new ArrayList<List<Term>>();
        for (CoNLLWord word : wordArray)
            wordPaths.add(getWordsInPath(word));
        return wordPaths;
    }
    
    /**
     * 获得词语依存路径
     * @param segResult 分词结果
     * @param maxReturn 最大路径长度
     * @return 依存路径列表
     */
    public static List<List<Term>> getWordPaths(List<Term> segResult, int maxReturn) {
        CoNLLWord[] wordArray = parse(segResult).getWordArray();
        List<List<Term>> wordPaths = new ArrayList<List<Term>>();
        for (CoNLLWord word : wordArray)
            wordPaths.add(getWordsInPath(word, maxReturn));
        return wordPaths;
    }
    
    /**
     * 获得词语依存路径
     * @param sentence 句子
     * @return 依存路径列表
     */
    public static List<List<Term>> getWordPaths(String sentence) {
        CoNLLWord[] wordArray = parse(sentence).getWordArray();
        List<List<Term>> wordPaths = new ArrayList<List<Term>>();
        for (CoNLLWord word : wordArray)
            wordPaths.add(getWordsInPath(word));
        return wordPaths;
    }
    
    /**
     * 获得词语依存路径
     * @param sentence 句子
     * @param maxReturn 最大路径长度
     * @return 依存路径列表
     */
    public static List<List<Term>> getWordPaths(String sentence, int maxReturn) {
        CoNLLWord[] wordArray = parse(sentence).getWordArray();
        List<List<Term>> wordPaths = new ArrayList<List<Term>>();
        for (CoNLLWord word : wordArray)
            wordPaths.add(getWordsInPath(word, maxReturn));
        return wordPaths;
    }
    
    /**
     * 获得词语的深度
     * @param word 词语
     * @return 词语在句法树中的深度
     */
    public static int calWordDepth(CoNLLWord word) {
        if (word == CoNLLWord.ROOT) return -1;
        int depth = 0;
        while(word.HEAD != CoNLLWord.ROOT) {
            depth++;
            word = word.HEAD;
        }
        return depth;
    }
    
    /**
     * 获取词语的深度
     * @param segResult 分词结果
     * @return 词语在句法树中的深度
     */
    public static Map<String, Integer> getWordsDepth(List<Term> segResult) {
        CoNLLSentence sentenceDep = parse(segResult);
        HashMap<String, Integer> wordsDepth = new HashMap<>();
        for (CoNLLWord wordDep : sentenceDep)
            wordsDepth.put(wordDep.LEMMA, calWordDepth(wordDep));
        return wordsDepth;
    }
    
    /**
     * 获取词语的深度
     * @param sentence 句子
     * @return 词语在句法树中的深度
     */
    public static Map<String, Integer> getWordsDepth(String sentence) {
        CoNLLSentence sentenceDep = parse(sentence);
        HashMap<String, Integer> wordsDepth = new HashMap<>();
        for (CoNLLWord wordDep : sentenceDep)
            wordsDepth.put(wordDep.LEMMA, calWordDepth(wordDep));
        return wordsDepth;
    }
    
    /**
     * 获取上层词语深度
     * @param segResult 分词结果
     * @param maxDepth 句法树最大深度 
     * @return 词语和对应深度
     */
    public static Map<String, Integer> getTopWordsDepth(List<Term> segResult, int maxDepth) {
        CoNLLSentence sentenceDep = parse(segResult);
        HashMap<String, Integer> wordsDepth = new HashMap<>();
        for (CoNLLWord wordDep : sentenceDep) {
            int depth = calWordDepth(wordDep);
            if (depth <= maxDepth)
                wordsDepth.put(wordDep.LEMMA, depth);
        }
        return wordsDepth;
    }
    
    /**
     * 获取上层词语深度
     * @param sentence 句子
     * @param maxDepth 句法树最大深度 
     * @return 词语和对应深度
     */
    public static Map<String, Integer> getTopWordsDepth(String sentence, int maxDepth) {
        CoNLLSentence sentenceDep = parse(sentence);
        HashMap<String, Integer> wordsDepth = new HashMap<>();
        for (CoNLLWord wordDep : sentenceDep) {
            int depth = calWordDepth(wordDep);
            if (depth <= maxDepth)
                wordsDepth.put(wordDep.LEMMA, depth);
        }
        return wordsDepth;
    }
    
    /**
     * 获取上层词语
     * @param segResult 分词结果
     * @param maxDepth 句法树最大深度 
     * @return 词语
     */
    public static List<String> getTopWords(List<Term> segResult, int maxDepth) {
        CoNLLSentence sentenceDep = parse(segResult);
        List<String> wordsDepth = new ArrayList<>();
        for (CoNLLWord wordDep : sentenceDep) {
            if (calWordDepth(wordDep) <= maxDepth)
                wordsDepth.add(wordDep.LEMMA);
        }
        return wordsDepth;
    }
    
    /**
     * 获取上层词语
     * @param sentence 句子
     * @param maxDepth 句法树最大深度 
     * @return 词语
     */
    public static List<String> getTopWords(String sentence, int maxDepth) {
        CoNLLSentence sentenceDep = parse(sentence);
        List<String> wordsDepth = new ArrayList<>();
        for (CoNLLWord wordDep : sentenceDep) {
            if (calWordDepth(wordDep) <= maxDepth)
                wordsDepth.add(wordDep.LEMMA);
        }
        return wordsDepth;
    }
}
