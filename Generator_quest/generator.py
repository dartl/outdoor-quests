# -*- coding: utf-8 -*- 
import json
import codecs
import pymongo
import itertools
import timeit
import math
from lib.route import *
''' Функция расчета растояний межджу координатами напрямую '''
def getDistance(llat1, llong1, llat2, llong2):
	#pi - число pi, rad - радиус сферы (Земли)
	rad = 6372795

	#в радианах
	lat1 = llat1*math.pi/180.
	lat2 = llat2*math.pi/180.
	long1 = llong1*math.pi/180.
	long2 = llong2*math.pi/180.

	#косинусы и синусы широт и разницы долгот
	cl1 = math.cos(lat1)
	cl2 = math.cos(lat2)
	sl1 = math.sin(lat1)
	sl2 = math.sin(lat2)
	delta = long2 - long1
	cdelta = math.cos(delta)
	sdelta = math.sin(delta)

	#вычисления длины большого круга
	y = math.sqrt(math.pow(cl2*sdelta,2)+math.pow(cl1*sl2-sl1*cl2*cdelta,2))
	x = sl1*sl2+cl1*cl2*cdelta
	ad = math.atan2(y,x)
	dist = ad*rad

	#вычисление начального азимута
	x = (cl1*sl2) - (sl1*cl2*cdelta)
	y = sdelta*cl2
	z = math.degrees(math.atan(-y/x))

	if (x < 0):
	 z = z+180.

	z2 = (z+180.) % 360. - 180.
	z2 = - math.radians(z2)
	anglerad2 = z2 - ((2*math.pi)*math.floor((z2/(2*math.pi))) )
	angledeg = (anglerad2*180.)/math.pi

	#print 'Distance >> %.0f' % dist, ' [meters]'
	#print 'Initial bearing >> ', angledeg, '[degrees]'

	return dist

''' Класс места на карте '''
class Place:
        images = []
        def place(self, name, lng, lat, description):
            self.name = name
            self.lng = lng
            self.lat = lat
            self.description = description

        def getName():
        	return self.name

''' Класс метки квеста '''
class Marker:
        place = Place()
        helps = []
        def marker(self, place, helps):
                self.place = place
                self.helps = helps

''' Класс подсказки '''
class Help:
        def help_quest(self, type_help, text):
                self.type_help = type_help
                self.text = text

# Функция, проверяющая наличие тегов у метки
def checkTags(tags_config, tags):
	for tag_config in tags_config:
		for tag in tags:
			if (tag_config == tag):
				return True
	return False

# функция подключающая к БД и достающая из нее все места, которые подходят нам по тегам
def getPlaces(tags, max_places):
	places = []
	client = pymongo.MongoClient("localhost", 27017)
	db = client.test
	places_db = db.places
	# Обходим места и если теги подходят - добавляем его в список мест для дальнейшей работы
	for place in places_db.find():
		if len(places) >= max_places:
			break
		if checkTags(tags,place["tags"]):
			temp = Place()
			temp.place(place["name"],place["long"],place["lat"],place["description"])
			print place["name"]
			places.append(temp)
	return places

def exportToJSON(places,quest_options):
	with codecs.open('quests.json','w',encoding='utf8') as data_file:
		basic_entry = []
		j = 0
		k = 0
		for x in quest_options:
			quest  = {}
			points = []
			for i in x:
				point = {}
				point["number"] = j
				j += 1
				point["name"] = places[i].name
				point["lat"] = places[i].lat
				point["long"] = places[i].lng
				point["description"] = places[i].description
				point["radius"] = 250
				points.append(point)
			j = 0
			quest["points"] = points
			quest["number"] = k
			k += 1
			quest["name"] = "Сгенерированный квест №%d" % k
			basic_entry.append(quest)
		json.dump(basic_entry, data_file, indent=4, encoding="utf8", ensure_ascii=False)

def filterQuestMethod1(quest_options, places):
	k = 0
	new_quest_options = [];
	checkQuest = True
	for x in quest_options:
		checkQuest = True
		k = 0
		for i in x:
			if k > 2:
				N = x[k-2]
				M = x[k-1]

				data = LoadOsm("foot")

				node1 = data.findNode(places[N].lat,places[N].lng)
				node2 = data.findNode(places[M].lat,places[M].lng)
				node3 = data.findNode(places[i].lat,places[i].lng)

				router = Router(data)

				dist1 = router.distance(node1, node3)
				dist2 = router.distance(node2, node3)
				dist3 = router.distance(node1, node2)

				cos = ((dist3*dist3) - (dist1*dist1) + dist2*dist2) / (2 * dist3 * dist2)
				arcCosinus = math.acos(cos)
				print arcCosinus

				#if arcCosinus < 1:
					#checkQuest = False

				#if dist1 > dist2:
					#checkQuest = False
			k += 1
		if checkQuest:
			new_quest_options.append(x)
	return new_quest_options

def filterQuestMethod2(quest_options, places):
	k = 0
	new_quest_options = [];
	checkQuest = True
	for x in quest_options:
		checkQuest = True
		k = 0
		for i in x:
			if k > 2:
				N = x[k-2]
				M = x[k-1]
				dist1 = getDistance(places[N].lat,places[N].lng,places[i].lat,places[i].lng)
				dist2 = getDistance(places[M].lat,places[M].lng,places[i].lat,places[i].lng)
				dist3 = getDistance(places[N].lat,places[N].lng,places[M].lat,places[M].lng)

				cos = ((dist3*dist3) - (dist1*dist1) + dist2*dist2) / (2 * dist3 * dist2)
				arcCosinus = math.acos(cos)
				print arcCosinus

				if arcCosinus < 1:
					checkQuest = False

				#if dist1 > dist2:
					#checkQuest = False
			k += 1
		if checkQuest:
			new_quest_options.append(x)
	return new_quest_options

def start_generate(max_numbers_places, time_quest, tags):

	places = getPlaces(tags,max_numbers_places)
	N = len(places)	# количество мест
	if N < max_numbers_places: # если количество подходящих мест меньше максимального порога
		M = N 	# тогда генерируем массивы размером N
	else:
		M = max_numbers_places 	# иначе генерируем массивы размера максимального порога

	quest_options = itertools.permutations(range(0,N),M)

	#quest_options = filterQuestMethod1(quest_options, places)
	#quest_options = filterQuestMethod2(quest_options, places)

	exportToJSON(places,quest_options)
	return 0

''' Запускаем всю программу '''
a = timeit.default_timer()
with codecs.open('arguments.json','r', 'utf-8-sig') as data_file:
	json_load = json.load(data_file)
	start_generate(json_load["max_places"],json_load["max_time"],json_load["tags"])
print(timeit.default_timer()-a)
