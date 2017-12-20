<?php
	$servername = "p63488.mysql.ihc.ru";
	$username = "p63488_site";
	$password = "SQwktw3hm3";
	$dbname = "p63488_site";

	$name = $_POST['username'];
	$lat = $_POST['lat'];
	$long = $_POST['long'];

	// Create connection
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	} 

	$sql = "INSERT INTO `statistics` (`id`, `name_user`, `lat`, `lng`) VALUES (NULL, '".$name."', '".$lat."', '".$long."')";

	if ($conn->query($sql) === TRUE) {
	    echo "New record created successfully";
	} else {
	    echo "Error: " . $sql . "<br>" . $conn->error;
	}

	$conn->close();
?>