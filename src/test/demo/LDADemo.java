package test.demo;

import java.io.IOException;

import me.xiaosheng.chnlp.Config;
import me.xiaosheng.chnlp.AHANLP;
import me.xiaosheng.chnlp.lda.HanLDA;

public class LDADemo {

    public static void main(String[] args) throws IOException {
        // 主题分布预测
        int topicNum510 = AHANLP.topicInference("data/mini/军事_510.txt");
        System.out.println("军事_510.txt 最可能的主题号为: " + topicNum510);
        int topicNum610 = AHANLP.topicInference("data/mini/军事_610.txt");
        System.out.println("军事_610.txt 最可能的主题号为: " + topicNum610);
        
        double[] topicProb510 = HanLDA.inference(Config.hanLDAModelPath(), "data/mini/军事_510.txt", false);
        System.out.println("\n军事_510.txt 的主题概率分布------------------------>");
        for(int i = 0; i < topicProb510.length; i++)
            System.out.println("topic" + i + "\tprobability:\t" + topicProb510[i]);
        double[] topicProb610 = HanLDA.inference(Config.hanLDAModelPath(), "data/mini/军事_610.txt", false);
        System.out.println("\n军事_610.txt 的主题概率分布------------------------>");
        for(int i = 0; i < topicProb610.length; i++)
            System.out.println("topic" + i + "\tprobability:\t" + topicProb610[i]);

        // 训练自定义LDA模型
        System.out.println("正在训练 LDA 模型，请稍等...");
        AHANLP.trainLDAModel("data/mini/", 10, "data/model/testLDA.model");
        // 预测文档的主题分布
        int topicNum710 = AHANLP.topicInference("data/model/testLDA.model", "data/mini/军事_710.txt");
        System.out.println("军事_710.txt 最可能的主题号为: " + topicNum710);
        int topicNum810 = AHANLP.topicInference("data/model/testLDA.model", "data/mini/军事_810.txt");
        System.out.println("军事_810.txt 最可能的主题号为: " + topicNum810);
    }
}
