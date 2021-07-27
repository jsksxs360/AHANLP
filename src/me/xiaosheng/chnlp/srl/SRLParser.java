package me.xiaosheng.chnlp.srl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import me.xiaosheng.chnlp.AHANLP;
import me.xiaosheng.chnlp.Config;
import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.corpus.Yield;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;
import se.lth.cs.srl.options.FullPipelineOptions;

public class SRLParser {
    
    static CompletePipeline pipeline;
    static {
        try {
            Logger.getGlobal().info("loading SRL models...");
            FullPipelineOptions options = new CompletePipelineCMDLineOptions();
            options.parseCmdLineArgs(new String[]{
                "chi",                                        // language
                "-tagger", Config.srlTaggerModelPath(),       // tagger model
                "-parser", Config.srlParserModelPath(),       // parsing model
                "-srl", Config.srlModelPath(),                // SRL model
            });
            pipeline = CompletePipeline.getCompletePipeline(options);
            Logger.getGlobal().info("load model successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * SRL解析长文本（可以包含多个句子）
     * @param content 长文本
     * @return 谓词列表
     */
    public static List<SRLPredicate> parseContent(String content) {
        List<SRLPredicate> results = new LinkedList<>();
        List<String> senList = AHANLP.splitSentence(content);
        try {
            Map<Integer, int[]> sentMap = MatchArg.MapWords(content, senList);
            for (int i = 0; i < senList.size(); i++) {
                List<SRLPredicate> pList = parseOneSentence(senList.get(i), sentMap.get(i)[0]);
                results.addAll(pList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * SRL解析多个句子
     * @param sentenceList 句子列表
     * @return 谓词列表的列表，一个列表对应一个句子
     */
    public static List<List<SRLPredicate>> parseSentences(List<String> sentenceList) {
        List<List<SRLPredicate>> results = new LinkedList<>();
        for (String s : sentenceList) {
            results.add(parseOneSentence(s));
        }
        return results;
    }
    
    /**
     * SRL解析单个句子
     * @param sentence 句子文本
     * @param senOffset 句子的全文偏移量
     * @return 谓词列表
     */
    public static List<SRLPredicate> parseOneSentence(String sentence, int senOffset) {
        List<String> words = AHANLP.getWordList(AHANLP.NLPSegment(sentence));
        words.add(0, "ROOT"); // add ROOT node
        Sentence s;
        List<SRLPredicate> results = new ArrayList<SRLPredicate>();
        try {
            s = pipeline.parse(words);
            MatchArg matchArg = new MatchArg(
                sentence, words.subList(1, words.size()), s.toString(), s.getPredicates().size()
            );
            for(int i = 1; i < s.size(); i++) {
                Word w = s.get(i);
                if (w instanceof Predicate) {
                    Map<Word, String> argWordMap = ((Predicate) w).getArgMap();
                    List<Arg> argList = new ArrayList<Arg>();
                    for(Word arg : argWordMap.keySet()) {
                        String argLabel = argWordMap.get(arg);
                        Yield argSpan = arg.getYield((Predicate) w, argLabel, argWordMap.keySet());
                        for (Yield span : argSpan.explode()) {
                            StringBuilder sb = new StringBuilder();
                            for (Word word : span) {
                                sb.append(word.getForm());
                            }
                            int offset = matchArg.getArgOffset(i-1, argLabel, sb.toString());
                            if (offset != -1) {
                                Arg a = new Arg(sb.toString(), offset, argLabel);
                                a.setSenOffset(senOffset);
                                argList.add(a);
                            }
                        }
                    }
                    try {
                        SRLPredicate predicate = new SRLPredicate(sentence, w.getForm(), matchArg.getWordOffset(i-1), argList);
                        predicate.setSenOffset(senOffset);
                        results.add(predicate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<SRLPredicate>();
        }
        return results;
    }
    
    /**
     * SRL解析单个句子
     * @param sentence 句子文本
     * @return 谓词列表
     */
    public static List<SRLPredicate> parseOneSentence(String sentence) {
        List<String> words = AHANLP.getWordList(AHANLP.NLPSegment(sentence));
        words.add(0, "ROOT"); // add ROOT node
        Sentence s;
        List<SRLPredicate> results = new ArrayList<SRLPredicate>();
        try {
            s = pipeline.parse(words);
            MatchArg matchArg = new MatchArg(
                sentence, words.subList(1, words.size()), s.toString(), s.getPredicates().size()
            );
            for(int i = 1; i < s.size(); i++) {
                Word w = s.get(i);
                if (w instanceof Predicate) {
                    Map<Word, String> argWordMap = ((Predicate) w).getArgMap();
                    List<Arg> argList = new ArrayList<Arg>();
                    for(Word arg : argWordMap.keySet()) {
                        String argLabel = argWordMap.get(arg);
                        Yield argSpan = arg.getYield((Predicate) w, argLabel, argWordMap.keySet());
                        for (Yield span : argSpan.explode()) {
                            StringBuilder sb = new StringBuilder();
                            for (Word word : span) {
                                sb.append(word.getForm());
                            }
                            int offset = matchArg.getArgOffset(i-1, argLabel, sb.toString());
                            if (offset != -1) {
                                argList.add(new Arg(sb.toString(), offset, argLabel));
                            }
                        }
                    }
                    try {
                        results.add(
                            new SRLPredicate(sentence, w.getForm(), matchArg.getWordOffset(i-1), argList)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<SRLPredicate>();
        }
        return results;
    }
}
