<?php
	$action = $_POST['action']; // поменять на POST

	function getAllUser() {
		$servername = "p63488.mysql.ihc.ru";
		$username = "p63488_site";
		$password = "SQwktw3hm3";
		$dbname = "p63488_site";

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

		$sql = "SELECT `name_user`, `date` FROM `statistics` WHERE `name_user` != '' GROUP BY day(`date`) ASC, `name_user`";

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

		while ($row = mysql_fetch_assoc($result)) {
			$array[$i] = $row;
			$i++;
		}

		mysql_close ($conn);
		return $array;
	}
	
	switch ($action) {
		case 'getAllUser':
			$array = getAllUser();
			echo json_encode($array);
			break;
	
		default:
			# code...
			break;
	}
?>