package me.xiaosheng.chnlp.summary;

import com.hankcs.hanlp.corpus.occurrence.Occurrence;
import com.hankcs.hanlp.corpus.occurrence.PairFrequency;
import com.hankcs.hanlp.mining.phrase.IPhraseExtractor;
import com.hankcs.hanlp.seg.common.Term;

import me.xiaosheng.chnlp.seg.Segment;

import java.util.LinkedList;
import java.util.List;

public class MutualInformationEntropyPhraseExtractor implements IPhraseExtractor {
	
	private String segType; // 分词器类型（Standard 或 NLP）
	
	public MutualInformationEntropyPhraseExtractor(String segType) {
		this.segType = segType;
	}
	
	@Override
    public List<String> extractPhrase(String text, int size) {
        List<String> phraseList = new LinkedList<String>();
        Occurrence occurrence = new Occurrence();
        for (List<Term> sentence : Segment.seg2sentence(this.segType, text, true)) {
            occurrence.addAll(sentence);
        }
        occurrence.compute();
        
        for (PairFrequency phrase : occurrence.getPhraseByScore())
        {
            if (phraseList.size() == size) break;
            phraseList.add(phrase.first + phrase.second);
        }
        return phraseList;
    }

}
