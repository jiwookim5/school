<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) {
    echo "<script>alert('로그인이 필요합니다.'); location.href='login.php';</script>";
    exit;
}

$post_id = $_GET['id'];
$board = $_GET['board'] ?? 'free'; // 게시판 정보 받기
$table = ($board == 'qna') ? 'posts_qna' : 'posts_free';

// 작성자 확인 쿼리
$stmt = $conn->prepare("SELECT author_id FROM $table WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();

if ($post['author_id'] != $_SESSION['username']) {
    echo "<script>alert('본인 글만 삭제 가능합니다.'); location.href='index.php?board=$board';</script>";
    exit;
}

// 삭제 쿼리
$deleteStmt = $conn->prepare("DELETE FROM $table WHERE id = ?");
$deleteStmt->bind_param("i", $post_id);

if ($deleteStmt->execute()) {
    echo "<script>alert('게시글이 삭제되었습니다.'); location.href='index.php?board=$board';</script>";
} else {
    echo "삭제 실패: " . $conn->error;
}
?>