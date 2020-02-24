package me.xiaosheng.chnlp.seg;

import java.util.ArrayList;
import java.util.List;

import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;

/**
 * 命名实体识别
 * 
 * @author Xusheng
 */
public class NER {
    
    /**
     * 命名实体识别<br>
     * NLP分词器感知机模型
     * @param content 文本
     * @return 实体列表
     */
    public static List<NERTerm> namedEntityRecognition(String content) {
    	List<NERTerm> result = new ArrayList<NERTerm>();
		for (IWord word : NLPTokenizer.analyze(content)) {
			if (word.getLabel().startsWith("ns")) {
                result.add(new NERTerm(word.getValue(), "loc"));
            } else if (word.getLabel().startsWith("nr")) {
                result.add(new NERTerm(word.getValue(), "per"));
            } else if (word.getLabel().startsWith("nt")) {
                result.add(new NERTerm(word.getValue(), "org"));
            } else if (word.getLabel().equals("t")) {
            	result.add(new NERTerm(word.getValue(), "time"));
            }
        }
        return result;
    }
    
    /**
     * 获取时间信息
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
