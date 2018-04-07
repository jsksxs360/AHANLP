# AHANLP
啊哈自然语言处理包，集成了 [**HanLP**](https://github.com/hankcs/HanLP)、[**Word2Vec**](https://github.com/jsksxs360/Word2Vec) 等项目，提供高质量的中文自然语言处理服务。

**AHANLP** 目前提供的功能有：

- 分词类
  - 标准分词
  - NLP分词
  - 分句
  - 多句分词
  - 命名实体识别
- 句法分析类
  - 依存句法分析
- 摘要类
  - TextRank 摘取关键词
  - TextRank 摘取关键句和摘要
- 语义类
  - Word2Vec 词语相似度
  - Word2Vec 句子相似度
  - LDA 主题预测
- 附加功能
  - 简繁转换
  - WordCloud 绘制词云

## 下载与配置

### 1. 依赖 jar 包

- **[HanLP](https://github.com/hankcs/HanLP)：** 汉语言处理包
- **[Word2Vec](https://github.com/jsksxs360/Word2Vec)：** 谷歌 word2vec 的 java 实现版本

在 `lib` 目录下默认已包含 **hanlp-1.6.1.jar** 和 **Word2Vec-1.2.2.jar** 。

### 2. 数据包

AHANLP 沿用 HanLP 的数据组织结构，字典与模型分开存储，采用模块化管理。用户可以根据自己的需要选择相应的数据包下载：

- 如果只需要使用到分词类的功能，那么只需要下载字典数据包 [AHANLP_dictionary-1.1](https://pan.baidu.com/s/13Csy4ZsNTbJl7wDDEi9QsA)。将解压出的 **dictionary** 目录存放到 `data/` 目录下。
- 如果需要使用到句法分析类的功能，请额外下载 [AHANLP_parser_model-1.1](https://pan.baidu.com/s/1PKIYhHX7SLgnEUhN7czi8g)。将解压出的 **dependency** 目录存放到 `data/model/` 目录下。
- 如果需要使用到摘要类或者 Word2Vec 的相关功能，请额外下载 [word2vec 模型](w2v.markdown)。将解压出的模型文件存放到 `data/model/` 目录下。
- 如果需要使用到 LDA 主题预测功能，请额外下载 [AHANLP_LDA_model](https://pan.baidu.com/s/1nvNpZIh)，将解压出的 **SogouCS_LDA.model** 文件存放到 `data/model/` 目录下。如果你需要运行 LDADemo.java 进行测试，还需要下载 [SogouCA_mini](https://pan.baidu.com/s/1nvujNEL)，将解压出的 **mini** 文件夹存放到 `data/` 目录下。

### 3. 配置文件

AHANLP 项目中的各项参数均读取自配置文件（不建议用户修改），下面仅作简单说明。

主配置文件为 `ahanlp.properties`，需要配置 Word2Vec 模型路径等，默认为

```
word2vecModel = data/model/wiki_chinese_word2vec(Google).model
hanLDAModel = data/model/SogouCS_LDA.model
pythonCMD = python
```

HanLP 配置文件为 `hanlp.properties`，只需要在第一行设置 data 目录所在路径，默认为

```
root=./
```

### 3. 附加

如果需要使用 WordCloud 绘制词云服务，需要配置 Python 环境（建议使用 Anaconda），并且安装以下 package：

- wordcloud: 点击[这里](http://www.lfd.uci.edu/~gohlke/pythonlibs/#wordcloud)下载 whl 文件，然后使用 `python -m pip install xxx.whl` 安装

## 调用方法

**AHANLP** 几乎所有的功能都可以通过工具类 `AHANLP` 快捷调用。并且推荐用户始终通过工具类 AHANLP 调用，这样将来 AHANLP 升级后，用户无需修改调用代码。

所有 Demo 都位于 [test.demo](https://github.com/jsksxs360/AHANLP/tree/master/src/test/demo) 下，比文档覆盖了更多细节，强烈建议运行一遍。

### 1. 分词

```java
String content = "中国科学院计算技术研究所的宗成庆教授正在教授自然语言处理课程。";
// 标准分词
List<Term> stdSegResult = AHANLP.StandardSegment(content);
System.out.println(stdSegResult);
//[中国科学院计算技术研究所/nt, 的/ude1, 宗成庆/nr, 教授/nnt, 正在/d, 教授/nnt, 自然语言处理/nz, 课程/n, 。/w]

// NLP分词
List<Term> nlpSegResult = AHANLP.NLPSegment(content);
System.out.println(nlpSegResult);
//[中国科学院计算技术研究所/nt, 的/ude1, 宗成庆/nr, 教授/nnt, 正在/d, 教授/v, 自然语言处理/nz, 课程/n, 。/w]
```

**StandardSegment** 标准分词是对 HanLP 中 StandardTokenizer 的封装，在绝大多数场合下都是最好的选择，推荐使用。**NLPSegment** NLP分词是对 HanLP 中 NLPTokenizer 的封装，会执行全部命名实体识别和词性标注。

分词默认的返回结果包含词语和词性，可以通过 `AHANLP.getWordList(stdSegResult)` 和 `AHANLP.getNatureList(stdSegResult)` 来直接获取词语或词性列表。词性标注请参见[《HanLP 词性标注集》](hanlp_pos.markdown)。

如果需要自定义词性过滤，可以使用 `me.xiaosheng.chnlp.seg.POSFilter` 类，它还实现了过滤标点、保留实词等常用方法。

```java
// 过滤标点
POSFilter.removePunc(stdSegResult);
// 保留名词
POSFilter.selectPOS(stdSegResult, Arrays.asList("n", "ns", "nr", "nt", "nz"));
```

上面的分词器都支持对停用词的过滤，只需再带上第二个参数，并且设为 true 就可以了

```java
List<Term> stdSegResult = AHANLP.StandardSegment(content, true);
List<String> stdWordList = AHANLP.getWordList(stdSegResult);
System.out.println(stdWordList);
//[中国科学院计算技术研究所, 宗成庆, 教授, 正在, 教授, 自然语言处理, 课程]
```

分词支持用户自定义字典，HanLP 中使用 `CustomDictionary` 类表示，是一份全局的用户自定义词典，影响全部分词器。词典文本路径是 `data/dictionary/custom/CustomDictionary.txt`，用户可以在此增加自己的词语（不推荐）。也可以单独新建一个文本文件，通过配置文件 `CustomDictionaryPath=data/dictionary/custom/CustomDictionary.txt; 我的词典.txt;` 来追加词典（推荐）。

词典格式：

- 每一行代表一个单词，格式遵从 `[单词] [词性A] [A的频次] [词性B] [B的频次] ...`  如果不填词性则表示采用词典的默认词性。
- 词典的默认词性默认是名词 n，可以通过配置文件修改： `全国地名大全.txt ns;` 如果词典路径后面空格紧接着词性，则该词典默认是该词性。

程序默认从缓存文件(filename.txt.bin 或 filename.txt.trie.dat 和 filename.txt.trie.value)中读取字典，如果你修改了任何词典，只有删除缓存才能生效。

### 2. 分句

```java
List<String> senList = AHANLP.splitSentence(content);
for (int i = 0; i < senList.size(); i++)
    System.out.println((i + 1) + " : " + senList.get(i));
```

默认以 `[，,。:：“”？?！!；;]` 为句间分隔符切分句子，也可以使用自定义的分隔符，以参数形式传入

```java
List<String> senList = AHANLP.splitSentence(content, "[,。？！；]");
```

### 3. 多句分词

```java
List<List<String>> senWordList = AHANLP.splitWordInSentences(senList, true);
for (int i = 0; i < senWordList.size(); i++)
    System.out.println((i + 1) + " : " + senWordList.get(i));
```

**AHANLP** 还支持同时对句子列表进行分词，分词器使用标准分词，返回每个句子中分出的词语列表（不包含词性）。

### 4. 命名实体识别

```java
String sentence = "2013年9月，习近平出席上合组织比什凯克峰会和二十国集团圣彼得堡峰会，"
                + "并对哈萨克斯坦等中亚4国进行国事访问。在“一带一路”建设中，这次重大外交行程注定要被历史铭记。";
List<NERTerm> NERResult = AHANLP.NER(sentence);
System.out.println(NERResult);
// [习近平/per, 上合组织/org, 比什凯克/loc, 二十国集团/org, 圣彼得堡/loc, 哈萨克斯坦/loc, 中亚/loc, 2013年9月/time]
```

命名实体识别负责识别出文本中的人名、地名、机构名，**AHANLP** 在这三类之外还添加了时间信息的识别。其中，人名、地名、机构名识别是在 HanLP 中 StandardTokenizer 的基础上进行的，而时间信息的识别则是在 HanLP 中 NLPTokenizer 的基础上进行的。

注意：人名使用 per 标注、地名使用 loc 标注、机构名使用 org 标注、时间使用 time 标注。

### 5. 依存句法分析

```java
String sentence = "北京是中国的首都";
CoNLLSentence deps = AHANLP.DependencyParse(sentence);
for (CoNLLWord dep : deps)
    System.out.printf("%s --(%s)--> %s\n", dep.LEMMA, dep.DEPREL, dep.HEAD.LEMMA);
/*
北京 --(主谓关系)--> 是
是 --(核心关系)--> ##核心##
中国 --(定中关系)--> 首都
的 --(右附加关系)--> 中国
首都 --(动宾关系)--> 是
*/

// 词语依存路径
List<List<Term>> wordPaths = AHANLP.getWordPathsInDST(sentence);
for (List<Term> wordPath : wordPaths) {
    System.out.println(wordPath.get(0).word + " : " + AHANLP.getWordList(wordPath));
}
/*
北京 : [北京, 是]
是 : [是]
中国 : [中国, 首都, 是]
的 : [的, 中国, 首都, 是]
首都 : [首都, 是]
*/

// 依存句法树前2层的词语
List<String> words = AHANLP.getTopWordsInDST(sentence, 1);
System.out.println(words);
// [北京, 是, 首都]
```

依存句法分析是对 HanLP 中 NeuralNetworkDependencyParser 的封装，使用的是[基于神经网络的高性能依存句法分析器](http://www.hankcs.com/nlp/parsing/neural-network-based-dependency-parser.html)。分析结果为 CoNLL 格式，可以按 CoNLLWord 类型进行迭代，如上所示，`CoNLLWord.LEMMA` 为从属词，`CoNLLWord.HEAD.LEMMA` 为支配词，`CoNLLWord.DEPREL` 为依存标签，默认为中文标签。

如果需要使用英文标签，只需再带上第二个参数，并且设为 true 就可以了

```
CoNLLSentence enDeps = AHANLP.DependencyParse(sentence, true);
```

关于依存标签的详细说明，可以参见[《依存标签》](dep_tag.markdown)。

### 6. TextRank 摘取关键词

```java
String document = "我国第二艘航空母舰下水仪式26日上午在中国船舶重工集团公司大连造船厂举行。航空母舰在拖曳牵引下缓缓移出船坞，停靠码头。目前，航空母舰主船体完成建造，动力、电力等主要系统设备安装到位。";
List<String> wordList = AHANLP.extractKeyword(document, 5);
System.out.println(wordList);
//[航空母舰, 动力, 建造, 牵引, 完成]
```

**extractKeyword** 函数通过第二个参数设定返回的关键词个数。内部通过 TextRank 算法计算每个词语的 Rank 值，并按 Rank 值降序排列，提取出前面的几个作为关键词。具体原理可以参见[《TextRank算法提取关键词和摘要》](http://xiaosheng.me/2017/04/08/article49/)。

### 7. TextRank 摘取关键句和自动摘要

```java
String document = "我国第二艘航空母舰下水仪式26日上午在中国船舶重工集团公司大连造船厂举行。"
                + "按照国际惯例，剪彩后进行“掷瓶礼”。随着一瓶香槟酒摔碎舰艏，两舷喷射绚丽彩带，周边船舶一起鸣响汽笛，全场响起热烈掌声。"
                + "航空母舰在拖曳牵引下缓缓移出船坞，停靠码头。第二艘航空母舰由我国自行研制，2013年11月开工，2015年3月开始坞内建造。" + "目前，航空母舰主船体完成建造，动力、电力等主要系统设备安装到位。"
                + "出坞下水是航空母舰建设的重大节点之一，标志着我国自主设计建造航空母舰取得重大阶段性成果。" + "下一步，该航空母舰将按计划进行系统设备调试和舾装施工，并全面开展系泊试验。";
List<String> senList = AHANLP.extractKeySentence(document, 5);
System.out.println("Key Sentences: ");
for (int i = 0; i < senList.size(); i++)
    System.out.println((i + 1) + " : " + senList.get(i));
System.out.println("Summary: ");
System.out.println(AHANLP.extractSummary(document, 50));
/*
Key Sentences: 
1 : 航空母舰主船体完成建造
2 : 航空母舰在拖曳牵引下缓缓移出船坞
3 : 该航空母舰将按计划进行系统设备调试和舾装施工
4 : 并全面开展系泊试验
5 : 出坞下水是航空母舰建设的重大节点之一
Summary: 
航空母舰在拖曳牵引下缓缓移出船坞。航空母舰主船体完成建造。出坞下水是航空母舰建设的重大节点之一。
*/
```

**extractKeySentence** 函数负责摘取关键句，第二个参数控制摘取的关键句数量。**extractSummary** 函数负责自动摘要，第二个参数设定摘要字数上限。内部通过 TextRank 算法计算每个句子的 Rank 值，并按 Rank 值降序排列，提取出前面的几个作为关键句。自动摘要类似，并且还考虑了句子在原文中的位置以及句子的长度。具体原理可以参见[《TextRank算法提取关键词和摘要》](http://xiaosheng.me/2017/04/08/article49/)。

句子之间的相似程度（即 TextRank 算法中的权值）使用 Word2Vec 提供的函数计算，默认使用了维基百科中文语料训练出的模型，也可以使用自定义模型。

### 8. 语义距离

```java
System.out.println("猫 | 狗 : " + AHANLP.wordSimilarity("猫", "狗"));
System.out.println("计算机 | 电脑 : " + AHANLP.wordSimilarity("计算机", "电脑"));
System.out.println("计算机 | 男人 : " + AHANLP.wordSimilarity("计算机", "男人"));

String s1 = "苏州有多条公路正在施工，造成局部地区汽车行驶非常缓慢。";
String s2 = "苏州最近有多条公路在施工，导致部分地区交通拥堵，汽车难以通行。";
String s3 = "苏州是一座美丽的城市，四季分明，雨量充沛。";
System.out.println("s1 | s1 : " + AHANLP.sentenceSimilarity(s1, s1));
System.out.println("s1 | s2 : " + AHANLP.sentenceSimilarity(s1, s2));
System.out.println("s1 | s3 : " + AHANLP.sentenceSimilarity(s1, s3));
/*
猫 | 狗 : 0.71021223
计算机 | 电脑 : 0.64130974
计算机 | 男人 : -0.013071457
s1 | s1 : 1.0
s1 | s2 : 0.6512632
s1 | s3 : 0.3648093
*/
```

**wordSimilarity** 和 **sentenceSimilarity** 分别是计算词语和句子相似度的函数，计算过程都使用了 Word2Vec 模型提供的词向量，使用前需要在 `ahanlp.properties` 文件中配置好 Word2Vec 模型路径。词语相似度直接通过计算词向量余弦值得到，句子相似度求取方式可以参见 [Word2Vec/issues1](https://github.com/jsksxs360/Word2Vec/issues/1)。如果想自己训练 Word2Vec 模型，可以参考[训练 Google 版模型](https://github.com/jsksxs360/Word2Vec#user-content-2-训练-google-版模型)。

注：**sentenceSimilarity** 使用标准分词对句子进行分词，并过滤停用词。

### 9. LDA 主题预测

```java
int topicNum510 = AHANLP.topicInference("data/mini/军事_510.txt");
System.out.println("军事_510.txt 最可能的主题号为: " + topicNum510);
int topicNum610 = AHANLP.topicInference("data/mini/军事_610.txt");
System.out.println("军事_610.txt 最可能的主题号为: " + topicNum610);
```

LDA 主题预测默认使用新闻 LDA 模型进行主题的预测，输出最大概率的主题编号。默认模型从搜狗新闻语料库(SogouCS)中每个类别抽取 1000 篇（可能不满）新闻中训练得到，共包含 100 个主题。

用户也可以通过 `ZHNLP.trainLDAModel()` 自己训练 LDA 模型，训练好后，使用重载的 `topicInference` 方法来加载

```java
AHANLP.trainLDAModel("data/mini/", 10, "data/model/testLDA.model");
int topicNum710 = AHANLP.topicInference("data/model/testLDA.model", "data/mini/军事_710.txt");
System.out.println("军事_710.txt 最可能的主题号为: " + topicNum710);
int topicNum810 = AHANLP.topicInference("data/model/testLDA.model", "data/mini/军事_810.txt");
System.out.println("军事_810.txt 最可能的主题号为: " + topicNum810);
```

### 10. 简繁转换

```java
String tc = AHANLP.convertSC2TC("用笔记本电脑写程序");
System.out.println(tc);
String sc = AHANLP.convertTC2SC("「以後等妳當上皇后，就能買士多啤梨慶祝了」");
System.out.println(sc);
/*
用筆記本電腦寫程序
“以后等你当上皇后，就能买草莓庆祝了”
*/
```

简繁转换是对 HanLP 中 `convertToTraditionalChinese` 和 `convertToSimplifiedChinese` 方法的包装。能够识别简繁分歧词，比如 `打印机=印表機`；以及许多简繁转换工具不能区分的字，例如“以后”、“皇后”中的两个“后”字。

### 11. WordCloud 绘制词云

```java
String document = "我国第二艘航空母舰下水仪式26日上午在中国船舶重工集团公司大连造船厂举行。" + "中共中央政治局委员、中央军委副主席范长龙出席仪式并致辞。9时许，仪式在雄壮的国歌声中开始。"
                + "按照国际惯例，剪彩后进行“掷瓶礼”。随着一瓶香槟酒摔碎舰艏，两舷喷射绚丽彩带，周边船舶一起鸣响汽笛，全场响起热烈掌声。"
                + "航空母舰在拖曳牵引下缓缓移出船坞，停靠码头。第二艘航空母舰由我国自行研制，2013年11月开工，2015年3月开始坞内建造。" + "目前，航空母舰主船体完成建造，动力、电力等主要系统设备安装到位。"
                + "出坞下水是航空母舰建设的重大节点之一，标志着我国自主设计建造航空母舰取得重大阶段性成果。" + "下一步，该航空母舰将按计划进行系统设备调试和舾装施工，并全面开展系泊试验。"
                + "海军、中船重工集团领导沈金龙、苗华、胡问鸣以及军地有关部门领导和科研人员、干部职工、参建官兵代表等参加仪式。";
List<String> wordList = Segment.getWordList(Segment.StandardSegment(document, true));
WordCloud wc = new WordCloud(wordList);
try {
    wc.createImage("D:\\test.png");
} catch (IOException e) {
    e.printStackTrace();
}
```

**WordCloud** 使用一个词语列表创建 WordCloud 对象，然后调用 `createImage` 方法创建词云图片，并且将图片的保存地址作为参数传入。词云按照词频来绘制每个词语的大小，词频越高，词语越大；颜色及位置随机生成。

![test](wordcloud.png)

默认生成图片尺寸为 500x400，背景色为白色。也可以自定义图片的背景色和图片尺寸

```java
WordCloud wc = new WordCloud(wordList);
wc.createImage("D:\\test_black.png", true); // 黑色背景
wc.createImage("D:\\test_1000x800.png", 1000, 800); // 尺寸 1000x800
wc.createImage("D:\\test_1000x800_black.png", 1000, 800, true); // 尺寸 1000x800, 黑色背景
```

### 鸣谢

再次向以下这些项目的作者致以敬意，正是这些开源软件推动了自然语言处理技术的普及和运用。

- [HanLP](https://github.com/hankcs/HanLP)
- [Ansj分词](https://github.com/NLPchina/ansj_seg)
- [Word2VEC_java](https://github.com/NLPchina/Word2VEC_java)
- [word_cloud](https://github.com/amueller/word_cloud)
- [SharpICTCLAS](http://www.cnblogs.com/zhenyulu/archive/2007/04/18/718383.html)
- [snownlp](https://github.com/isnowfy/snownlp)
- ​[nlp-lang](https://github.com/NLPchina/nlp-lang)


