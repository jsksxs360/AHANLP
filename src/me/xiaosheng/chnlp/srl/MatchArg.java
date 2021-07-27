package me.xiaosheng.chnlp.srl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 匹配论元的位置
 * @author Xusheng
 */
class MatchArg {
    
    final static int predicateSymbolIdx = 12;           // 谓词标志位
    final static int dataIdxStart = 14;                 // SRL预测结果起始位置
    
    private String sentence;                            // 句子
    private List<String> words;                         // 句子分词后的词语列表
    private Map<Integer, int[]> wordMap;                // 词语序号-句内索引 映射表
    
    private List<Integer> predicatePostion;                  // 谓词位置列表 
    private List<Map<String,List<Integer>>> argPositionMap;  // 论元索引映射表的列表，与谓词索引列表一一对应
    
    /**
     * 匹配出词语的句内索引
     * @param sentence 句子
     * @param words 词语列表
     * @return <词语编号，[start, end]>, 
     * , sentence[start, end+1]=word
     * @throws Exception 匹配错误（无法找到词语）
     */
    public static Map<Integer, int[]> MapWords(String sentence, List<String> words) throws Exception {
        Map<Integer, int[]> wordMap = new HashMap<Integer, int[]>();
        int start = 0;
        for (int i = 0; i < words.size(); i++) {
            int idx = sentence.indexOf(words.get(i));
            if (idx != -1) {
                wordMap.put(
                    i, new int[] {
                            start + idx, // start index
                            start + idx + words.get(i).length() - 1 // end index
                    }
                );
                sentence = sentence.substring(idx + words.get(i).length());
                start += idx + words.get(i).length();
            } else {
                throw new Exception("match word error! can't find word " + words.get(i));
            }
        }
        return wordMap;
    }
    
    /**
     * 获取词语的句内索引
     * @param wordPosition 词语列表中的位置
     * @return 词语的句内索引, [start, end]
     * , sentence[start, end+1]=word
     * 失败，返回 [-1, -1]
     */
    public int[] getWordIdxs(int wordPosition) {
        if (this.wordMap.containsKey(wordPosition))
            return this.wordMap.get(wordPosition);
        else
            return new int[] {-1, -1};
    }
    
    /**
     * 获取词语的句内偏移量
     * @param wordPosition 词语列表中的位置
     * @return 词语的句内偏移量
     * 失败，返回 -1
     */
    public int getWordOffset(int wordPosition) {
        return this.wordMap.get(wordPosition)[0];
    }
    
    /**
     * 匹配论元的位置
     * @param sentence 句子原文
     * @param words 句子分词后的词语列表
     * @param data SRL识别结果
     * @param predicateNum 触发词数量
     * @throws Exception 句子词语匹配错误、解析数据错误
     */
    public MatchArg(String sentence, List<String> words, String data, int predicateNum) throws Exception {
        this.sentence = sentence;
        this.words = words;
        this.wordMap = MapWords(this.sentence, this.words);
        this.predicatePostion = new ArrayList<>();
        this.argPositionMap = new ArrayList<>();
        for (int i = 0; i < predicateNum; i++)
            this.argPositionMap.add(new HashMap<String, List<Integer>>());
        if (predicateNum > 0) {
            String[] dataLines = data.trim().split("\n");
            if (this.words.size() != dataLines.length) {
                throw new Exception("parsing data error!");
            }
            try {
                for (int l = 0; l < dataLines.length; l++) {
                    String[] items = dataLines[l].split("\t");
                    if (items[predicateSymbolIdx].equals("Y"))
                        this.predicatePostion.add(l);
                    for (int i = 0; i < predicateNum; i++) { // parse arg for each predicate
                        String label = items[dataIdxStart + i];
                        if (!label.equals("_")) {
                            if (this.argPositionMap.get(i).containsKey(label))
                                this.argPositionMap.get(i).get(label).add(l);
                            else
                                this.argPositionMap.get(i).put(label, new ArrayList<>(Arrays.asList(l)));
                        }
                    }
                }
            } catch (Exception e) {
                throw new Exception("parsing data error!");
            }
            
        }
    }
    
    /**
     * @param predicateIdx 谓词序号
     * @return 谓词序号在列表中的位置
     */
    private int mapPredicatePosition(int predicatePosition) {
        return this.predicatePostion.indexOf(predicatePosition);
    }
    
    /**
     * 匹配论元（前后双向匹配）
     * @param positions 可能的编号，对应论元中的某个词语
     * @param argText 论元文本
     * @return 论元编号, [start, end]
     * , wordList[start, end + 1]=argument
     * 匹配失败，返回 [-1, -1]
     */
    private int[] matchWords(List<Integer> positions, String argText) {
        for (Integer position : positions) {
            if (!argText.contains(this.words.get(position)))
                continue;
            for (int i = 0; i <= position; i++) { // 前向指针
                StringBuilder sb = new StringBuilder();
                for (int k = position - i; k < position; k++)
                    sb.append(this.words.get(k));
                for (int j = position; j < this.words.size(); j++) { // 后向指针
                    if (sb.append(this.words.get(j)).toString().equals(argText))
                        return new int[] {
                            position - i, // start position
                            j             // end position
                        };
                    if (sb.toString().length() >= argText.length())
                        break;
                }
            }
        }
        return new int[] {-1, -1};
    }
    
    /**
     * 匹配出论元在词语列表中的位置
     * @param predicatePosition 谓词编号
     * @param label 论元SRL标签
     * @param argText 论元文本
     * @return 论元编号, [start, end]
     * , wordList[start, end+1]=argument
     * 匹配失败，返回 [-1, -1]
     */
    public int[] getArgPositions(int predicatePosition, String label, String argText) {
        int predicateNo = mapPredicatePosition(predicatePosition);
        if (predicateNo == -1)
            return new int[] {-1, -1};
        Map<String, List<Integer>> argPosition = argPositionMap.get(predicateNo);        
        if (argPosition.get(label) != null) {
            List<Integer> positions = argPosition.get(label);
            return matchWords(positions, argText);
        } else {
            return new int[] {-1, -1};
        }
    }
    
    /**
     * 匹配出论元的句内索引
     * @param predicatePosition 谓词编号
     * @param label 论元SRL标签
     * @param argText 论元文本
     * @return 论元的句内索引, [start, end]
     * , sentence[start, end+1]=argument
     */
    public int[] getArgIdxs(int predicatePosition, String label, String argText) {
        int[] wordPositions = getArgPositions(predicatePosition, label, argText);
        if (wordPositions[0] != -1 && wordPositions[1] != -1) {
            return new int[] { 
                this.getWordIdxs(wordPositions[0])[0], // start index
                this.getWordIdxs(wordPositions[1])[1]  // end index
            };
        } else {
            return new int[] {-1, -1};
        }
    }
    
    /**
     * 匹配出论元的句内偏移量
     * @param predicatePosition 谓词编号
     * @param label 论元SRL标签
     * @param argText 论元文本
     * @return 论元的句内偏移量，匹配失败返回-1
     */
    public int getArgOffset(int predicatePosition, String label, String argText) {
        int[] wordIdxs = getArgPositions(predicatePosition, label, argText);
        if (wordIdxs[0] != -1 && wordIdxs[1] != -1) {
            return this.getWordIdxs(wordIdxs[0])[0];
        } else {
            return -1;
        }
    }
}
