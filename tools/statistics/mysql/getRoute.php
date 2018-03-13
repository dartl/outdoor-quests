<?php
	$servername = "p63488.mysql.ihc.ru";
	$username = "p63488_site";
	$password = "SQwktw3hm3";
	$dbname = "p63488_site";

	$name = $_POST['username']; // поменять на POST
	$date = $_POST['date']; // поменять на POST


	// Create connection
	$conn = mysql_connect($servername, $username, $password);

	if (!$conn) {
	    echo "Unable to connect to DB: " . mysql_error();
	    exit;
	}

	if (!mysql_select_db($dbname)) {
	    echo "Unable to select mydbname: " . mysql_error();
	    exit;
	}


	// Check connection
	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	} 

	$sql = "SELECT `lat`, `lng`, `date` FROM `statistics` WHERE  `name_user` = '" . $name . "' AND `date` between '" . $date . "' and '". $date ." 23:59:59' ORDER BY `date`";

	$result = mysql_query($sql);


	if (!$result) {
	    echo "Could not successfully run query ($sql) from DB: " . mysql_error();
	    exit;
	}

	if (mysql_num_rows($result) == 0) {
	    echo "No rows found, nothing to print so am exiting";
	    exit;
	}
	
	$array = array();
	$i = 0;

	while ($row = mysql_fetch_row($result)) {
	    $array[$i] = $row;
		$i++;
	}

	mysql_close ($conn);

	echo json_encode($array);
?>