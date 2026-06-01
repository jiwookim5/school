<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) {
    echo "<script>alert('권한이 없습니다.'); location.href='login.php';</script>";
    exit;
}

$comment_id = $_POST['comment_id'];
$post_id = $_POST['post_id'];
$content = $_POST['content'];
$board = $_POST['board'] ?? 'free';

// 다시 한번 본인 확인 (우수 보안)
$stmt = $conn->prepare("SELECT author_id FROM comments WHERE id = ?");
$stmt->bind_param("i", $comment_id);
$stmt->execute();
$comment = $stmt->get_result()->fetch_assoc();

if ($comment['author_id'] != $_SESSION['username']) {
    echo "<script>alert('권한이 없습니다.'); location.href='index.php';</script>";
    exit;
}

// 댓글 수정 실행
$updateStmt = $conn->prepare("UPDATE comments SET content = ? WHERE id = ?");
$updateStmt->bind_param("si", $content, $comment_id);

if ($updateStmt->execute()) {
    echo "<script>alert('댓글이 수정되었습니다!'); location.href='view.php?id=".$post_id."';</script>";
} else {
    echo "댓글 수정 오류: " . $conn->error;
}
?>