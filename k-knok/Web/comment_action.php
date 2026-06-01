<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) { 
    echo "<script>alert('로그인이 필요한 기능입니다.'); location.href='login.php';</script>";
    exit;
}

$post_id = $_POST['post_id'];
$content = $_POST['content'];
$board = $_POST['board'] ?? 'free'; // 게시판 정보 받기
$author_id = $_SESSION['username']; 

$stmt = $conn->prepare("INSERT INTO comments (post_id, author_id, content) VALUES (?, ?, ?)");
$stmt->bind_param("iss", $post_id, $author_id, $content);

if ($stmt->execute()) {
    // 이동할 때 board 파라미터를 유지합니다
    header("Location: view.php?id=" . $post_id . "&board=" . $board);
} else {
    echo "댓글 등록 오류: " . $conn->error;
}
?>