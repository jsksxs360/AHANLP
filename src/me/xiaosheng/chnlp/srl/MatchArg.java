package me.xiaosheng.chnlp.srl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 匹配论元的位置(词语列表索引、句子偏移量)
 * @author Xusheng
 */
class MatchArg {
	
	private static int predicateSymbolIdx = 12;         // 是否为谓词标志位
	private static int dataIdxStart = 14;               // SRL预测结果起始位置
	
	private String sentence;                            // 句子
	private List<String> words;                         // 句子分词后的词语列表
	private List<Integer> predicateIdx;                 // 谓词索引列表 
	private List<Map<String,List<Integer>>> argIdxMap;  // 论元索引映射表的列表，与谓词索引列表一一对应
	private Map<Integer, int[]> wordMap;                // 词语映射表
	
	/**
	 * 匹配词语在原始句子中的位置
	 * @param sentence 句子
	 * @param words 词语列表
	 * @return <词语索引，[start, end]>, sen[start, end+1] = word
	 * @throws Exception 匹配错误（无法找到词语）
	 */
	private static Map<Integer, int[]> MapWords(String sentence, List<String> words) throws Exception {
		Map<Integer, int[]> wordMap = new HashMap<Integer, int[]>();
		int global_offset = 0;
		for (int i = 0; i < words.size(); i++) {
			int idx = sentence.indexOf(words.get(i));
			if (idx != -1) {
				wordMap.put(i, new int[] {global_offset + idx, global_offset + idx + words.get(i).length() - 1});
				sentence = sentence.substring(idx + words.get(i).length());
				global_offset += idx + words.get(i).length();
			} else {
				throw new Exception("Match word error! Can't find word " + words.get(i));
			}
		}
		return wordMap;
	}
	
	/**
	 * 匹配出论元在词语列表中的位置
	 * @param sentence 句子原文
	 * @param words 句子分词后的词语列表
	 * @param data SRL识别结果
	 * @param predicateNum 触发词数量
	 * @throws Exception
	 */
	public MatchArg(String sentence, List<String> words, String data, int predicateNum) throws Exception {
		this.sentence = sentence;
		this.words = words.subList(1, words.size());
		this.wordMap = MapWords(this.sentence, this.words);
		this.predicateIdx = new ArrayList<>();
		this.argIdxMap = new ArrayList<>();
		for (int i = 0; i < predicateNum; i++)
			this.argIdxMap.add(new HashMap<String, List<Integer>>());
		if (predicateNum > 0) {
			String[] dataLines = data.trim().split("\n");
			if (this.words.size() != dataLines.length) {
				throw new Exception("parsing data error!");
			}
			for (int l = 0; l < dataLines.length; l++) {
				String[] items = dataLines[l].split("\t");
				if (items[predicateSymbolIdx].equals("Y"))
					this.predicateIdx.add(l);
				for (int i = 0; i < predicateNum; i++) {
					String label = items[dataIdxStart + i];
					if (!"_".equals(label)) {
						if (this.argIdxMap.get(i).containsKey(label))
							this.argIdxMap.get(i).get(label).add(l);
						else
							this.argIdxMap.get(i).put(label, new ArrayList<>(Arrays.asList(l)));
					}
				}
			}
		}
	}
	
	/**
	 * @param predicateIdx 谓词索引
	 * @return 谓词索引对应在列表中的位置
	 */
	private int mapPredicateIdx(int predicateIdx) {
		return this.predicateIdx.indexOf(predicateIdx);
	}
	
	/**
	 * 匹配论元（前后双向匹配）
	 * @param startIdx 可能的索引位置，对应论元中的某个词语
	 * @param argText 论元文本
	 * @return 论元索引, [start, end], wordList[start, end + 1] = arg
	 */
	private int[] matchWords(List<Integer> startIdx, String argText) {
		for (Integer start : startIdx) {
			if (!argText.contains(this.words.get(start)))
				continue;
			for (int i = 0; i <= start; i++) { // 前向指针
				StringBuilder sb = new StringBuilder();
				for (int k = start - i; k < start; k++)
					sb.append(this.words.get(k));
				for (int j = start; j < this.words.size(); j++) { // 后向指针
					if (sb.append(this.words.get(j)).toString().equals(argText))
						return new int[] {start - i, j};
					if (sb.toString().length() >= argText.length())
						break;
				}
			}
		}
		return new int[0];
	}
	
	/**
	 * 匹配出论元在词语列表中的位置
	 * @param predicateIdx 谓词索引
	 * @param label 论元SRL标签
	 * @param argText 论元文本
	 * @return 论元索引, [start, end], wordList[start, end+1] = arg
	 */
	public int[] getWordIdxs(int predicateIdx, String label, String argText) {
		int predicatePos = mapPredicateIdx(predicateIdx);
		if (predicatePos == -1 || predicatePos >= argIdxMap.size())
			return new int[0];
		Map<String, List<Integer>> argIdx = argIdxMap.get(mapPredicateIdx(predicateIdx));		
		if (argIdx.get(label) != null) {
			List<Integer> startIdx = argIdx.get(label);
			return matchWords(startIdx, argText);
		} else {
			return new int[0];
		}
	}
	
	/**
	 * 匹配出论元在句子中偏移位置
	 * @param predicateIdx 谓词索引
	 * @param label 论元SRL标签
	 * @param argText 论元文本
	 * @return 论元偏移量, [start, end], sen[start, end+1] = arg
	 */
	public int[] getWordOffset(int predicateIdx, String label, String argText) {
		int[] wordIdxs = getWordIdxs(predicateIdx, label, argText);
		if (wordIdxs.length == 2) {
			return new int[] { this.wordMap.get(wordIdxs[0])[0], this.wordMap.get(wordIdxs[1])[1]};
		} else {
			return new int[0];
		}
	}
}
