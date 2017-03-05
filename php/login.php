<?php
include "conn.php";
$username = $_POST["user_name"];
$password = $_POST["password"];
$sql = "SELECT * FROM employee_data WHERE username='$username' AND password='$password'"; 
$result = mysqli_query($conn,$sql);
if(mysqli_num_rows($result) > 0){
	echo "login success";
}
else{
	echo "login not success";
}
?>