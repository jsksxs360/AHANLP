package test.demo;

import java.util.List;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.seg.common.Term;

import me.xiaosheng.chnlp.AHANLP;

public class Parser {

	public static void main(String[] args) {
		
		String sentence = "北京是中国的首都";
		
		CoNLLSentence zhDeps = AHANLP.DependencyParse(sentence);
		System.out.println("\n中文标签 --->");
		for (CoNLLWord dep : zhDeps)
            System.out.printf("%s --(%s)--> %s\n", dep.LEMMA, dep.DEPREL, dep.HEAD.LEMMA);
		
		CoNLLSentence enDeps = AHANLP.DependencyParse(sentence, true);
		System.out.println("\n英文标签 --->");
		for (CoNLLWord dep : enDeps)
            System.out.printf("%s --(%s)--> %s\n", dep.LEMMA, dep.DEPREL, dep.HEAD.LEMMA);
		
		//自定义分词
		String sentence2 = "金正男在马来西亚被刺杀";
		List<Term> regResult = AHANLP.NLPSegment(sentence2);
		System.out.println("\nSeg Result: " + regResult);
		CoNLLSentence Deps = AHANLP.DependencyParse(sentence2);
		for (CoNLLWord dep : Deps)
            System.out.printf("%s --(%s)--> %s\n", dep.LEMMA, dep.DEPREL, dep.HEAD.LEMMA);
	}
}
