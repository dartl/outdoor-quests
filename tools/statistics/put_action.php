<?php
	$servername = "p63488.mysql.ihc.ru";
	$username = "p63488_site";
	$password = "SQwktw3hm3";
	$dbname = "p63488_site";

	$name = $_POST['username'];
	$action = $_POST['action'];

	// Create connection
	$conn = new mysqli($servername, $username, $password, $dbname);
	
	$conn->set_charset("utf8");

	// Check connection
	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	} 

	$sql = "INSERT INTO `actions` (`id`, `name_user`, `action`) VALUES (NULL, '".$name."', '".$action."')";

	if ($conn->query($sql) === TRUE) {
	    echo "New record created successfully";
	} else {
	    echo "Error: " . $sql . "<br>" . $conn->error;
	}

	$conn->close();
?>