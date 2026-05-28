<?php
session_start();
include 'db.php';

// 세션에 로그인한 유저명이 없으면 차단
if (!isset($_SESSION['username'])) {
    echo "<script>alert('로그인이 필요합니다.'); location.href='login.php';</script>";
    exit;
}

$post_id = $_GET['id'];

// 작성자 이름을 가져와서 비교
$stmt = $conn->prepare("SELECT author_id FROM posts WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();

// 여기서 'user_id' 대신 'username'을 비교합니다!
if ($post['author_id'] != $_SESSION['username']) {
    echo "<script>alert('본인 글만 삭제 가능합니다.'); location.href='index.php';</script>";
    exit;
}

$deleteStmt = $conn->prepare("DELETE FROM posts WHERE id = ?");
$deleteStmt->bind_param("i", $post_id);

if ($deleteStmt->execute()) {
    echo "<script>alert('게시글이 삭제되었습니다.'); location.href='index.php';</script>";
} else {
    echo "삭제 실패: " . $conn->error;
}
?>