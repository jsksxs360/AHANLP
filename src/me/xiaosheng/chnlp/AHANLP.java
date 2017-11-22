package me.xiaosheng.chnlp;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ansj.vec.domain.WordEntry;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.seg.common.Term;

import me.xiaosheng.chnlp.distance.Word2VecSimi;
import me.xiaosheng.chnlp.lda.HanLDA;
import me.xiaosheng.chnlp.parser.DependencyParser;
import me.xiaosheng.chnlp.seg.NERTerm;
import me.xiaosheng.chnlp.seg.Segment;
import me.xiaosheng.chnlp.summary.TextRankKeyword;
import me.xiaosheng.chnlp.summary.TextRankSentence;

public class AHANLP {

    /**
     * 标准分词
     * @param content 文本
     * @return
     */
    public static List<Term> StandardSegment(String content) {
        return Segment.StandardSegment(content);
    }
    
    /**
     * 标准分词
     * @param content 文本
     * @param filterStopWord 滤掉停用词
     * @return
     */
    public static List<Term> StandardSegment(String content, boolean filterStopWord) {
        return Segment.StandardSegment(content, filterStopWord);
    }
    
    /**
     * 带有新词发现功能的分词
     * @param content 文本
     * @return
     */
    public static List<Term> NLPSegment(String content) {
        return Segment.NLPSegment(content);
    }
    
    /**
     * 带有新词发现功能的分词
     * @param content 文本
     * @param filterStopWord 滤掉停用词
     * @return
     */
    public static List<Term> NLPSegment(String content, boolean filterStopWord) {
        return Segment.NLPSegment(content, filterStopWord);
    }
    
    /**
     * 获得词语列表
     * @param termList 分词结果
     * @return
     */
    public static List<String> getWordList(List<Term> termList) {
        return Segment.getWordList(termList);
    }
    
    /**
     * 获取词性列表
     * @param termList 分词结果
     * @return
     */
    public static List<String> getNatureList(List<Term> termList) {
        return Segment.getNatureList(termList);
    }
    
    /**
     * 分句
     * @param document 文本
     * @return 句子列表
     */
    public static List<String> splitSentence(String document) {
        return Segment.splitSentence(document);
    }
    
    /**
     * 分句
     * @param document 文本
     * @param splitReg 切分符号(正则表达式)
     * @return 句子列表
     */
    public static List<String> splitSentence(String document, String splitReg) {
        return Segment.splitSentence(document, splitReg);
    }
    
    /**
     * 对句子列表分词
     * @param sentenceList 句子列表
     * @param filterStopWord 滤掉停用词
     * @return
     */
    public static List<List<String>> splitWordInSentences(List<String> sentenceList, boolean filterStopWord) {
        return Segment.splitWordInSentences(sentenceList, filterStopWord);
    }
    
    /**
     * 命名实体识别
     * @param content 文本
     * @return
     */
    public static List<NERTerm> NER(String content) {
        return me.xiaosheng.chnlp.seg.NER.namedEntityRecognition(content);
    }
    
    /**
     * 提取关键词
     * @param document 文档
     * @param num 关键词数量
     * @return 关键词列表
     */
    public static List<String> extractKeyword(String document, int num) {
        return TextRankKeyword.getKeywordList(document, num);
    }
    
    /**
     * 提取关键句
     * @param document 文档
     * @param num 关键句数量
     * @return 关键句列表
     */
    public static List<String> extractKeySentence(String document, int num) {
        return TextRankSentence.getTopSentenceList(document, num);
    }
    
    /**
     * 提取摘要
     * @param document 文档
     * @param maxLength 最大长度
     * @return 摘要
     */
    public static String extractSummary(String document, int maxLength) {
        return TextRankSentence.getSummary(document, maxLength);
    }
    
    /**
     * 词语相似度
     * @param word1 词语1
     * @param word2 词语2
     * @return
     */
    public static float wordSimilarity(String word1, String word2) {
        return Word2VecSimi.wordSimilarity(word1, word2);
    }
    
    /**
     * 获取相似词语
     * @param word 词语
     * @param maxReturnNum 最大返回词数
     * @return
     */
    public static Set<WordEntry> getSimilarWords(String word, int maxReturnNum) {
        return Word2VecSimi.getSimilarWords(word, maxReturnNum);
    }
    
    /**
     * 句子相似度
     * @param sentence1 句子1
     * @param sentence2 句子2
     * @return
     */
    public static float sentenceSimilarity(String sentence1, String sentence2) {
        return Word2VecSimi.sentenceSimilarity(sentence1, sentence2);
    }
    
    /**
     * 训练LDA模型
     * @param trainFolderPath 训练语料文件所在目录
     * @param trainTopicNum 训练的主题个数
     * @param saveModelFilePath 模型文件保存路径
     * @throws IOException
     */
    public static void trainLDAModel(String trainFolderPath, int trainTopicNum, String saveModelFilePath) throws IOException {
    	HanLDA.train(trainFolderPath, trainTopicNum, saveModelFilePath, true);
    }
    
    /**
     * 主题预测
     * @param documentFilePath 文档文件路径
     * @return 最可能的主题号
     */
    public static int topicInference(String documentFilePath) {
        return topicInference(Config.hanLDAModelPath(), documentFilePath);
    }
    
    /**
     * LDA主题预测
     * @param modelFilePath LDA模型路径
     * @param documentFilePath 文档文件路径
     * @return 最可能的主题号
     */
    public static int topicInference(String modelFilePath, String documentFilePath) {
        try {
            return HanLDA.inference(modelFilePath, documentFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * 依存句法分析
     * @param sentence 句子
     * @return CONLL格式分析结果
     */
    public static CoNLLSentence DependencyParse(String sentence) {
        return DependencyParser.parse(sentence);
    }
    
    /**
     * 依存句法分析
     * @param sentence 句子
     * @param englishTag 使用英文标签
     * @return CONLL格式分析结果
     */
    public static CoNLLSentence DependencyParse(String sentence, boolean englishTag) {
        return DependencyParser.parse(sentence, englishTag);
    }
    
    /**
     * 获得词语依存路径
     * @param segResult
     * @return 依存路径列表
     */
    public static List<List<Term>> getWordPathsInDST(String sentence) {
        return DependencyParser.getWordPaths(sentence);
    }
    
    /**
     * 获取词语在依存句法树中的深度
     * @param sentence 句子
     * @return 词语在依存句法树中的深度
     */
    public static Map<String, Integer> getWordsDepthInDST(String sentence) {
        return DependencyParser.getWordsDepth(sentence);
    }
    
    /**
     * 获取依存句法树上层词语
     * @param sentence 句子
     * @param maxDepth 句法树最大深度 
     * @return 词语
     */
    public static List<String> getTopWordsInDST(String sentence, int maxDepth) {
        return DependencyParser.getTopWords(sentence, maxDepth);
    }
    
    /**
     * 简体中文转繁体
     * @param simplifiedChinese 简体中文
     * @return 繁体中文
     */
    public static String convertSC2TC(String simplifiedChinese) {
        return HanLP.convertToTraditionalChinese(simplifiedChinese);
    }
    /**
     * 繁体中文转简体
     * @param TraditionalChinese 繁体中文
     * @return 简体中文
     */
    public static String convertTC2SC(String TraditionalChinese) {
        return HanLP.convertToSimplifiedChinese(TraditionalChinese);
    }
}
