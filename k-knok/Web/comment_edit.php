<?php
include 'db.php';

if (!isset($_SESSION['user_id'])) {
    echo "<script>alert('권한이 없습니다.'); location.href='login.php';</script>";
    exit;
}

$comment_id = $_GET['id'];
$post_id = $_GET['post_id'];

// 수정할 댓글 데이터 가져오기
$stmt = $conn->prepare("SELECT * FROM comments WHERE id = ?");
$stmt->bind_param("i", $comment_id);
$stmt->execute();
$comment = $stmt->get_result()->fetch_assoc();

// 본인 검증
if ($comment['author_id'] != $_SESSION['user_id']) {
    echo "<script>alert('본인의 댓글만 수정할 수 있습니다.'); location.href='view.php?id=".$post_id."';</script>";
    exit;
}
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>댓글 수정</title>
</head>
<body>
    <h2>💬 댓글 수정하기</h2>
    <form action="comment_edit_action.php" method="POST">
        <input type="hidden" name="comment_id" value="<?php echo $comment_id; ?>">
        <input type="hidden" name="post_id" value="<?php echo $post_id; ?>">
        <p>
            <textarea name="content" rows="4" cols="50" required><?php echo htmlspecialchars($comment['content']); ?></textarea>
        </p>
        <button type="submit">수정 완료</button>
        <a href="view.php?id=<?php echo $post_id; ?>">취소</a>
    </form>
</body>
</html>