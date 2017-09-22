package test.demo;

import me.xiaosheng.chnlp.AHANLP;

public class ConvertDemo {

    public static void main(String[] args) {
        //简繁转换
        String tc = AHANLP.convertSC2TC("用笔记本电脑写程序");
        System.out.println(tc);
        String sc = AHANLP.convertTC2SC("「以後等妳當上皇后，就能買士多啤梨慶祝了」");
        System.out.println(sc);
    }
}
