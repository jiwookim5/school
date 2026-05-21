<?php
include 'db.php';

$post_id = $_GET['id'];

$stmt = $conn->prepare("SELECT * FROM posts WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();

// 권한 체크 (남의 글을 주소창으로 강제 접근하는 것 차단)
if ($post['author_id'] != $_SESSION['user_id']) {
    echo "<script>alert('수정 권한이 없습니다.'); location.href='index.php';</script>";
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
    <form action="edit_action.php" method="POST">
        <input type="hidden" name="id" value="<?php echo $post['id']; ?>">
        <p>제목: <input type="text" name="title" value="<?php echo htmlspecialchars($post['title']); ?>" required style="width: 300px;"></p>
        <p>내용: <textarea name="content" rows="10" cols="50" required><?php echo htmlspecialchars($post['content']); ?></textarea></p>
        <button type="submit">수정 완료</button>
        <a href="view.php?id=<?php echo $post['id']; ?>">취소</a>
    </form>
</body>
</html>