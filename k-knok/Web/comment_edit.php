<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) { // user_id 대신 username으로 통일
    die("로그인이 필요합니다.");
}

$comment_id = $_GET['id'];
$post_id = $_GET['post_id'];

$stmt = $conn->prepare("SELECT * FROM comments WHERE id = ?");
$stmt->bind_param("i", $comment_id);
$stmt->execute();
$comment = $stmt->get_result()->fetch_assoc();

if ($comment['author_id'] != $_SESSION['username']) {
    die("본인만 수정 가능합니다.");
}
?>
<form action="comment_edit_action.php" method="POST">
    <input type="hidden" name="comment_id" value="<?php echo $comment_id; ?>">
    <input type="hidden" name="post_id" value="<?php echo $post_id; ?>">
    <textarea name="content" required><?php echo htmlspecialchars($comment['content']); ?></textarea>
    <button type="submit">수정 완료</button>
</form>