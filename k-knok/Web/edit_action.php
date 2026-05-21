<?php
include 'db.php';

$post_id = $_POST['id'];
$title = $_POST['title'];
$content = $_POST['content'];

// 수정 전 다시 한번 본인 확인 (보안 가점)
$stmt = $conn->prepare("SELECT author_id FROM posts WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();

if ($post['author_id'] != $_SESSION['user_id']) {
    echo "<script>alert('권한이 없습니다.'); location.href='index.php';</script>";
    exit;
}

// DB 업데이트
$updateStmt = $conn->prepare("UPDATE posts SET title = ?, content = ? WHERE id = ?");
$updateStmt->bind_param("ssi", $title, $content, $post_id);

if ($updateStmt->execute()) {
    echo "<script>alert('글이 정상적으로 수정되었습니다!'); location.href='view.php?id=".$post_id."';</script>";
} else {
    echo "오류 발생: " . $conn->error;
}
?>