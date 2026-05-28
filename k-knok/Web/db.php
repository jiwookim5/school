<?php
$host = 'mysql-db';
$user = 'developer';
$pass = 'password123!';
$db   = 'study_board';
$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) { 
    die('연결 실패: ' . $conn->connect_error); 
}
?>