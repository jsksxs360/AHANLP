package test.demo;

import java.util.List;

import me.xiaosheng.chnlp.AHANLP;
import me.xiaosheng.chnlp.seg.NER;
import me.xiaosheng.chnlp.seg.NERTerm;

public class NERDemo {

    public static void main(String[] args) {
        String sentence = "2013年9月，习近平出席上合组织比什凯克峰会和二十国集团圣彼得堡峰会，"
                + "并对哈萨克斯坦等中亚4国进行国事访问。在“一带一路”建设中，这次重大外交行程注定要被历史铭记。";
        // 命名实体识别
        List<NERTerm> NERResult = AHANLP.NER(sentence);
        System.out.println(NERResult);
        // 时间信息
        System.out.println("time:" + NER.getTimeInfo(NERResult));
        // 地名信息
        System.out.println("location:" + NER.getLocInfo(NERResult));
        // 人名信息
        System.out.println("person:" + NER.getPerInfo(NERResult));
        // 机构名信息
        System.out.println("organization:" + NER.getOrgInfo(NERResult));
    }

}
