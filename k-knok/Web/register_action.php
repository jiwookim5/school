<?php
include 'db.php';

$username = trim($_POST['username']);
$password = $_POST['password'];

// 1. 중복 아이디 체크
$check_stmt = $conn->prepare("SELECT id FROM users WHERE username = ?");
$check_stmt->bind_param("s", $username);
$check_stmt->execute();
$check_stmt->store_result();

if ($check_stmt->num_rows > 0) {
    echo "<script>alert('이미 존재하는 아이디입니다.'); history.back();</script>";
} else {
    // 2. 비밀번호 해싱 (보안의 핵심!)
    $hashed_password = password_hash($password, PASSWORD_DEFAULT);

    // 3. 데이터 삽입
    $stmt = $conn->prepare("INSERT INTO users (username, password) VALUES (?, ?)");
    $stmt->bind_param("ss", $username, $hashed_password);
    
    if ($stmt->execute()) {
        echo "<script>alert('회원가입 성공!'); location.href='login.php';</script>";
    } else {
        echo "<script>alert('가입 실패: 관리자에게 문의하세요.');</script>";
    }
}
?>