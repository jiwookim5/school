<?php
session_start();
include 'db.php';

// 🛡️ 방어 포인트 1: 비로그인 유저의 비인가 접근 즉시 차단
if (!isset($_SESSION['username'])) {
    echo "<script>alert('로그인이 필요합니다.'); location.href='login.php';</script>";
    exit;
}

// 🛡️ 방어 포인트 2: board 파라미터 화이트리스트 검증 (SQL 조작 방어)
$board = $_GET['board'] ?? 'free'; 
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free'; // 허용되지 않은 이상한 값은 무조건 free 테이블로 세탁 고정
}
$table = ($board == 'qna') ? 'posts_qna' : 'posts_free';

$post_id = $_GET['id'] ?? null;
// 🛡️ 방어 포인트 3: id 값의 정수형 타입 교차 체크 (비정상 입력값 필터링)
if (!$post_id || !is_numeric($post_id)) {
    echo "<script>alert('올바르지 않은 접근입니다.'); location.href='index.php?board=$board';</script>";
    exit;
}

// 작성자 확인 쿼리 (기존 로직 유지)
$stmt = $conn->prepare("SELECT author_id FROM $table WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();
$stmt->close(); // 자원 반환

// 🛡️ 방어 포인트 4: 존재하지 않는 글 예외 처리 및 작성자 매핑 검증
if (!$post) {
    echo "<script>alert('이미 삭제되었거나 존재하지 않는 게시글입니다.'); location.href='index.php?board=$board';</script>";
    exit;
}

if ($post['author_id'] != $_SESSION['username']) {
    echo "<script>alert('본인 글만 삭제 가능합니다.'); location.href='index.php?board=$board';</script>";
    exit;
}


/* 🛡️ 방어 포인트 5: 첨부파일 무단 누수 및 디스크 잔존 방어
   본문글만 지우고 넘어가면 서버 디스크(uploads/)에 고스란히 공격자 파일이 살아남아 
   서버 용량을 바닥내는 DoS 공격이나 숨겨진 악성 파일 다운로드 활로로 악용됩니다.
   글을 물리적으로 파괴하기 전, 매핑된 첨부파일을 찾아 디스크와 테이블에서 먼저 완전히 제거합니다.
*/
$file_stmt = $conn->prepare("SELECT id, file_path FROM post_files WHERE post_id = ?");
$file_stmt->bind_param("i", $post_id);
$file_stmt->execute();
$file_result = $file_stmt->get_result();

while ($file = $file_result->fetch_assoc()) {
    $physical_path = $file['file_path'];
    // 1. 서버 저장소에서 진짜 파일 물리 삭제
    if (!empty($physical_path) && file_exists($physical_path)) {
        unlink($physical_path);
    }
    // 2. post_files 테이블의 고유 매핑 로그 완전 삭제
    $del_f_stmt = $conn->prepare("DELETE FROM post_files WHERE id = ?");
    $del_f_stmt->bind_param("i", $file['id']);
    $del_f_stmt->execute();
    $del_f_stmt->close();
}
$file_stmt->close();


// 게시글 삭제 쿼리 (보안 잠금 강화)
// 🛡️ 방어 포인트 6: 조건문 자체에 author_id를 한 번 더 결합하여 물리적 소유권 2차 잠금
$deleteStmt = $conn->prepare("DELETE FROM $table WHERE id = ? AND author_id = ?");
$deleteStmt->bind_param("is", $post_id, $_SESSION['username']);

if ($deleteStmt->execute()) {
    echo "<script>alert('게시글이 삭제되었습니다.'); location.href='index.php?board=$board';</script>";
} else {
    // 🛡️ 방어 포인트 7: $conn->error 노출 금지 (시스템 구조 에러 힌트 유출 차단)
    echo "<script>alert('게시글 삭제 중 서버 내부 오류가 발생했습니다.'); history.back();</script>";
}
$deleteStmt->close();
$conn->close();
?>