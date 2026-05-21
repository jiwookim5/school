<?php
include 'db.php';

if (!isset($_SESSION['user_id'])) {
    echo "<script>alert('로그인이 필요한 기능입니다.'); location.href='login.php';</script>";
    exit;
}

$post_id = $_POST['post_id'];
$content = $_POST['content'];
$author_id = $_SESSION['user_id'];

// comments 테이블에 데이터 삽입
$stmt = $conn->prepare("INSERT INTO comments (post_id, author_id, content) VALUES (?, ?, ?)");
$stmt->bind_param("iis", $post_id, $author_id, $content);

if ($stmt->execute()) {
    // 댓글 쓰기가 끝나면 보던 상세 페이지로 바로 새로고침하듯 복귀!
    header("Location: view.php?id=" . $post_id);
} else {
    echo "댓글 등록 오류: " . $conn->error;
}
?> 