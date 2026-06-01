<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
</head>
<body>
    <h2>게시판 로그인</h2>
    <form action="login_action.php" method="POST">
        <p>아이디: <input type="text" name="username" required></p>
        <p>비밀번호: <input type="password" name="password" required></p>
        <button type="submit">로그인</button>
    </form>
    <br>
    <a href="register.php">아직 회원이 아니신가요? (회원가입)</a>
</body>
</html>