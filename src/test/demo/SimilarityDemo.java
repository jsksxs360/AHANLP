package test.demo;

import java.util.List;
import java.util.Set;

import com.ansj.vec.domain.WordEntry;

import me.xiaosheng.chnlp.AHANLP;
import me.xiaosheng.chnlp.distance.Word2VecSimi;

public class SimilarityDemo {

    public static void main(String[] args) {
        System.out.println("猫 | 狗 : " + AHANLP.wordSimilarity("猫", "狗"));
        System.out.println("计算机 | 电脑 : " + AHANLP.wordSimilarity("计算机", "电脑"));
        System.out.println("计算机 | 男人 : " + AHANLP.wordSimilarity("计算机", "男人"));
        
        Set<WordEntry> similarWords = AHANLP.getSimilarWords("微积分", 10);
        System.out.println("\n与\"微积分\"语义相似的前10个词语:");
        for(WordEntry word : similarWords) {
            System.out.println(word.name + " : " + word.score);
        }
        
        String s1 = "苏州有多条公路正在施工，造成局部地区汽车行驶非常缓慢。";
        String s2 = "苏州最近有多条公路在施工，导致部分地区交通拥堵，汽车难以通行。";
        String s3 = "苏州是一座美丽的城市，四季分明，雨量充沛。";
        
        System.out.println();
        System.out.println("s1 : " + s1 + "\ns2 : " + s2 + "\ns3 : " + s3);
        System.out.println("s1 | s1 : " + AHANLP.sentenceSimilarity(s1, s1));
        System.out.println("s1 | s2 : " + AHANLP.sentenceSimilarity(s1, s2));
        System.out.println("s1 | s3 : " + AHANLP.sentenceSimilarity(s1, s3));
        
        System.out.println();
        List<String> sen1words = AHANLP.getWordList(AHANLP.NLPSegment(s1, true));
        List<String> sen2words = AHANLP.getWordList(AHANLP.NLPSegment(s2, true));
        List<String> sen3words = AHANLP.getWordList(AHANLP.NLPSegment(s3, true));
        System.out.println("s1 | s1 : " + Word2VecSimi.sentenceSimilarity(sen1words, sen1words));
        System.out.println("s1 | s2 : " + Word2VecSimi.sentenceSimilarity(sen1words, sen2words));
        System.out.println("s1 | s3 : " + Word2VecSimi.sentenceSimilarity(sen1words, sen3words));
    }
}
