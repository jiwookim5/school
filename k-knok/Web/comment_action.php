<?php
session_start();
include 'db.php';

// 여기서 'user_id'를 'username'으로 변경했습니다!
if (!isset($_SESSION['username'])) { 
    echo "<script>alert('로그인이 필요한 기능입니다.'); location.href='login.php';</script>";
    exit;
}

$post_id = $_POST['post_id'];
$content = $_POST['content'];
// 세션에서 username 값을 가져오도록 변경
$author_id = $_SESSION['username']; 

// comments 테이블에 데이터 삽입
$stmt = $conn->prepare("INSERT INTO comments (post_id, author_id, content) VALUES (?, ?, ?)");
$stmt->bind_param("iss", $post_id, $author_id, $content); // "iss"로 변경 (author_id가 문자열이므로)

if ($stmt->execute()) {
    header("Location: view.php?id=" . $post_id);
} else {
    echo "댓글 등록 오류: " . $conn->error;
}
?>