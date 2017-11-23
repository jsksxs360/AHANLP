package test.demo;

import java.util.List;
import java.util.Map;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.seg.common.Term;

import me.xiaosheng.chnlp.AHANLP;
import me.xiaosheng.chnlp.parser.DependencyParser;

public class ParserDemo {

    public static void main(String[] args) {
        
        String sentence = "北京是中国的首都";
        
        CoNLLSentence zhDeps = AHANLP.DependencyParse(sentence);
        
        // 直接遍历
        System.out.println("\n中文标签 --->");
        for (CoNLLWord dep : zhDeps)
            System.out.printf("%s --(%s)--> %s\n", dep.LEMMA, dep.DEPREL, dep.HEAD.LEMMA);
        
        // 以数组方式遍历
        System.out.println("\n遍历结果数组:");
        CoNLLWord[] wordArray = zhDeps.getWordArray();
        for (int i = 0; i < wordArray.length; i++) {
            CoNLLWord word = wordArray[i];
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        
        // 从某个节点一路遍历到虚根
        CoNLLWord head = wordArray[2];
        System.out.println("\n从“" + head.LEMMA + "”遍历到虚根的路径为:");
        while (head != null) {
            if (head == CoNLLWord.ROOT) System.out.println(head.LEMMA);
            else System.out.printf("%s --(%s)--> ", head.LEMMA, head.DEPREL);
            head = head.HEAD;
        }
        
        // 打印每个词语的依存路径
        System.out.println("\n词语依存路径:");
        List<List<Term>> wordPaths = AHANLP.getWordPathsInDST(sentence);
        for (List<Term> wordPath : wordPaths) {
            System.out.println(wordPath.get(0).word + " : " + AHANLP.getWordList(wordPath));
        }
        // 每个词语依存路径最大为2
//        wordPaths = DependencyParser.getWordPaths(sentence, 2);
//        for (List<Term> wordPath : wordPaths) {
//            System.out.println(wordPath.get(0).word + " : " + AHANLP.getWordList(wordPath));
//        }
        
        // 打印每个词语在句法树中的深度
        System.out.println("\n词语在句法树中的深度:");
        Map<String, Integer> wordsDepth = AHANLP.getWordsDepthInDST(sentence);
        for (Map.Entry<String, Integer> entry : wordsDepth.entrySet()) {
            System.out.println(entry.getKey() + " --- " + entry.getValue());
        }
        
        // 获取上层词语深度
        System.out.println("\n句法树前2层词语的深度:");
        Map<String, Integer> wordsDepth2 = DependencyParser.getTopWordsDepth(sentence, 1);
        for (Map.Entry<String, Integer> entry : wordsDepth2.entrySet()) {
            System.out.println(entry.getKey() + " --- " + entry.getValue());
        }
        
        // 获取上层词语
        System.out.println("\n句法树前3层的词语:");
        List<String> words = AHANLP.getTopWordsInDST(sentence, 2);
        System.out.println(words);
        
        // 英文标签
        CoNLLSentence enDeps = AHANLP.DependencyParse(sentence, true);
        System.out.println("\n英文标签 --->");
        for (CoNLLWord dep : enDeps)
            System.out.printf("%s --(%s)--> %s\n", dep.LEMMA, dep.DEPREL, dep.HEAD.LEMMA);
        
        //自定义分词
        String sentence2 = "金正男在马来西亚被刺杀";
        List<Term> regResult = AHANLP.NLPSegment(sentence2);
        System.out.println("\nSeg Result: " + regResult);
        CoNLLSentence Deps = DependencyParser.parse(regResult);
        for (CoNLLWord dep : Deps)
            System.out.printf("%s --(%s)--> %s\n", dep.LEMMA, dep.DEPREL, dep.HEAD.LEMMA);
    }
}
