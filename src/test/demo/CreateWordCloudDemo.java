package test.demo;

import java.io.IOException;
import java.util.List;

import me.xiaosheng.chnlp.addition.WordCloud;
import me.xiaosheng.chnlp.seg.Segment;

public class CreateWordCloudDemo {

    public static void main(String[] args) {
        String document = "我国第二艘航空母舰下水仪式26日上午在中国船舶重工集团公司大连造船厂举行。" + "中共中央政治局委员、中央军委副主席范长龙出席仪式并致辞。9时许，仪式在雄壮的国歌声中开始。"
                + "按照国际惯例，剪彩后进行“掷瓶礼”。随着一瓶香槟酒摔碎舰艏，两舷喷射绚丽彩带，周边船舶一起鸣响汽笛，全场响起热烈掌声。"
                + "航空母舰在拖曳牵引下缓缓移出船坞，停靠码头。第二艘航空母舰由我国自行研制，2013年11月开工，2015年3月开始坞内建造。" + "目前，航空母舰主船体完成建造，动力、电力等主要系统设备安装到位。"
                + "出坞下水是航空母舰建设的重大节点之一，标志着我国自主设计建造航空母舰取得重大阶段性成果。" + "下一步，该航空母舰将按计划进行系统设备调试和舾装施工，并全面开展系泊试验。"
                + "海军、中船重工集团领导沈金龙、苗华、胡问鸣以及军地有关部门领导和科研人员、干部职工、参建官兵代表等参加仪式。";
        List<String> wordList = Segment.getWordList(Segment.StandardSegment(document, true));
        WordCloud wc = new WordCloud(wordList);
        try {
            wc.createImage("D:\\test.png");
            wc.createImage("D:\\test_black.png", true);
            wc.createImage("D:\\test_1000x800.png", 1000, 800);
            wc.createImage("D:\\test_1000x800_black.png", 1000, 800, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
