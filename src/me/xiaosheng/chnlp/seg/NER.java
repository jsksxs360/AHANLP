package me.xiaosheng.chnlp.seg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.tokenizer.lexical.AbstractLexicalAnalyzer;

/**
 * 命名实体识别
 * 
 * @author Xusheng
 */
public class NER {

    public static AbstractLexicalAnalyzer perceptronAnalyzer;
    public static AbstractLexicalAnalyzer CRFAnalyzer;

    static {
        try {
            perceptronAnalyzer = new PerceptronLexicalAnalyzer();
            CRFAnalyzer = new CRFLexicalAnalyzer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 命名实体识别<br>
     * 感知机和CRF模型
     * 
     * @param content 文本
     * @return 实体列表
     */
    public static List<NERTerm> namedEntityRecognition(String content) {
        Set<NERTerm> result = new HashSet<NERTerm>();
        for (IWord word : perceptronAnalyzer.analyze(content)) {
            if (word.getLabel().startsWith("ns")) {
                result.add(new NERTerm(word.getValue(), "loc"));
            } else if (word.getLabel().startsWith("nr")) {
                result.add(new NERTerm(word.getValue(), "per"));
            } else if (word.getLabel().startsWith("nt")) {
                result.add(new NERTerm(word.getValue(), "org"));
            }
        }
        String timeTemp = "";
        for (IWord word : CRFAnalyzer.analyze(content)) {
            if (word.getLabel().equals("t")) {
                timeTemp += word.getValue();
            } else {
                if (!timeTemp.isEmpty()) {
                    result.add(new NERTerm(timeTemp, "time"));
                    timeTemp = "";
                }
                if (word.getLabel().startsWith("nr"))
                    result.add(new NERTerm(word.getValue(), "per"));
            }
        }
        if (!timeTemp.isEmpty())
            result.add(new NERTerm(timeTemp, "time"));
        return new ArrayList<NERTerm>(result);
    }

    /**
     * 获取时间信息
     * 
     * @param NERResult NER结果列表
     * @return 时间信息列表
     */
    public static List<String> getTimeInfo(List<NERTerm> NERResult) {
        List<String> result = new ArrayList<String>();
        for (NERTerm term : NERResult) {
            if (term.type.equals("time"))
                result.add(term.word);
        }
        return result;
    }

    /**
     * 获取地名信息
     * 
     * @param NERResult NER结果列表
     * @return 地名信息列表
     */
    public static List<String> getLocInfo(List<NERTerm> NERResult) {
        List<String> result = new ArrayList<String>();
        for (NERTerm term : NERResult) {
            if (term.type.equals("loc"))
                result.add(term.word);
        }
        return result;
    }

    /**
     * 获取人名信息
     * 
     * @param NERResult NER结果列表
     * @return 人名信息列表
     */
    public static List<String> getPerInfo(List<NERTerm> NERResult) {
        List<String> result = new ArrayList<String>();
        for (NERTerm term : NERResult) {
            if (term.type.equals("per"))
                result.add(term.word);
        }
        return result;
    }

    /**
     * 获取机构名信息
     * 
     * @param NERResult NER结果列表
     * @return 机构名信息列表
     */
    public static List<String> getOrgInfo(List<NERTerm> NERResult) {
        List<String> result = new ArrayList<String>();
        for (NERTerm term : NERResult) {
            if (term.type.equals("org"))
                result.add(term.word);
        }
        return result;
    }
}
