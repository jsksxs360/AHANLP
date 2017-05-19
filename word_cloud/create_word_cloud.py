# coding: utf-8

import sys
import getopt
import matplotlib.pyplot as plt
from wordcloud import WordCloud
import codecs
from numpy import integer

class WordCloud_CN:

    def __init__(self, word_list_file, pic_width, pic_height, pic_back, pic_save_path, font_path):
        self.word_list_file = word_list_file
        self.pic_width = int(pic_width)
        self.pic_height = int(pic_height)
        self.pic_back = pic_back
        self.pic_save_path = pic_save_path
        self.font_path = font_path

    def create_image(self):
        word_lines = open(self.word_list_file,encoding="utf8").readlines()
        word_list = [word.strip() for word in word_lines]
        word_list_text = ' '.join(word_list)
        wordcloud = WordCloud(font_path=self.font_path, background_color=self.pic_back, width=self.pic_width, height=self.pic_height)
        wordcloud = wordcloud.generate(word_list_text)
        image = wordcloud.to_image()
        image.save(self.pic_save_path)

def create_word_cloud(word_list_file, pic_width, pic_height, pic_back, pic_save_path, font_path):
    word_cloud = WordCloud_CN(word_list_file, pic_width, pic_height, pic_back, pic_save_path, font_path)
    word_cloud.create_image()

opts, args = getopt.getopt(sys.argv[1:], 'l:w:h:b:s:f:')
word_list_file = ''
pic_width = ''
pic_height = ''
pic_back = ''
pic_save_path = ''
font_path = ''
for op, value in opts:
    if op == '-l':
        word_list_file = value
    elif op == '-w':
        pic_width = value
    elif op == '-h':
        pic_height = value
    elif op == '-b':
        pic_back = value
    elif op == '-s':
        pic_save_path = value
    elif op == '-f':
        font_path = value
create_word_cloud(word_list_file, pic_width, pic_height, pic_back, pic_save_path, font_path)