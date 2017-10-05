# -*- coding: utf-8 -*-
from PIL import Image
import os

os.chdir('./9')
lst = os.listdir()

fp = open('../train.txt','a')

for f in lst:
	if f == ".DS_Store":
		continue
	ii = 1
	im = Image.open(f)
	w,h = im.size
	s = "9"
	for i in range(0,h):
		for j in range(0,w):
			d = im.getpixel((j,i))
			if d > 100:
				s += " %d:1" % ii
			else:
				s += " %d:0" % ii
			ii+=1
	fp.write(s+"\n")
fp.close()