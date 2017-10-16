package testtest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.hankcs.hanlp.seg.common.Term;

import me.xiaosheng.chnlp.AHANLP;
import me.xiaosheng.chnlp.seg.Segment;

public class CDTB_E {

    public static void main(String[] args) throws IOException {
//        File txtDir = new File("F:\\Ubuntu\\CDTB_DL_E\\txt");
//        for (String fileName : txtDir.list()) {
//            BufferedReader br = new BufferedReader(new FileReader("F:\\Ubuntu\\CDTB_DL_E\\txt\\" + fileName));
//            BufferedWriter bw = new BufferedWriter(new FileWriter("F:\\Ubuntu\\CDTB_DL_E\\seg\\" + fileName));
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                String[] items = line.trim().split("\t");
//                String pid = items[0];
//                String paragraph = items[1];
//                List<Term> stdSegResult = AHANLP.StandardSegment(paragraph, true);
//                List<String> stdWordList = Segment.getWordList(stdSegResult);
//                List<String> stdPOSList = Segment.getNatureList(stdSegResult);
//                if (stdWordList.isEmpty()) {
//                    System.out.println(fileName + "\t" + pid);
//                    continue;
//                }
//                bw.write(pid + "\t" + listToString(stdWordList) + "\t" + listToString(stdPOSList) + "\n");
//            }
//            bw.close();
//            br.close();
//        }
        createRelationSegFile();
    }

    public static void createRelationSegFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Xusheng\\Desktop\\CDTB_DL_E\\data\\relation.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\Xusheng\\Desktop\\CDTB_DL_E\\data\\relation_seg.txt"));
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] items = line.trim().split("\t");
            String id = items[0];
            String pid = items[1];
            String text1 = items[2];
            String text2 = items[3];
            String nul = items[4];
            List<Term> stdSegResult1 = AHANLP.StandardSegment(text1, true);
            List<Term> stdSegResult2 = AHANLP.StandardSegment(text2, true);
            if (stdSegResult1.isEmpty() || stdSegResult2.isEmpty()) {
                System.out.println(id + "\t" + pid);
                continue;
            }
            List<String> stdWordList1 = Segment.getWordList(stdSegResult1);
            List<String> stdPOSList1 = Segment.getNatureList(stdSegResult1);
            List<String> stdWordList2 = Segment.getWordList(stdSegResult2);
            List<String> stdPOSList2 = Segment.getNatureList(stdSegResult2);
            bw.write(id + "\t" + pid + "\t" + listToString(stdWordList1) + "\t" + listToString(stdWordList2)
                        + "\t" + nul + "\t" + listToString(stdPOSList1) + "\t" + listToString(stdPOSList2) + "\n");
        }
        br.close();
        bw.close();
    }
    
    public static String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String n : list)
            sb.append(n + " ");
        return sb.toString().trim();
    }
}
