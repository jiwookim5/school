<?php
session_start();
include 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    die("<script>alert('잘못된 요청입니다.'); location.href='index.php';</script>");
}

// 🛡️ 방어 포인트 1: 비로그인 사용자의 우회 접근 차단
if (!isset($_SESSION['username'])) {
    echo "<script>alert('권한이 없습니다.'); location.href='login.php';</script>";
    exit;
}

// 🛡️ 방어 포인트 2: board 파라미터 화이트리스트 검증 (리다이렉션 및 변수 조작 방어)
$board = $_POST['board'] ?? 'free';
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free'; // 변조된 값이 들어오면 무조건 free로 강제 세탁
}

$comment_id = $_POST['comment_id'] ?? null;
$post_id = $_POST['post_id'] ?? null;

// 🛡️ 방어 포인트 3: 파라미터 정수형 타입 교차 검증 (비정상 주입 값 필터링)
if (!$comment_id || !is_numeric($comment_id) || !$post_id || !is_numeric($post_id)) {
    echo "<script>alert('올바르지 않은 접근입니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

// 🛡️ 방어 포인트 4: trim()을 이용한 무의미한 공백 도배 및 빈 댓글 수정 요청 차단
$content = trim($_POST['content'] ?? '');
$current_user = $_SESSION['username'];

if (empty($content)) {
    echo "<script>alert('댓글 내용을 올바르게 입력해주세요. (공백 불가)'); history.back();</script>";
    exit;
}

// 댓글 수정 길이 제한 (서버 및 DB 버퍼 오버플로우 차단)
if (strlen($content) > 3000) {
    echo "<script>alert('댓글 내용이 허용 길이를 초과했습니다.'); history.back();</script>";
    exit;
}

// 본인 확인 쿼리 (기존 로직 유지)
$stmt = $conn->prepare("SELECT author_id FROM comments WHERE id = ?");
$stmt->bind_param("i", $comment_id);
$stmt->execute();
$comment = $stmt->get_result()->fetch_assoc();
$stmt->close();

// 🛡️ 방어 포인트 5: 존재하지 않는 댓글 데이터 예외 처리 및 작성자 검증
if (!$comment) {
    echo "<script>alert('존재하지 않거나 이미 삭제된 댓글입니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

if ($comment['author_id'] != $current_user) {
    echo "<script>alert('권한이 없습니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

/* 🛡️ 방어 포인트 6: UPDATE 쿼리 물리적 조건 잠금 강화
   WHERE 절에 오직 comment_id만 매핑하는 것이 아니라 작성자($current_user) 조건까지 결합시켜
   만일의 논리 우회 공격이 들어오더라도 실제 DB 변조가 일어나지 않도록 방어합니다.
*/
$updateStmt = $conn->prepare("UPDATE comments SET content = ? WHERE id = ? AND author_id = ?");
$updateStmt->bind_param("sis", $content, $comment_id, $current_user);

if ($updateStmt->execute()) {
    $updateStmt->close();
    $conn->close();
    // 🛡️ 방어 포인트 7: 안전한 화이트리스트 변수를 결합하여 상세보기로 부드럽게 복귀
    echo "<script>alert('댓글이 수정되었습니다!'); location.href='view.php?id=" . (int)$post_id . "&board=" . urlencode($board) . "';</script>";
    exit;
} else {
    $updateStmt->close();
    $conn->close();
    // 🛡️ 방어 포인트 8: 시스템 에러($conn->error) 차단 (구조적 힌트 유출 완전 봉쇄)
    echo "<script>alert('댓글 수정 중 서버 내부 오류가 발생했습니다.'); history.back();</script>";
    exit;
}
?><?php
session_start();
include 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    die("<script>alert('잘못된 요청입니다.'); location.href='index.php';</script>");
}

// 🛡️ 방어 포인트 1: 비로그인 사용자의 우회 접근 차단
if (!isset($_SESSION['username'])) {
    echo "<script>alert('권한이 없습니다.'); location.href='login.php';</script>";
    exit;
}

// 🛡️ 방어 포인트 2: board 파라미터 화이트리스트 검증 (리다이렉션 및 변수 조작 방어)
$board = $_POST['board'] ?? 'free';
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free'; // 변조된 값이 들어오면 무조건 free로 강제 세탁
}

$comment_id = $_POST['comment_id'] ?? null;
$post_id = $_POST['post_id'] ?? null;

// 🛡️ 방어 포인트 3: 파라미터 정수형 타입 교차 검증 (비정상 주입 값 필터링)
if (!$comment_id || !is_numeric($comment_id) || !$post_id || !is_numeric($post_id)) {
    echo "<script>alert('올바르지 않은 접근입니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

// 🛡️ 방어 포인트 4: trim()을 이용한 무의미한 공백 도배 및 빈 댓글 수정 요청 차단
$content = trim($_POST['content'] ?? '');
$current_user = $_SESSION['username'];

if (empty($content)) {
    echo "<script>alert('댓글 내용을 올바르게 입력해주세요. (공백 불가)'); history.back();</script>";
    exit;
}

// 댓글 수정 길이 제한 (서버 및 DB 버퍼 오버플로우 차단)
if (strlen($content) > 3000) {
    echo "<script>alert('댓글 내용이 허용 길이를 초과했습니다.'); history.back();</script>";
    exit;
}

// 본인 확인 쿼리 (기존 로직 유지)
$stmt = $conn->prepare("SELECT author_id FROM comments WHERE id = ?");
$stmt->bind_param("i", $comment_id);
$stmt->execute();
$comment = $stmt->get_result()->fetch_assoc();
$stmt->close();

// 🛡️ 방어 포인트 5: 존재하지 않는 댓글 데이터 예외 처리 및 작성자 검증
if (!$comment) {
    echo "<script>alert('존재하지 않거나 이미 삭제된 댓글입니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

if ($comment['author_id'] != $current_user) {
    echo "<script>alert('권한이 없습니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

/* 🛡️ 방어 포인트 6: UPDATE 쿼리 물리적 조건 잠금 강화
   WHERE 절에 오직 comment_id만 매핑하는 것이 아니라 작성자($current_user) 조건까지 결합시켜
   만일의 논리 우회 공격이 들어오더라도 실제 DB 변조가 일어나지 않도록 방어합니다.
*/
$updateStmt = $conn->prepare("UPDATE comments SET content = ? WHERE id = ? AND author_id = ?");
$updateStmt->bind_param("sis", $content, $comment_id, $current_user);

if ($updateStmt->execute()) {
    $updateStmt->close();
    $conn->close();
    // 🛡️ 방어 포인트 7: 안전한 화이트리스트 변수를 결합하여 상세보기로 부드럽게 복귀
    echo "<script>alert('댓글이 수정되었습니다!'); location.href='view.php?id=" . (int)$post_id . "&board=" . urlencode($board) . "';</script>";
    exit;
} else {
    $updateStmt->close();
    $conn->close();
    // 🛡️ 방어 포인트 8: 시스템 에러($conn->error) 차단 (구조적 힌트 유출 완전 봉쇄)
    echo "<script>alert('댓글 수정 중 서버 내부 오류가 발생했습니다.'); history.back();</script>";
    exit;
}
?>