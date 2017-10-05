# -*- coding: utf-8 -*-

f = open('data.txt','r')
ft = open('train.txt','w')
ftt = open('test.txt','w')


a = []

for i in range(0,10):
	a.append([])

lines = f.readlines()
for line in lines:
	a[int(line.strip().split(' ')[0])].append(line)

for i in range(0,10):
	ii = 0
	for ll in a[i]:
		if ii < 3:
			ftt.write(ll)
		else:
			ft.write(ll)
		ii+=1

f.close()
ft.close()
ftt.close()