<?php
session_start();
include 'db.php';

// 🛡️ 방어 포인트 1: 비로그인 유저의 우회 접근 차단
if (!isset($_SESSION['username'])) { 
    echo "<script>alert('로그인이 필요한 기능입니다.'); location.href='login.php';</script>";
    exit;
}

// 🛡️ 방어 포인트 2: board 파라미터 화이트리스트 검증
$board = $_POST['board'] ?? 'free'; 
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free'; 
}

$post_id = $_POST['post_id'] ?? null;
// 🛡️ 방어 포인트 3: 정수형 파라미터 강제 검증
if (!$post_id || !is_numeric($post_id)) {
    echo "<script>alert('올바르지 않은 접근입니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

// 🎯 외래키 우회 해결의 핵심: MySQL 엔진 대신 PHP가 타깃 게시판에 진짜 글이 있는지 선제적 매핑 검증
$target_table = ($board == 'qna') ? 'posts_qna' : 'posts_free';

$chk_stmt = $conn->prepare("SELECT id FROM $target_table WHERE id = ?");
$chk_stmt->bind_param("i", $post_id);
$chk_stmt->execute();
$chk_res = $chk_stmt->get_result()->fetch_assoc();
$chk_stmt->close();

// 실제 글이 없다면 인서트를 거절하여 무결성 유지
if (!$chk_res) {
    echo "<script>alert('존재하지 않거나 삭제된 게시글에는 댓글을 달 수 없습니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

// 🛡️ 방어 포인트 4: trim()을 이용한 무의미한 공백 도배(DoS) 차단
$content = trim($_POST['content'] ?? '');
$author_id = $_SESSION['username']; 

if (empty($content)) {
    echo "<script>alert('댓글 내용을 올바르게 입력해주세요. (공백 불가)'); history.back();</script>";
    exit;
}

if (strlen($content) > 3000) { 
    echo "<script>alert('댓글이 너무 깁니다. (최대 1000자 이내)'); history.back();</script>";
    exit;
}

/* 🎯 단일 comments 테이블 통합 연동: 
   board 컬럼과 post_id를 동시에 엮어서 인서트함으로써 
   어떤 게시판의 몇 번 글인지 데이터 무결성을 완벽하게 확보합니다.
*/
$stmt = $conn->prepare("INSERT INTO comments (post_id, board, author_id, content) VALUES (?, ?, ?, ?)");
$stmt->bind_param("isss", $post_id, $board, $author_id, $content);

if ($stmt->execute()) {
    $stmt->close();
    $conn->close();
    header("Location: view.php?id=" . (int)$post_id . "&board=" . urlencode($board));
    exit;
} else {
    $stmt->close();
    $conn->close();
    // 🛡️ 방어 포인트 5: 에러 추상화 (해커에게 시스템 구조 노출 숨김)
    echo "<script>alert('댓글 등록 중 서버 내부 오류가 발생했습니다.'); history.back();</script>";
    exit;
}
?>