package me.xiaosheng.chnlp.addition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import me.xiaosheng.chnlp.Config;

public class WordCloud {

    private List<String> wordList = null;

    public WordCloud(List<String> wordList) {
        this.wordList = wordList;
    }
    
    /**
     * 绘制词云图片
     * @param savePicPath 图片存储路径
     * @throws IOException
     */
    public void createImage(String savePicPath) throws IOException {
        createImage(savePicPath, 500, 400, false);
    }
    
    /**
     * 绘制词云图片
     * @param savePicPath 图片存储路径
     * @param blackBackground 是否使用黑色背景
     * @throws IOException
     */
    public void createImage(String savePicPath, boolean blackBackground) throws IOException {
        createImage(savePicPath, 500, 400, true);
    }
    
    /**
     * 绘制词云图片
     * @param savePicPath 图片存储路径
     * @param picWidth 图片宽度
     * @param picHeight 图片高度
     * @throws IOException
     */
    public void createImage(String savePicPath, int picWidth, int picHeight) throws IOException {
        createImage(savePicPath, picWidth, picHeight, false);
    }
    
    /**
     * 绘制词云图片
     * @param savePicPath 图片存储路径
     * @param picWidth 图片宽度
     * @param picHeight 图片高度
     * @param blackBackground 是否使用黑色背景
     * @throws IOException
     */
    public void createImage(String savePicPath, int picWidth, int picHeight, boolean blackBackground) throws IOException {
        String tempFileName = UUID.randomUUID().toString();
        // 生成临时词语文件
        BufferedWriter bw = new BufferedWriter(new FileWriter("word_cloud/" + tempFileName));
        for (String word : wordList)
            bw.write(word + "\n");
        bw.flush();
        bw.close();
        StringBuilder cmd = new StringBuilder(Config.pythonCMD());
        cmd.append(" create_word_cloud.py");
        cmd.append(" -l ");
        cmd.append(tempFileName);
        cmd.append(" -w ");
        cmd.append(picWidth);
        cmd.append(" -h ");
        cmd.append(picHeight);
        cmd.append(" -b ");
        cmd.append(blackBackground ? "black" : "white");
        cmd.append(" -s ");
        cmd.append("\"" + savePicPath + "\"");
        cmd.append(" -f ");
        cmd.append("\"simhei.ttf\"");
        System.out.println(cmd.toString());
        Process pr = Runtime.getRuntime().exec(cmd.toString(), null, new File("word_cloud/"));
        try {
            pr.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pr.destroy();
        }
        new File("word_cloud/" + tempFileName).delete();
        System.out.println("create wordcloud success!");
    }
}
