<?php
session_start(); 
include 'db.php';

$username = trim($_POST['username']);
$password = trim($_POST['password']);

$stmt = $conn->prepare("SELECT id, password FROM users WHERE username = ?");
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    if (password_verify($password, $row['password'])) {
        $_SESSION['user_id'] = $row['id'];
        $_SESSION['username'] = $username; 
        echo "<script>alert('로그인 성공!'); location.href='index.php';</script>";
    } else {
        echo "<script>alert('비밀번호가 일치하지 않습니다.'); history.back();</script>";
    }
} else {
    echo "<script>alert('존재하지 않는 아이디입니다.'); history.back();</script>";
}
?>