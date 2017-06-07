package me.xiaosheng.chnlp.seg;

import java.util.ArrayList;
import java.util.List;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

/**
 * 命名实体识别
 * 
 * @author Xusheng
 */
public class NER {
    
    /**
     * 命名实体识别
     * @param content 文本
     * @return
     */
    public static List<NERTerm> namedEntityRecognition(String content) {
        List<NERTerm> result = new ArrayList<NERTerm>();
        // 进行标准分词和NLP分词
        List<Term> segResult = StandardTokenizer.segment(content);
        List<Term> nlpSegResult = NLPTokenizer.segment(content);
        // 标准分词提取地名、人名、机构名
        for (Term term : segResult) {
            if (term.nature.toString().startsWith("ns")) {
                result.add(new NERTerm(term.word, "loc"));
            } else if (term.nature.toString().startsWith("nr")) {
                result.add(new NERTerm(term.word, "per"));
            } else if (term.nature.toString().startsWith("nt")) {
                result.add(new NERTerm(term.word, "org"));
            }
        }
        // NLP分词提取时间
        String temp = "";
        for (int i = 0; i < nlpSegResult.size(); i++) {
            Term term = nlpSegResult.get(i);
            // 处理 m + qt，例如: 3/m 月/qt
            if (term.nature.toString().equals("m") && (i+1 < nlpSegResult.size()) && nlpSegResult.get(i+1).nature.toString().equals("qt")) {
                temp += (term.word + nlpSegResult.get(i+1).word);
                i++;
            } else if (term.nature.toString().equals("t")) {
                temp += term.word;
            } else {
                if (!temp.isEmpty()) {
                    result.add(new NERTerm(temp, "time"));
                    temp = "";
                }
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
