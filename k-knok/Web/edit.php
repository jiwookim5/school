<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) {
    header("Location: login.php");
    exit;
}

$board = $_GET['board'] ?? 'free'; // 게시판 정보 받기
$post_id = $_GET['id'] ?? null;
$table = ($board == 'qna') ? 'posts_qna' : 'posts_free';

$stmt = $conn->prepare("SELECT * FROM $table WHERE id = ?"); // $table 사용
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();

if (!$post || $_SESSION['username'] != $post['author_id']) {
    echo "<script>alert('권한이 없거나 없는 게시글입니다.'); location.href='index.php?board=$board';</script>";
    exit;
}
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 수정</title>
</head>
<body>
    <h2>게시글 수정</h2>
    <form action="edit_action.php" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="id" value="<?php echo $post['id']; ?>">
        <input type="hidden" name="board" value="<?php echo htmlspecialchars($board); ?>"> <p>제목: <input type="text" name="title" value="<?php echo htmlspecialchars($post['title']); ?>" required></p>
        <p>내용:</p>
        <textarea name="content" rows="10" cols="50" required><?php echo htmlspecialchars($post['content']); ?></textarea>
        
        <div style="margin: 20px 0; padding: 10px; border: 1px solid #ccc; width: 400px;">
            <p><strong>첨부파일 관리:</strong></p>
            <?php
            $file_stmt = $conn->prepare("SELECT id, file_path FROM post_files WHERE post_id = ?");
            $file_stmt->bind_param("i", $post_id);
            $file_stmt->execute();
            $file_result = $file_stmt->get_result();
            while ($file = $file_result->fetch_assoc()) {
                echo "<input type='checkbox' name='delete_files[]' value='" . $file['id'] . "'> " . htmlspecialchars(basename($file['file_path'])) . "<br>";
            }
            ?>
            <br>
            <p>새 파일 추가: <input type="file" name="upload_file[]" multiple></p>
        </div>
        
        <button type="submit">수정 완료</button>
        <a href="view.php?id=<?php echo $post['id']; ?>&board=<?php echo htmlspecialchars($board); ?>">[취소]</a>
    </form>
</body>
</html>