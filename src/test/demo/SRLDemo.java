package test.demo;

import java.util.Arrays;
import java.util.List;

import me.xiaosheng.chnlp.AHANLP;
import me.xiaosheng.chnlp.srl.Arg;
import me.xiaosheng.chnlp.srl.SRLParser;
import me.xiaosheng.chnlp.srl.SRLPredicate;

public class SRLDemo {

	public static void main(String[] args) {
		String sentence = "全球最大石油生产商沙特阿美（Saudi Aramco）周三（7月21日）证实，公司的一些文件遭泄露。";
		// 语义角色标注
		List<SRLPredicate> predicateList = AHANLP.SRL(sentence);
		for (SRLPredicate p : predicateList)
			System.out.println(p);
		// 循环打印每一个谓词和对应的论元
		System.out.println("解析句子： " + sentence);
		for (SRLPredicate p : predicateList) {
			System.out.print("谓词: " + p.getPredicate());
			System.out.print("\t\t句内偏移量: " + p.getLocalOffset());
			System.out.print("\t句内索引： [" + p.getLocalIdxs()[0] + ", " + p.getLocalIdxs()[1] + "]\n");
			for (Arg arg : p.getArguments()) {
				System.out.print("\t" + arg.getLabel() + ": " + arg.getSpan());
				System.out.print("\t\t句内偏移量: " + arg.getLocalOffset());
				System.out.print("\t句内索引: [" + arg.getLocalIdxs()[0] + ", " + arg.getLocalIdxs()[1] + "]\n");
			}
			System.out.println();
		}
		// 解析包含多个句子的长文
		String content = "全球最大石油生产商沙特阿美（Saudi Aramco）周三（7月21日）证实，公司的一些文件遭泄露。" + 
						 "此前，一名网络勒索者声称获取了该公司大量数据，并要求其支付5000万美元赎金。";
		List<SRLPredicate> pList = AHANLP.SRLParseContent(content);
		System.out.println("\n解析长文： " + content);
		for (SRLPredicate p : pList) {
			System.out.print("谓词: " + p.getPredicate());
			System.out.print("\t\t全文偏移量: " + p.getGlobalOffset());
			System.out.print("\t全文索引： [" + p.getGlobalIdxs()[0] + ", " + p.getGlobalIdxs()[1] + "]\n");
			for (Arg arg : p.getArguments()) {
				System.out.print("\t" + arg.getLabel() + ": " + arg.getSpan());
				System.out.print("\t\t全文偏移量: " + arg.getGlobalOffset());
				System.out.print("\t全文索引: [" + arg.getGlobalIdxs()[0] + ", " + arg.getGlobalIdxs()[1] + "]\n");
			}
			System.out.println();
		}
		// 批量解析句子
		List<String> senList = Arrays.asList(
			"全球最大石油生产商沙特阿美（Saudi Aramco）周三（7月21日）证实，公司的一些文件遭泄露。",
			"此前，一名网络勒索者声称获取了该公司大量数据，并要求其支付5000万美元赎金。"
		);
		List<List<SRLPredicate>> results = SRLParser.parseSentences(senList);
		for (int i = 0; i < senList.size(); i++) {
			System.out.println("\n解析句子: " + senList.get(i));
			for (SRLPredicate p : results.get(i)) {
				System.out.println("谓词:" + p.getPredicate());
			}
		}
	}

}
