<?php
include 'db.php';

$post_id = $_GET['id'];

// 1. 본인 확인 (이전 로직 활용)
$stmt = $conn->prepare("SELECT author_id FROM posts WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();

if ($post['author_id'] != $_SESSION['user_id']) {
    echo "<script>alert('본인 글만 삭제 가능합니다.'); location.href='index.php';</script>";
    exit;
}

// 2. 게시글 삭제 (DB에서 CASCADE 설정이 되어있으므로 댓글은 자동 삭제됨)
$deleteStmt = $conn->prepare("DELETE FROM posts WHERE id = ?");
$deleteStmt->bind_param("i", $post_id);

if ($deleteStmt->execute()) {
    echo "<script>alert('게시글이 삭제되었습니다.'); location.href='index.php';</script>";
} else {
    echo "삭제 실패: " . $conn->error;
}
?>