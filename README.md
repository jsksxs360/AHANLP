![AHANLP](https://socialify.git.ci/jsksxs360/AHANLP/image?description=1&descriptionEditable=Aha%20Chinese%20Natural%20Language%20Processing%20Package&font=Jost&language=1&logo=https%3A%2F%2Fraw.githubusercontent.com%2Fjsksxs360%2FAHANLP%2Fmaster%2Flogo.png&name=1&pattern=Circuit%20Board&theme=Light)

啊哈自然语言处理包，集成了 [**HanLP**](https://github.com/hankcs/HanLP/tree/1.x)、[**Word2Vec**](https://github.com/jsksxs360/Word2Vec)、[**Mate-Tools**](https://code.google.com/archive/p/mate-tools/) 等项目，提供高质量的中文自然语言处理服务。

**AHANLP** 目前提供的功能有：

- 分词类
  - [标准分词](#1-分词)
  - [NLP 分词](#1-分词)
  - [分词断句](#2-分词断句)
  - [命名实体识别](#3-命名实体识别)
- 句法分析类
  - [依存句法分析](#4-依存句法分析)
- 摘要类
  - [TextRank 摘取关键词](#5-textrank-摘取关键词)
  - [TextRank 摘取关键句和摘要](#6-textrank-摘取关键句和自动摘要)
- 语义类
  - [Word2Vec 词语相似度](#7-语义距离)
  - [Word2Vec 句子相似度](#7-语义距离)
  - [语义角色标注](#8-语义角色标注)
  - [LDA 主题预测](#9-lda-主题预测)
- 附加功能
  - [简繁转换](#10-简繁转换)
  - [WordCloud 绘制词云](#11-wordcloud-绘制词云)

## 下载与配置

最新版本 **ahanlp.jar** 和对应的基础数据包 **AHANLP_base**，点击[这里](https://github.com/jsksxs360/AHANLP/releases)下载。配置文件 **ahanlp.properties** 和 **hanlp.properties** 放入 classpath 即可，对于多数项目，只需放到 src 目录下，编译时 IDE 会自动将其复制到 classpath 中。

AHANLP 沿用 HanLP 的数据组织结构，代码和数据分离，用户可以根据自己的需要选择相应的数据包下载：

- 基础数据包 [AHANLP_base](https://github.com/jsksxs360/AHANLP/releases) 包含分词、句法分析等基础分析需要的模型和字典文件，下载解压后，将 `dictionary` 目录和 `model` 目录存放到项目的 `data/` 目录下。
- 如果需要使用到**句子摘要**和 **Word2Vec** 的相关功能，请额外下载 [word2vec 模型](github/w2v.md)，将解压出的模型文件存放到项目的 `data/model/` 目录下。
- 如果需要使用 **WordCloud 绘制词云**服务，需要 Python 环境，并且安装 [wordcloud](http://www.lfd.uci.edu/~gohlke/pythonlibs/#wordcloud) 包（下载后使用 `python -m pip install xxx.whl` 安装）。然后下载 [word_cloud](https://pan.baidu.com/s/1zhwZH5D5aO7gGHag1G76wQ) (提取码 9jb6)，将解压出的 `word_cloud` 文件夹放到项目根目录下。

AHANLP 项目中的各项参数均读取自配置文件（不建议用户修改），下面仅作简单说明。

主配置文件为 `ahanlp.properties`，需要配置 Word2Vec 模型路径等，默认为

```
word2vecModel = data/model/wiki_chinese_word2vec(Google).model
hanLDAModel = data/model/SogouCS_LDA.model
srlTaggerModel = data/model/srl/CoNLL2009-ST-Chinese-ALL.anna-3.3.postagger.model
srlParserModel = data/model/srl/CoNLL2009-ST-Chinese-ALL.anna-3.3.parser.model
srlModel = data/model/srl/CoNLL2009-ST-Chinese-ALL.anna-3.3.srl-4.1.srl.model
wordCloudPath = word_cloud/ 
pythonCMD = python
```

HanLP 配置文件为 `hanlp.properties`，只需要在第一行设置 data 目录所在路径，默认为

```
root=./
```

注：语义角色标注模块的内存占用较高，如果要使用该功能，请将 JVM 的最大内存占用设置为 4GB。

## 调用方法及文档

**AHANLP** 几乎所有的功能都可以通过工具类 `AHANLP` 快捷调用。并且推荐用户始终通过工具类 AHANLP 调用，这样将来 AHANLP 升级后，用户无需修改调用代码。

所有 Demo 位于 [test.demo](https://github.com/jsksxs360/AHANLP/tree/master/src/test/demo) 下，比下方简介文档覆盖了更多细节，强烈建议运行一遍，也可以查阅:

[**AHANLP 接口文档**](https://github.com/jsksxs360/AHANLP/wiki)。

### 1. 分词

```java
String content = "苏州大学的周国栋教授正在教授自然语言处理课程。";
// 标准分词
List<Term> stdSegResult = AHANLP.StandardSegment(content);
System.out.println(stdSegResult);
//[苏州大学/ntu, 的/ude1, 周/qt, 国栋/nz, 教授/nnt, 正在/d, 教授/nnt, 自然语言处理/nz, 课程/n, 。/w]

// NLP分词
List<Term> nlpSegResult = AHANLP.NLPSegment(content);
System.out.println(nlpSegResult);
//[苏州大学/ntu, 的/u, 周国栋/nr, 教授/n, 正在/d, 教授/v, 自然语言处理/nz, 课程/n, 。/w]
```

**标准分词 (StandardSegment)** 封装了 HMM-Bigram 模型，使用最短路方法分词（最短路求解采用 Viterbi 算法），兼顾了效率和效果。**NLP分词 (NLPSegment)** 封装了感知机模型，由[结构化感知机序列标注框架](https://github.com/hankcs/HanLP/wiki/结构化感知机标注框架)支撑，会同时执行词性标注和命名实体识别，准确率更高，适合生产环境使用。

分词默认的返回结果包含词语和词性，可以分别通过 `AHANLP.getWordList(stdSegResult)` 和 `AHANLP.getNatureList(stdSegResult)` 来获取词语或词性列表。词性标注请参见[《HanLP 词性标注集》](github/hanlp_pos.md)。

如果需要自定义词性过滤，可以使用 `me.xiaosheng.chnlp.seg.POSFilter` 类，它还实现了过滤标点、保留实词等常用方法。

```java
// 过滤标点
POSFilter.removePunc(stdSegResult);
// 保留名词
POSFilter.selectPOS(stdSegResult, Arrays.asList("n", "ns", "nr", "nt", "nz"));
```

上面的分词器都支持对停用词的过滤，只需再带上第二个参数，并且设为 true 就可以了

```java
List<Term> stdSegResult = AHANLP.NLPSegment(content, true);
List<String> stdWordList = AHANLP.getWordList(stdSegResult);
System.out.println(stdWordList);
//[苏州大学, 周国栋, 教授, 正在, 教授, 自然语言处理, 课程]
```

分词支持用户自定义字典，HanLP 中使用 `CustomDictionary` 类表示，是一份全局的用户自定义词典，影响全部分词器。词典文本路径是 `data/dictionary/custom/CustomDictionary.txt`，用户可以在此增加自己的词语（不推荐）。也可以单独新建一个文本文件，通过配置文件 `CustomDictionaryPath=data/dictionary/custom/CustomDictionary.txt; 我的词典.txt;` 来追加词典（推荐）。

词典格式：

- 每一行代表一个单词，格式遵从 `[单词] [词性A] [A的频次] [词性B] [B的频次] ...`  如果不填词性则表示采用词典的默认词性。
- 词典的默认词性默认是名词 n，可以通过配置文件修改： `全国地名大全.txt ns;` 如果词典路径后面空格紧接着词性，则该词典默认是该词性。

**注意：**程序默认从缓存文件(filename.txt.bin 或 filename.txt.trie.dat 和 filename.txt.trie.value)中读取字典，如果你修改了任何词典，只有删除缓存才能生效。

### 2. 分词断句

```java
List<List<Term>> segResults = AHANLP.seg2sentence("Standard", content, true);
for (int i = 0; i < senList.size(); i++)
    System.out.println((i + 1) + " : " + AHANLP.getWordList(results.get(i)));
```

对长文本按句子进行分词也是常见用法，返回是由所有句子分词结果组成的列表。通过第一个 segType 参数控制分词器的类型：“Standard”对应标准分词，“NLP”对应NLP分词。

当然也可以先手工断句，再进行多句分词。

```java
List<String> senList = AHANLP.splitSentence(content);
List<List<Term>> senWordList = AHANLP.splitWordInSentences("Standard", senList, true);
for (int i = 0; i < senWordList.size(); i++)
    System.out.println((i + 1) + " : " + senWordList.get(i));
```

### 3. 命名实体识别

```java
String sentence = "2013年9月，习近平出席上合组织比什凯克峰会和二十国集团圣彼得堡峰会，"
                + "并对哈萨克斯坦等中亚4国进行国事访问。在“一带一路”建设中，这次重大外交行程注定要被历史铭记。";
List<NERTerm> NERResult = AHANLP.NER(sentence);
System.out.println(NERResult);
//[中亚/loc, 习近平/per, 上合组织/org, 二十国集团/org, 哈萨克斯坦/loc, 比什凯克/loc, 圣彼得堡/loc, 2013年9月/time]
```

封装了感知机和CRF模型，由[结构化感知机序列标注框架](https://github.com/hankcs/HanLP/wiki/结构化感知机标注框架)支撑，识别出文本中的时间、地名、人名、机构名：人名 `per`、地名 `loc`、机构名 `org`、时间 `time`。

### 4. 依存句法分析

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

关于依存标签的详细说明，可以参见[《依存标签》](github/dep_tag.md)。

### 5. TextRank 摘取关键词

```java
String document = "我国第二艘航空母舰下水仪式26日上午在中国船舶重工集团公司大连造船厂举行。航空母舰在拖曳牵引下缓缓移出船坞，停靠码头。目前，航空母舰主船体完成建造，动力、电力等主要系统设备安装到位。";
List<String> wordList = AHANLP.extractKeyword(document, 5);
System.out.println(wordList);
//[航空母舰, 动力, 建造, 牵引, 完成]
```

**extractKeyword** 函数通过第二个参数设定返回的关键词个数。内部通过 TextRank 算法计算每个词语的 Rank 值，并按 Rank 值降序排列，提取出前面的几个作为关键词。具体原理可以参见[《TextRank算法提取关键词和摘要》](http://xiaosheng.online/2017/04/08/article49/)。

### 6. TextRank 摘取关键句和自动摘要

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
2 : 该航空母舰将按计划进行系统设备调试和舾装施工
3 : 我国第二艘航空母舰下水仪式26日上午在中国船舶重工集团公司大连造船厂举行
4 : 标志着我国自主设计建造航空母舰取得重大阶段性成果
5 : 出坞下水是航空母舰建设的重大节点之一
Summary: 
我国第二艘航空母舰下水仪式26日上午在中国船舶重工集团公司大连造船厂举行。航空母舰主船体完成建造。
*/
```

**extractKeySentence** 函数负责摘取关键句，第二个参数控制摘取的关键句数量。**extractSummary** 函数负责自动摘要，第二个参数设定摘要字数上限。内部通过 TextRank 算法计算每个句子的 Rank 值，并按 Rank 值降序排列，提取出前面的几个作为关键句。自动摘要类似，并且还考虑了句子在原文中的位置以及句子的长度。具体原理可以参见[《TextRank算法提取关键词和摘要》](http://xiaosheng.online/2017/04/08/article49/)。

句子之间的相似程度（即 TextRank 算法中的权值）使用 Word2Vec 提供的函数计算，默认使用了维基百科中文语料训练出的模型，也可以使用自定义模型。

### 7. 语义距离

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

**wordSimilarity** 和 **sentenceSimilarity** 分别是计算词语和句子相似度的函数，计算过程都使用到了 Word2Vec 模型预训练好的词向量，使用前需要下载 [word2vec 模型](github/w2v.md)，然后将解压出的模型文件存放到项目的 `data/model/` 目录下。词语相似度直接通过计算词向量余弦值得到，句子相似度求取方式可以参见 [Word2Vec/issues1](https://github.com/jsksxs360/Word2Vec/issues/1)。如果想自己训练 Word2Vec 模型，可以参考[训练 Google 版模型](https://github.com/jsksxs360/Word2Vec#user-content-2-训练-google-版模型)。

注：**sentenceSimilarity** 默认使用标准分词对句子进行分词，并过滤停用词。

### 8. 语义角色标注

```java
String sentence = "全球最大石油生产商沙特阿美（Saudi Aramco）周三（7月21日）证实，公司的一些文件遭泄露。";
List<SRLPredicate> predicateList = AHANLP.SRL(sentence);
for (SRLPredicate p : predicateList) {
    System.out.print("谓词: " + p.getPredicate());
    System.out.print("\t\t句内偏移量: " + p.getLocalOffset());
    System.out.print("\t句内索引： [" + p.getLocalIdxs()[0] + ", " + p.getLocalIdxs()[1] + "]\n");
    for (Arg arg : p.getArguments()) {
        System.out.print("\t" + arg.getLabel() + ": " + arg.getSpan());
        System.out.print("\t\t句内偏移量: " + arg.getLocalOffset());
        System.out.print("\t句内索引: [" + arg.getLocalIdxs()[0] + ", " + arg.getLocalIdxs()[1] + "]\n");
    }
    System.out.println();
}
/*
谓词: 证实		                               句内偏移量: 36     句内索引： [36, 37]
TMP: 周三（7月21日）                            句内偏移量: 27     句内索引: [27, 35]
A0: 全球最大石油生产商沙特阿美（Saudi Aramco）     句内偏移量: 0     句内索引: [0, 26]
A1: 公司的一些文件遭泄露                         句内偏移量: 39     句内索引: [39, 48]

谓词: 遭                                       句内偏移量: 46     句内索引： [46, 46]
A0: 公司的一些文件                              句内偏移量: 39     句内索引: [39, 45]
A1: 泄露                                      句内偏移量: 47     句内索引: [47, 48]
*/
```

封装了 [Mate-Tools](https://code.google.com/archive/p/mate-tools/) 中的 [SRL 组件](https://aclanthology.org/W09-1206/)（包括[词性识别器](https://aclanthology.org/D12-1133/)和[依存句法分析器](https://aclanthology.org/E12-1009/)），能够识别出文本中的谓词 `predicate` 和对应的论元：施事者 `A0` 、受事者 `A1`、时间 `TMP` 和地点 `LOC`。添加了对长文本的支持 **SRLParseContent(content)** 以及文本偏移量的匹配，可以通过谓词和论元在文本中的位置和偏移量进行定位。

注：每一个谓词对应的某一类别的论元都可能不止一个，例如谓词”攻击“可以对应多个施事者A0（攻击者）。

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

如果你需要运行 LDADemo.java 进行测试，还需要下载 [SogouCA_mini](https://github.com/jsksxs360/AHANLP/raw/master/SogouCA_mini.zip)，将解压出的 `mini` 文件夹存放到项目的 `data/` 目录下。

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
List<String> wordList = AHANLP.getWordList(AHANLP.StandardSegment(document, true));
WordCloud wc = new WordCloud(wordList);
try {
    wc.createImage("D:\\test.png");
} catch (IOException e) {
    e.printStackTrace();
}
```

**WordCloud** 使用一个词语列表创建 WordCloud 对象，然后调用 `createImage` 方法创建词云图片，并且将图片的保存地址作为参数传入。词云按照词频来绘制每个词语的大小，词频越高，词语越大；颜色及位置随机生成。

![test](github/wordcloud.png)

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
- [nlp-lang](https://github.com/NLPchina/nlp-lang)

