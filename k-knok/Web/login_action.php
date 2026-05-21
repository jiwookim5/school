<?php
include 'db.php';

$username = $_POST['username'];
$password = $_POST['password'];

// DB에서 해당 아이디 유저 찾기
$stmt = $conn->prepare("SELECT id, password FROM users WHERE username = ?");
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    // 아까 DB에 수동으로 넣은 '1234' 문정값과 직접 비교
    if ($password === $row['password']) {
        // 로그인 성공 시 세션에 유저 PK ID(1)를 저장!
        $_SESSION['user_id'] = $row['id'];
        $_SESSION['username'] = $username;
        
        // 여기를 index.php로 바꿨습니다!
        echo "<script>alert('로그인 성공!'); location.href='index.php';</script>";
    } else {
        echo "<script>alert('비밀번호가 일치하지 않습니다.'); history.back();</script>";
    }
} else {
    echo "<script>alert('존재하지 않는 아이디입니다.'); history.back();</script>";
}
?>