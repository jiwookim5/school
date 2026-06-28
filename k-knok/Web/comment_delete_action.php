<?php
session_start();
include 'db.php';

// 🛡️ 방어 포인트 1: 정보 유출 방지를 위한 에러 비활성화
error_reporting(0);
ini_set('display_errors', 0);

if (!isset($_SESSION['username'])) {
    echo "<script>alert('로그인 정보가 없습니다.'); location.href='login.php';</script>";
    exit;
}

$board = $_GET['board'] ?? 'free';
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free';
}

if (!isset($_GET['id']) || !isset($_GET['post_id'])) {
    echo "<script>alert('필수 인자가 누락되었습니다.'); history.back();</script>";
    exit;
}

$comment_id = $_GET['id'];
$post_id = $_GET['post_id'];

if (!is_numeric($comment_id) || !is_numeric($post_id)) {
    echo "<script>alert('올바르지 않은 파라미터 형식입니다.'); history.back();</script>";
    exit;
}

// 🎯 단일 comments 테이블을 조회하여 작성자 매핑 확인
$stmt = $conn->prepare("SELECT author_id FROM comments WHERE id = ?");
$stmt->bind_param("i", $comment_id);
$stmt->execute();
$result = $stmt->get_result();
$comment = $result->fetch_assoc();
$stmt->close();

if (!$comment) {
    echo "<script>alert('댓글을 찾을 수 없습니다.'); history.back();</script>";
    exit;
}

if ($comment['author_id'] != $_SESSION['username']) {
    echo "<script>alert('본인 댓글만 삭제 가능합니다.'); history.back();</script>";
    exit;
}

// 🎯 단일 comments 테이블 소유권 이중 잠금 격리 삭제
$deleteStmt = $conn->prepare("DELETE FROM comments WHERE id = ? AND author_id = ?");
$deleteStmt->bind_param("is", $comment_id, $_SESSION['username']);

if ($deleteStmt->execute()) {
    $deleteStmt->close();
    $conn->close();
    echo "<script>alert('댓글이 삭제되었습니다.'); location.href='view.php?id=" . (int)$post_id . "&board=" . urlencode($board) . "';</script>";
    exit;
} else {
    $deleteStmt->close();
    $conn->close();
    echo "<script>alert('댓글 삭제 처리 중 서버 내부 오류가 발생했습니다.'); history.back();</script>";
    exit;
}
?>