# 依存标签

使用的是 Chinese Dependency Treebank 1.0，原始的标签是英文的，在 Parser 中，被按照下表进行了转换：

| Tag  |  关系   | Description           |
| :--: | :---: | --------------------- |
| SBV  | 主谓关系  | subject-verb          |
| VOB  | 动宾关系  | 直接宾语，verb-object      |
| IOB  | 间宾关系  | 间接宾语，indirect-object  |
| FOB  | 前置宾语  | 前置宾语，fronting-object  |
| DBL  |  兼语   | double                |
| ATT  | 定中关系  | attribute             |
| ADV  | 状中结构  | adverbial             |
| CMP  | 动补结构  | complement            |
| COO  | 并列关系  | coordinate            |
| POB  | 介宾关系  | preposition-object    |
| LAD  | 左附加关系 | left adjunct          |
| RAD  | 右附加关系 | right adjunct         |
|  IS  | 独立结构  | independent structure |
|  WP  | 标点符号  | punctuation           |
| HED  | 核心关系  | head                  |

