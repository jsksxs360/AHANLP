package me.xiaosheng.chnlp.lda;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class LDAModel {
    double[][] phi; //phi矩阵 (训练出的LDA模型)
    Vocabulary vocabulary; //词汇表
    
    /**
     * 读取模型文件
     * @param modelFilePath 模型文件路径
     * @throws IOException
     */
    public LDAModel(String modelFilePath) throws IOException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(modelFilePath)));
        int x = in.readInt(); //读取phi矩阵行数
        int y = in.readInt(); //读取phi矩阵列数
        phi = new double[x][y];
        for(int i = 0; i < x; i++) { //读取phi矩阵
            for(int j = 0; j < y; j++) {
                phi[i][j] = in.readDouble();
            }
        }
        int wordListLength = in.readInt(); //读取词汇个数
        String[] wordList = new String[wordListLength];
        for(int i = 0; i < wordListLength; i++) { //读取所有词汇
            wordList[i] = in.readUTF();
        }
        in.close();
        vocabulary = new Vocabulary(wordList); //生成词汇表
    }
    
    /**
     * 保存模型文件
     * @param saveModelFilePath 模型文件保存位置
     * @param phi LDA模型(phi矩阵)
     * @param vocabulary 词汇表
     * @throws IOException
     */
    public static void saveModelFile(String saveModelFilePath, double[][] phi, Vocabulary vocabulary) throws IOException {
        createFolders(saveModelFilePath);
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(saveModelFilePath)));
        out.writeInt(phi.length); //写入phi矩阵行数
        out.writeInt(phi[0].length); //写入phi矩阵列数
        for (int i = 0; i < phi.length; i++) { //写入phi矩阵
            for (int j = 0; j < phi[i].length; j++) {
                out.writeDouble(phi[i][j]);
            }
        }
        String[] id2WordArray = vocabulary.getId2wordArray();
        int wordListLength = vocabulary.size();
        out.writeInt(wordListLength); //写入词汇表长度
        for (int i = 0; i < wordListLength; i++) { //写入所有词汇
            out.writeUTF(id2WordArray[i]);
        }
        out.close();
    }
    
    /**
     * 保存LDA展示文件
     * @param saveFilePath 文件保存路径
     * @param phi LDA 模型(phi 矩阵)
     * @param vocabulary 词汇表
     * @param topicWordNum 每个主题展示的词汇数量
     * @throws IOException
     */
    public static void saveLDAShowFile(String saveFilePath, double[][] phi, Vocabulary vocabulary, int topicWordNum) throws IOException {
        createFolders(saveFilePath);
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, vocabulary, topicWordNum);
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(saveFilePath)));
        int i = 0;
        for (Map<String, Double> topic : topicMap) {
            out.println("topic" + (i++) + " : ");
            for (Map.Entry<String, Double> entry : topic.entrySet()) {
                out.println(entry);
            }
            out.println();
        }
        out.close();
    }
    
    /**
     * 获取phi矩阵
     * @return
     */
    public double[][] getPhiMatrix() {
        return phi;
    }
    
    /**
     * 获取词汇表
     * @return
     */
    public Vocabulary getVocabulary() {
        return vocabulary;
    }
    
    /**
     * 创建文件路径中的目录
     * @param filePath 文件路径
     */
    public static void createFolders(String filePath) {
        Path folderPath = Paths.get(filePath).getParent();
        if(folderPath != null) { //路径中存在目录
            File folder = folderPath.toFile();
            if(!folder.exists())
                folder.mkdirs();
        }
    }
}
