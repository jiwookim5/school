<?php
// register.php
include 'db.php';

// 사용자가 회원가입 버튼을 눌러서 POST 요청을 보냈을 때만 실행
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = trim($_POST['username']);
    $password = trim($_POST['password']);

    if (empty($username) || empty($password)) {
        echo "<script>alert('아이디와 비밀번호를 모두 입력해주세요.'); history.back();</script>";
        exit;
    }

    // 비밀번호 안전하게 해시화
    $hashed_password = password_hash($password, PASSWORD_BCRYPT);

    //SQL 인젝션 방지를 위한 Prepared Statement 사용
    // 먼저 아이디 중복 체크
    $stmt = $conn->prepare("SELECT username FROM users WHERE username = ?");
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        echo "<script>alert('이미 존재하는 아이디입니다.'); history.back();</script>";
        $stmt->close();
        exit;
    }
    $stmt->close();

    // 중복이 없으면 DB에 인서트
    $stmt = $conn->prepare("INSERT INTO users (username, password) VALUES (?, ?)");
    $stmt->bind_param("ss", $username, $hashed_password);

    if ($stmt->execute()) {
        echo "<script>alert('회원가입이 완료되었습니다! 로그인해주세요.'); location.href='login.php';</script>";
    } else {
        echo "<script>alert('오류가 발생했습니다.'); history.back();</script>";
    }
    $stmt->close();
    exit;
}
?>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
</head>
<body>
    <h2>회원가입</h2>
    <form action="register.php" method="POST">
        <p>아이디: <input type="text" name="username" required></p>
        <p>비밀번호: <input type="password" name="password" required></p>
        <button type="submit">가입하기</button>
    </form>
    <p>이미 계정이 있으신가요? <a href="login.php">로그인하기</a></p>
</body>
</html>