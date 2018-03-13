var allUsers = new Array();
var myMap;
var myPolyline;
var RouteOne = [[59.934135,30.333871],[59.934619,30.333493],[59.934480,30.335057],[59.936017,30.336155],[59.936032,30.338541],[59.936240,30.341703],[59.933742,30.340995]];
var RouteTwo = [[59.933329,30.336903],[59.933308,30.34288],[59.928876,30.334634],[59.932111,30.330749],[59.931024,30.326875]];
var yellowCollection;


$(document).ready(function() {
	getAllUsers();
	$( ".selectUser > button" ).click(function() {
		startRoute($(".selectUser > select").val());
	});

	ymaps.ready(init);
});

function init(){     
    myMap = new ymaps.Map("map", {
        center: [55.76, 37.64],
        zoom: 7
    });

    yellowCollection = new ymaps.GeoObjectCollection(null, {
            preset: 'islands#yellowIcon'
        });

    myMap.geoObjects.add(yellowCollection);

    multiRoute = new ymaps.multiRouter.MultiRoute({
            referencePoints: RouteOne,
            params: {
                //Тип маршрутизации - пешеходная маршрутизация.
                routingMode: 'pedestrian'
            }
        }, {
            // Автоматически устанавливать границы карты так, чтобы маршрут был виден целиком.
            boundsAutoApply: true
        });

    // Добавляем мультимаршрут на карту.
    myMap.geoObjects.add(multiRoute);

    multiRoute1 = new ymaps.multiRouter.MultiRoute({
            referencePoints: RouteTwo,
            params: {
                //Тип маршрутизации - пешеходная маршрутизация.
                routingMode: 'pedestrian'
            }
        }, {
            // Автоматически устанавливать границы карты так, чтобы маршрут был виден целиком.
            boundsAutoApply: true
        });

    // Добавляем мультимаршрут на карту.
    myMap.geoObjects.add(multiRoute1);
}

function getAllUsers() {
	$.ajax({
		url:"mysql/functions.php",
		type:"POST",
		data: "action=getAllUser",
		success:function(data){
			allUsers = data;
			refreshSelect();
		},
		dataType:"json"
	});
}

function refreshSelect() {
	var select = $(".selectUser > select");
	$.each(allUsers, function(key, value) {   

		date = new Date(value['date']);

		var options = {
			year: 'numeric',
			month: 'long',
			day: 'numeric',
			timezone: 'UTC',
		};

		select.append(
			$("<option></option>")
				.attr("value",key)
				.text(value['name_user'] + " / " + date.toLocaleString("ru", options))
					); 
	});
} 

function startRoute(i) {
	var object = allUsers[i];
	date = new Date(object['date']);
	username = object['name_user'];

	console.log("startRoute i = " + i);

	$.ajax({
		url:"mysql/getRoute.php",
		type:"POST",
		data: "username=" + username + "&date=" + date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate(),
		success:function(data){
			endRoute(data);
		},
		dataType:"json"
	});
}


function endRoute(data) {
	yellowCollection.removeAll();

	var color = "000000";

	console.log("data[0] = " + data[0]);


    var myPlacemark = new ymaps.Placemark(data[0], {
        // Чтобы балун и хинт открывались на метке, необходимо задать ей определенные свойства.
        balloonContentHeader: "Старт",
        hintContent: "Старт"
    } , {
        preset: 'twirl#redIcon' 
    });

    yellowCollection.add(myPlacemark);

    myPlacemark = new ymaps.Placemark(data[data.length-1], {
        // Чтобы балун и хинт открывались на метке, необходимо задать ей определенные свойства.
        balloonContentHeader: "Финиш",
        hintContent: "Финиш"
    });

    yellowCollection.add(myPlacemark);

	// Создаем ломаную с помощью вспомогательного класса Polyline.
	for (var i = 2; i < data.length; i = i + 2) {
	    myPolyline = new ymaps.Polyline([data[i-2], data[i-1], data[i]], {
	            // Описываем свойства геообъекта.
	            // Содержимое балуна.
	            balloonContent: "Ломаная линия"
	        }, {
	            // Задаем опции геообъекта.
	            // Отключаем кнопку закрытия балуна.
	            balloonCloseButton: false,
	            // Цвет линии.
	            strokeColor: "#" + color,
	            // Ширина линии.
	            strokeWidth: 3,
	            // Коэффициент прозрачности.
	            strokeOpacity: 1,
	            
	        });

	    color = upColor(color);

	    // Добавляем линии на карту.
	    yellowCollection.add(myPolyline);
	}
	showStatistics(data);
}

var count = 2;
var check =  true;

function upColor(color) {
	var numbers = new Array();
	numbers = hexToRGB(parseInt(color,16));

	if (numbers[0] >= 255 - count) {
		check = false;
	} 

	if (numbers[0] <= 0) {
		check = true;
	}

	if (check) {
		numbers[0] = numbers[0] + count;
	} else {
		numbers[0] = numbers[0] - count;
	}


	color = RGBToHex(numbers[0],numbers[1],numbers[2]);

	return color;
}

function replaceAt(string, index, replace) {
  return string.substring(0, index) + replace + string.substring(index + 1);
}

// convert a hexidecimal color string to 0..255 R,G,B
hexToRGB = function(hex){
    var r = hex >> 16;
    var g = hex >> 8 & 0xFF;
    var b = hex & 0xFF;
    return [r,g,b];
}

RGBToHex = function(r,g,b){
    var bin = r << 16 | g << 8 | b;
    return (function(h){
        return new Array(7-h.length).join("0")+h
    })(bin.toString(16).toUpperCase())
}

function showStatistics(data) {
	$(".statistics").empty();

	var timeStart = new Date();
	timeStart.setTime(Date.parse(data[1][2]));

	var timeEnd = new Date();
	timeEnd.setTime(Date.parse(data[data.length-1][2]));

	var time = "<p>Общее время прохождения: "+ (+timeEnd-timeStart) / 1000 / 60 +" мин.</p>";
	$(".statistics").append(time);
	console.log();
}