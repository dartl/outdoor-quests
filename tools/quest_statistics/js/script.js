var mFile;
var mJson;
var mQuest;
var mQuests = new Array();

window.onload = function() {
    document.getElementById('file-input').addEventListener('change', readSingleFile, false);
    document.getElementById('open').addEventListener('click', displayContents, false);
};

function readSingleFile(e) {
    mFile = e.target.files[0];
    if (!mFile) {
        return;
    }
    var reader = new FileReader();
    reader.onload = function(e) {
        mJson = e.target.result;
    };
    reader.readAsText(mFile);
}

function displayContents() {
    var quest_json = JSON.parse(mJson);
    for (var i = 0; i < quest_json.length; i++) {
        mQuests[i] = new Quest(quest_json[i].number,quest_json[i].name);
        points = quest_json[i].points;
        for (var j = 0; j < points.length; j++) {
            mQuests[i].addMark(new Mark(points[j].number,
                points[j].name,
                points[j].long,
                points[j].lat,
                points[j].radius)
            );
        }
    }
    startQuestOnMap();
}

function startQuestOnMap() {
    console.log("startQuestOnMap(). mQuests.length = " + mQuests.length);
    for (var i = 0; i < mQuests.length; i++) {
        var сoordinates = new Array();
        var marks = mQuests[i].getMarks();

        for (var j = 0; j < marks.length; j++) {
            newCoordinate = [marks[j].getLat(), marks[j].getLong()];
            сoordinates.push(newCoordinate);
        }
        setTimeout(nextRoute, 1000*i, mQuests[i], сoordinates);
    }
}

function nextRoute(route, сoordinates) {
            
        multiRoute = new ymaps.multiRouter.MultiRoute({
            referencePoints: сoordinates,
            params: {
                //Тип маршрутизации - пешеходная маршрутизация.
                routingMode: 'pedestrian',
            }
        });

        
        // Событие для выбора длинны маршрута и других данных
        multiRoute.model.events.add("requestsuccess", function (event) {
            $("div.information table tbody tr.sum").before("<tr><td>" 
                + route.name + "</td><td>" 
                + multiRoute.getRoutes().get(0).properties.get("distance").value + "</td><td>"
                + multiRoute.getRoutes().get(0).properties.get("duration").value + "</td></tr>");
            sum_duration = parseInt($("div.information table tr.sum td.duration").text());
            sum_duration += multiRoute.getRoutes().get(0).properties.get("duration").value;
            $("div.information table tr.sum td.duration").text(sum_duration);

            sum_distance = parseInt($("div.information table tr.sum td.distance").text());
            sum_distance += multiRoute.getRoutes().get(0).properties.get("distance").value;
            $("div.information table tr.sum td.distance").text(sum_distance);
        });
}

class Quest {
    constructor(number, name) {
        this.number = number;
        this.name = name;
        this.marks = new Array();
        this.time = 0;
        this.distance = 0;
    }

    addMark(mark) {
        this.marks.push(mark);
    }

    toString() {
        var text = "";
        text += "Название : " + this.name;
        text += ", Места : ["
        for (var i = 0; i < this.marks.length; i++) {
            text += "\r\n\t";
            text += this.marks[i].toString();
        }
        text += "\r\n]"
        return text;
    }

    getMarks() {
        return this.marks;
    }

    setDistance(dist) {
        this.distance = dist;
    }

    setTime(t) {
        this.time = t;
    }

    getDistance() {
        return this.distance;
    }

    getTime() {
        return this.time;
    }
}

class Mark {
    constructor(number, name, long, lat, radius) {
        this.number = number;
        this.name = name;
        this.long = long;
        this.lat = lat;
        this.radius = radius;
    }

    toString() {
        var text = "{ Номер(id) : " + this.number;
        text += ", Название : " + this.name;
        text += ", Long : " + this.long;
        text += ", Lat : " + this.lat;
        text += ", Радиус до обнаружения : " + this.radius;
        text += " }";
        return text;
    }

    getLong() {
        return this.long;
    }

    getLat() {
        return this.lat;
    }

    getNumber() {
        return this.number;
    }

    getName() {
        return this.name;
    }
}

/*
 * Класс, позволяющий создавать стрелку на карте.
 * Является хелпером к созданию полилинии, у которой задан специальный оверлей.
 * При использовании модулей в реальном проекте рекомендуем размещать их в отдельных файлах.
 */
