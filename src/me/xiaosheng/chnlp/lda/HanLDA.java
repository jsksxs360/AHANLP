package me.xiaosheng.chnlp.lda;

import java.io.IOException;
import java.util.Map;

public class HanLDA {
	
    /**
     * 训练模型
     * @param trainFolderPath 训练语料文件所在目录
     * @param trainTopicNum 训练的主题个数
     * @param saveModelFilePath 模型文件保存路径
     * @param printLDAModel 是否打印产生的模型
     * @throws IOException 
     * @throws Exception
     */
    public static void train(String trainFolderPath, int trainTopicNum, String saveModelFilePath, boolean printLDAModel) throws IOException {
        //载入语料，预处理
        System.out.println("载入语料  预处理中...");
        Corpus corpus = Corpus.load(trainFolderPath);
        System.out.println("预处理完毕！");
        //创建LDA采样器
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
        if (trainTopicNum < 1)
            trainTopicNum = 1; //训练的主题个数至少为1;
        //训练指定个数的主题
        ldaGibbsSampler.gibbs(trainTopicNum);
        //phi矩阵是唯一有用的东西，可以用LdaUtil来展示最终的结果
        double[][] phi = ldaGibbsSampler.getPhi();
        //存储产生的LDA模型
        LDAModel.saveModelFile(saveModelFilePath, phi, corpus.getVocabulary());
        if (printLDAModel) { //展示产生的模型，每个主题展示出现概率最高的10个词汇
            Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 10);
            LdaUtil.explain(topicMap);
        }
        //存储LDA展示文件，每个主题展示出现概率最高的20个词汇
        LDAModel.saveLDAShowFile(saveModelFilePath + ".txt", phi, corpus.getVocabulary(), 20);
    }
    
    /**
     * 预测文档的主题分布
     * @param modelFilePath 模型文件路径
     * @param documentFilePath 要预测的文档路径
     * @param printResult 是否打印预测结果
     * @return 主题分布数组
     * @throws IOException
     */
    public static double[] inference(String modelFilePath, String documentFilePath, boolean printResult) throws IOException {
        LDAModel ldaModel = new LDAModel(modelFilePath);
        int[] document = Corpus.loadDocument(documentFilePath, ldaModel.getVocabulary());
        //进行文档的主题分布预测
        double[] tp = LdaGibbsSampler.inference(ldaModel.getPhiMatrix(), document);
        if (printResult) {
            for(int i = 0; i < tp.length; i++) {
                System.out.println("topic" + i + "\tprobability:\t" + tp[i]);
            }
        }
        return tp;
    }
    
    /**
     * 预测文档最可能的主题号
     * @param modelFilePath 模型文件路径
     * @param documentFilePath 要预测的文档路径
     * @return 主题号
     * @throws IOException
     */
    public static int inference(String modelFilePath, String documentFilePath) throws IOException {
        double[] result = inference(modelFilePath, documentFilePath, false);
        return getMaxProbTopic(result);
    }
    
    /**
     * 获得最可能的主题号
     * @param topicProb
     * @return
     */
    private static int getMaxProbTopic(double[] topicProb) {
        int index = -1;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < topicProb.length; i++) {
            if (topicProb[i] > max) {
                max = topicProb[i];
                index = i;
            }
        }
        return index;
    }
}
