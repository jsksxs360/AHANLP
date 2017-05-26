package me.xiaosheng.chnlp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Config {

    private static String configFilePath = "ahanlp.properties";
    private static Properties props = new Properties();
    static {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) loader = Config.class.getClassLoader();
            props.load(new InputStreamReader(loader.getResourceAsStream(configFilePath), "UTF-8"));
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