ymaps.modules.define("geoObject.Arrow", [
    'Polyline',
    'overlay.Arrow',
    'util.extend'
], function (provide, Polyline, ArrowOverlay, extend) {
    /**
     * @param {Number[][] | Object | ILineStringGeometry} geometry Геометрия ломаной.
     * @param {Object} properties Данные ломаной.
     * @param {Object} options Опции ломаной.
     * Поддерживается тот же набор опций, что и в классе ymaps.Polyline.
     * @param {Number} [options.arrowAngle=20] Угол в градусах между основной линией и линиями стрелки.
     * @param {Number} [options.arrowMinLength=3] Минимальная длина стрелки. Если длина стрелки меньше минимального значения, стрелка не рисуется.
     * @param {Number} [options.arrowMaxLength=20] Максимальная длина стрелки.
     */
    var Arrow = function (geometry, properties, options) {
        return new Polyline(geometry, properties, extend({}, options, {
            lineStringOverlay: ArrowOverlay
        }));
    };
    provide(Arrow);
});

/*
 * Класс, реализующий интерфейс IOverlay.
 * Получает на вход пиксельную геометрию линии и добавляет стрелку на конце линии.
 */
ymaps.modules.define("overlay.Arrow", [
    'overlay.Polygon',
    'util.extend',
    'event.Manager',
    'option.Manager',
    'Event',
    'geometry.pixel.Polygon'
], function (provide, PolygonOverlay, extend, EventManager, OptionManager, Event, PolygonGeometry) {
    var domEvents = [
            'click',
            'contextmenu',
            'dblclick',
            'mousedown',
            'mouseenter',
            'mouseleave',
            'mousemove',
            'mouseup',
            'multitouchend',
            'multitouchmove',
            'multitouchstart',
            'wheel'
        ],

        /**
         * @param {geometry.pixel.Polyline} pixelGeometry Пиксельная геометрия линии.
         * @param {Object} data Данные оверлея.
         * @param {Object} options Опции оверлея.
         */
        ArrowOverlay = function (pixelGeometry, data, options) {
            // Поля .events и .options обязательные для IOverlay.
            this.events = new EventManager();
            this.options = new OptionManager(options);
            this._map = null;
            this._data = data;
            this._geometry = pixelGeometry;
            this._overlay = null;
        };

    ArrowOverlay.prototype = extend(ArrowOverlay.prototype, {
        // Реализовываем все методы и события, которые требует интерфейс IOverlay.
        getData: function () {
            return this._data;
        },

        setData: function (data) {
            if (this._data != data) {
                var oldData = this._data;
                this._data = data;
                this.events.fire('datachange', {
                    oldData: oldData,
                    newData: data
                });
            }
        },

        getMap: function () {
            return this._map;
        },

        setMap: function (map) {
            if (this._map != map) {
                var oldMap = this._map;
                if (!map) {
                    this._onRemoveFromMap();
                }
                this._map = map;
                if (map) {
                    this._onAddToMap();
                }
                this.events.fire('mapchange', {
                    oldMap: oldMap,
                    newMap: map
                });
            }
        },

        setGeometry: function (geometry) {
            if (this._geometry != geometry) {
                var oldGeometry = geometry;
                this._geometry = geometry;
                if (this.getMap() && geometry) {
                    this._rebuild();
                }
                this.events.fire('geometrychange', {
                    oldGeometry: oldGeometry,
                    newGeometry: geometry
                });
            }
        },

        getGeometry: function () {
            return this._geometry;
        },

        getShape: function () {
            return null;
        },

        isEmpty: function () {
            return false;
        },

        _rebuild: function () {
            this._onRemoveFromMap();
            this._onAddToMap();
        },

        _onAddToMap: function () {
            // Военная хитрость - чтобы в прозрачной ломаной хорошо отрисовывались самопересечения,
            // мы рисуем вместо линии многоугольник.
            // Каждый контур многоугольника будет отвечать за часть линии.
            this._overlay = new PolygonOverlay(new PolygonGeometry(this._createArrowContours()));
            this._startOverlayListening();
            // Эта строчка свяжет два менеджера опций.
            // Опции, заданные в родительском менеджере,
            // будут распространяться и на дочерний.
            this._overlay.options.setParent(this.options);
            this._overlay.setMap(this.getMap());
        },

        _onRemoveFromMap: function () {
            this._overlay.setMap(null);
            this._overlay.options.setParent(null);
            this._stopOverlayListening();
        },

        _startOverlayListening: function () {
            this._overlay.events.add(domEvents, this._onDomEvent, this);
        },

        _stopOverlayListening: function () {
            this._overlay.events.remove(domEvents, this._onDomEvent, this);
        },

        _onDomEvent: function (e) {
            // Мы слушаем события от дочернего служебного оверлея
            // и прокидываем их на внешнем классе.
            // Это делается для того, чтобы в событии было корректно определено
            // поле target.
            this.events.fire(e.get('type'), new Event({
                target: this
            // Свяжем исходное событие с текущим, чтобы все поля данных дочернего события
            // были доступны в производном событии.
            }, e));
        },

        _createArrowContours: function () {
            var contours = [],
                mainLineCoordinates = this.getGeometry().getCoordinates(),
                arrowLength = calculateArrowLength(
                    mainLineCoordinates,
                    this.options.get('arrowMinLength', 3),
                    this.options.get('arrowMaxLength', 20)
                );
            contours.push(getContourFromLineCoordinates(mainLineCoordinates));
            // Будем рисовать стрелку только если длина линии не меньше длины стрелки.
            if (arrowLength > 0) {
                // Создадим еще 2 контура для стрелочек.
                var lastTwoCoordinates = [
                        mainLineCoordinates[mainLineCoordinates.length - 2],
                        mainLineCoordinates[mainLineCoordinates.length - 1]
                    ],
                // Для удобства расчетов повернем стрелку так, чтобы она была направлена вдоль оси y,
                // а потом развернем результаты обратно.
                    rotationAngle = getRotationAngle(lastTwoCoordinates[0], lastTwoCoordinates[1]),
                    rotatedCoordinates = rotate(lastTwoCoordinates, rotationAngle),

                    arrowAngle = this.options.get('arrowAngle', 20) / 180 * Math.PI,
                    arrowBeginningCoordinates = getArrowsBeginningCoordinates(
                        rotatedCoordinates,
                        arrowLength,
                        arrowAngle
                    ),
                    firstArrowCoordinates = rotate([
                        arrowBeginningCoordinates[0],
                        rotatedCoordinates[1]
                    ], -rotationAngle),
                    secondArrowCoordinates = rotate([
                        arrowBeginningCoordinates[1],
                        rotatedCoordinates[1]
                    ], -rotationAngle);

                contours.push(getContourFromLineCoordinates(firstArrowCoordinates));
                contours.push(getContourFromLineCoordinates(secondArrowCoordinates));
            }
            return contours;
        }
    });

    function getArrowsBeginningCoordinates (coordinates, arrowLength, arrowAngle) {
        var p1 = coordinates[0],
            p2 = coordinates[1],
            dx = arrowLength * Math.sin(arrowAngle),
            y = p2[1] - arrowLength * Math.cos(arrowAngle);
        return [[p1[0] - dx, y], [p1[0] + dx, y]];
    }

    function rotate (coordinates, angle) {
        var rotatedCoordinates = [];
        for (var i = 0, l = coordinates.length, x, y; i < l; i++) {
            x = coordinates[i][0];
            y = coordinates[i][1];
            rotatedCoordinates.push([
                x * Math.cos(angle) - y * Math.sin(angle),
                x * Math.sin(angle) + y * Math.cos(angle)
            ]);
        }
        return rotatedCoordinates;
    }

    function getRotationAngle (p1, p2) {
        return Math.PI / 2 - Math.atan2(p2[1] - p1[1], p2[0] - p1[0]);
    }

    function getContourFromLineCoordinates (coords) {
        var contour = coords.slice();
        for (var i = coords.length - 2; i > -1; i--) {
            contour.push(coords[i]);
        }
        return contour;
    }

    function calculateArrowLength (coords, minLength, maxLength) {
        var linePixelLength = 0;
        for (var i = 1, l = coords.length; i < l; i++) {
            linePixelLength += getVectorLength(
                coords[i][0] - coords[i - 1][0],
                coords[i][1] - coords[i - 1][1]
            );
            if (linePixelLength / 3 > maxLength) {
                return maxLength;
            }
        }
        var finalArrowLength = linePixelLength / 3;
        return finalArrowLength < minLength ? 0 : finalArrowLength;
    }

    function getVectorLength (x, y) {
        return Math.sqrt(x * x + y * y);
    }

    provide(ArrowOverlay);
});