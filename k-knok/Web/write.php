<?php
session_start();
if (!isset($_SESSION['username'])) {
    echo "<script>alert('로그인이 필요합니다.'); location.href='login.php';</script>";
    exit;
}
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>글쓰기</title>
</head>
<body>
    <h2>글 작성</h2>
    <form action="write_action.php" method="POST" enctype="multipart/form-data">
        <p>제목: <input type="text" name="title" required></p>
        <p>내용:</p>
        <textarea name="content" rows="10" cols="50" required></textarea>
        
        <br>
        <p>파일 첨부: <input type="file" name="upload_file[]" multiple></p>
        
        <button type="submit">작성 완료</button>
        <a href="index.php">[목록으로]</a>
    </form>
</body>
</html>