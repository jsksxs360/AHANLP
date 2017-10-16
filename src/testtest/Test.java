package testtest;

import java.util.List;

import com.hankcs.hanlp.seg.common.Term;

import me.xiaosheng.chnlp.AHANLP;

public class Test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String s = "王五今天吃了3碗面，觉得味道不错。12";
        List<Term> stdSegResult = AHANLP.StandardSegment(s);
        System.out.println("标准分词:\n" + stdSegResult);
    }

}
