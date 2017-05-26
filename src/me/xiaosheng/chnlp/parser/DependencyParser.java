package me.xiaosheng.chnlp.parser;

import java.util.List;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
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
}
