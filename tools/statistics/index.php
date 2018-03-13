<HTML>
    <HEAD>
        <TITLE>Инструмент - информация о действиях пользователей в демо-версии</TITLE>
        <script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
        <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
        <script type="text/javascript" src="js/script.js"></script>

        <link href="css/style.css" rel="stylesheet">
        <link href="css/bootstrap.min.css" rel="stylesheet">
    </HEAD>
    <BODY>
        <?php 
            include 'mysql/functions.php';
        ?>
        <div class="content">
	        <h1>Инструмент - информация о действиях пользователей в демо-версии</h1>
            <div class="selectUser text-center">
                <select></select>
                <button type="button" class="btn btn-success">Отобразить</button>
            </div>
            <div id="map" style="width: 100%; height: 600px"></div>
            <div class="statistics"></div>
        </div>
    </BODY>
</HTML>