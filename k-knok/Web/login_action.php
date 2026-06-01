<?php
session_start();
include 'db.php';

// [수정사항] 로그인 시도 파일에서는 아래 세션 체크를 삭제해야 합니다.
// (로그인 전에는 당연히 세션이 없기 때문입니다.)

$username = $_POST['username'] ?? '';
$password = $_POST['password'] ?? '';

$stmt = $conn->prepare("SELECT username, password FROM users WHERE username = ?");
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    if (password_verify($password, $row['password'])) {
        $_SESSION['username'] = $row['username']; // 세션 변수 통일
        
        // [수정사항] main.php가 실제 서버 폴더에 존재하는지 확인하세요.
        // 파일명이 index.php라면 아래를 index.php로 바꾸세요.
        echo "<script>alert('로그인 성공!'); location.href='index.php';</script>";
    } else {
        echo "<script>alert('비밀번호가 일치하지 않습니다.'); history.back();</script>";
    }
} else {
    echo "<script>alert('존재하지 않는 아이디입니다.'); history.back();</script>";
}
?>