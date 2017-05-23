package test.demo;

import java.util.Arrays;
import java.util.List;

import com.hankcs.hanlp.seg.common.Term;

import me.xiaosheng.chnlp.AHANLP;
import me.xiaosheng.chnlp.seg.POSFilter;

public class SegDemo {

    public static void main(String[] args) {
        String content = "目前，航空母舰主船体完成建造，动力、电力等主要系统设备安装到位。" + 
        		"出坞下水是航空母舰建设的重大节点之一，标志着我国自主设计建造航空母舰取得重大阶段性成果。" + 
        		"下一步，该航空母舰将按计划进行系统设备调试和舾装施工，并全面开展系泊试验。";
        // 标准分词
        List<Term> stdSegResult = AHANLP.StandardSegment(content);
        System.out.println("标准分词:\n" + stdSegResult);
        // NLP分词
        List<Term> nlpSegResult = AHANLP.NLPSegment(content);
        System.out.println("NLP分词:\n" + nlpSegResult);
        // 标准分词(去停用词)
        stdSegResult = AHANLP.StandardSegment(content, true);
        List<String> stdWordList = AHANLP.getWordList(stdSegResult);
        System.out.println("标准分词(去停用词):\n" + stdWordList);
        // NLP分词(去停用词)
        nlpSegResult = AHANLP.NLPSegment(content, true);
        List<String> nlpWordList = AHANLP.getWordList(nlpSegResult);
        System.out.println("NLP分词(去停用词):\n" + nlpWordList);
        // 标准分词(去停用词，保留实词)
        stdSegResult = AHANLP.StandardSegment(content, true);
        //POSFilter.selectRealWords(stdSegResult);
        POSFilter.selectPOS(stdSegResult, Arrays.asList("n", "ns", "nr", "nt", "nz", "v", "vd", "vn", "a", "ad", "an", "d"));
        System.out.println("标准分词(去停用词，保留实词):\n" + AHANLP.getWordList(stdSegResult));
        // 分句
        System.out.println("切分句子:");
        List<String> senList = AHANLP.splitSentence(content);
        for (int i = 0; i < senList.size(); i++)
            System.out.println((i + 1) + " : " + senList.get(i));
        // 对句子列表分词
        System.out.println("对句子列表分词(去停用词):");
        List<List<String>> senWordList = AHANLP.splitWordInSentences(senList, true);
        for (int i = 0; i < senWordList.size(); i++)
            System.out.println((i + 1) + " : " + senWordList.get(i));
    }
}
