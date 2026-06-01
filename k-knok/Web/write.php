<?php
session_start();
if (!isset($_SESSION['username'])) {
    echo "<script>alert('로그인이 필요합니다.'); location.href='login.php';</script>";
    exit;
}

// URL 파라미터에서 board 값을 가져옵니다 (없으면 기본값 'free')
$board = $_GET['board'] ?? 'free';
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>글쓰기</title>
</head>
<body>
    <h2>글 작성 (<?php echo ($board == 'qna') ? '질문게시판' : '자유게시판'; ?>)</h2>
    
    <form action="write_action.php" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="board" value="<?php echo htmlspecialchars($board); ?>">
        
        <p>제목: <input type="text" name="title" required></p>
        <p>내용:</p>
        <textarea name="content" rows="10" cols="50" required></textarea>
        
        <br>
        <p>파일 첨부: <input type="file" name="upload_file[]" multiple></p>
        
        <button type="submit">작성 완료</button>
        <a href="index.php?board=<?php echo htmlspecialchars($board); ?>">[목록으로]</a>
    </form>
</body>
</html>