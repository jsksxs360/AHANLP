package me.xiaosheng.chnlp;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static String configFilePath = "zhnlp.properties";
    private static Properties props = new Properties();
    static {
        try {
            props.load(new BufferedInputStream(new FileInputStream(configFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * word2vec模型路径
     * 
     * @return
     */
    public static String word2vecModelPath() {
        return props.getProperty("word2vecModel");
    }
    
    /**
     * HanLDA模型路径
     * 
     * @return
     */
    public static String hanLDAModelPath() {
        return props.getProperty("hanLDAModel");
    }
    
    /**
     * python 调用命令
     * 
     * @return
     */
    public static String pythonCMD() {
        return props.getProperty("pythonCMD");
    }
}
