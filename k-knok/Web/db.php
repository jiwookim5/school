<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);

$host = '127.0.0.1';
$db   = 'study_board';
$user = 'developer';
$pass = 'password123!';
$charset = 'utf8mb4';

// 변수 이름을 $user, $pass, $db로 매칭했습니다!
$conn = new mysqli($host, $user, $pass, $db);

// 연결 오류 확인
if ($conn->connect_error) {
    die("DB 연결 실패: " . $conn->connect_error);
}

// 한글 깨짐 방지
$conn->set_charset("utf8mb4");

// 세션 시작
if (session_status() == PHP_SESSION_NONE) {
    session_start();
}
?>

