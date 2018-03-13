# -*- coding: utf-8 -*- 
import json
import codecs
import os, fnmatch
import sys  

class Argument:
	def argument(self, name, extension, constant, value):
		self.name = name
		self.extension = extension
		self.constant = constant
		self.value = value
	
	def getExtension(self):
		return self.extension

	def getConstant(self):
		return self.constant

	def getValue(self):
		return self.value


def generateArguments(json_load):
	arguments = []
	for i in json_load:
		print(i['name'])
		print(i['fileExtension'])
		print(i['constant'])
		print(i['value'])
		temp = Argument()
		temp.argument(i['name'],i['fileExtension'],i['constant'],i['value'])
		arguments.append(temp)
	return arguments
	

def findReplace(directory, find, replace, filePattern):
	for path, dirs, files in os.walk(os.path.abspath(directory)):
		for filename in fnmatch.filter(files, filePattern):
			filepath = os.path.join(path, filename)
			with open(filepath) as f:
				s = f.read()
			s = s.replace(find, replace)
			with open(filepath, "w") as f:
				f.write(s)

with codecs.open('arguments.json','r', 'utf-8-sig') as data_file:
	reload(sys)  
	sys.setdefaultencoding('utf8')
	json_load = json.load(data_file)
	arguments = generateArguments(json_load)
	directory = "HistoryGeocachingDemo"
	for i in arguments:
		findReplace(directory, i.getConstant(), i.getValue(), i.getExtension())