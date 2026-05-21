<?php
include 'db.php';

// 디버깅: 에러가 나면 무조건 화면에 출력하게 설정
error_reporting(E_ALL);
ini_set('display_errors', 1);

if (!isset($_SESSION['user_id'])) {
    die("로그인 정보가 없습니다.");
}

// 값이 제대로 들어오는지 확인
if (!isset($_GET['id']) || !isset($_GET['post_id'])) {
    die("삭제할 댓글 ID나 게시글 ID가 전달되지 않았습니다.");
}

$comment_id = $_GET['id'];
$post_id = $_GET['post_id'];

// 본인 검증
$stmt = $conn->prepare("SELECT author_id FROM comments WHERE id = ?");
$stmt->bind_param("i", $comment_id);
$stmt->execute();
$result = $stmt->get_result();
$comment = $result->fetch_assoc();

if (!$comment) {
    die("댓글을 찾을 수 없습니다.");
}

if ($comment['author_id'] != $_SESSION['user_id']) {
    die("본인 댓글만 삭제 가능합니다.");
}

// 삭제 실행
$deleteStmt = $conn->prepare("DELETE FROM comments WHERE id = ?");
$deleteStmt->bind_param("i", $comment_id);

if ($deleteStmt->execute()) {
    header("Location: view.php?id=" . $post_id);
    exit;
} else {
    echo "DB 에러: " . $conn->error;
}
?>