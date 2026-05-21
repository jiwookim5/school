<?php
include 'db.php';
if (!isset($_SESSION['user_id'])) {
    echo "<script>alert('로그인이 필요한 기능입니다.'); location.href='login.php';</script>";
    exit;
}
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>새 게시글 작성</title>
</head>
<body>
    <h2>새 게시글 작성</h2>
    <form action="write_action.php" method="POST">
        <p>제목: <input type="text" name="title" required></p>
        <p>내용:</p>
        <textarea name="content" rows="10" cols="50" required></textarea>
        <br>
        <button type="submit">작성 완료</button>
        <a href="index.php" style="margin-left: 10px;">[목록으로]</a>
    </form>
</body>
</html>