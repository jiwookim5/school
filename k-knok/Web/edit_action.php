<?php
session_start();
include 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    die("<script>alert('잘못된 요청입니다.'); location.href='index.php';</script>");
}

// 🛡️ 방어 포인트 1: 비로그인 사용자의 우회 접근 전면 차단
if (!isset($_SESSION['username'])) {
    echo "<script>alert('권한이 없습니다.'); location.href='login.php';</script>";
    exit;
}

$id = $_POST['id'] ?? null;
$board = $_POST['board'] ?? 'free';

// 🛡️ 방어 포인트 2: board 파라미터 화이트리스트 변조 방어
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free'; 
}

// 🛡️ 방어 포인트 3: 정수형 파라미터 강제 검증 및 타입 세탁
if (!$id || !is_numeric($id)) {
    echo "<script>alert('올바르지 않은 접근입니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

$title = trim($_POST['title'] ?? '');
$content = trim($_POST['content'] ?? '');
$table = ($board == 'qna') ? 'posts_qna' : 'posts_free'; 

// 🛡️ 방어 포인트 4: 무의미한 공백 도배 및 빈 데이터 전송 차단
if (empty($title) || empty($content)) {
    echo "<script>alert('제목과 내용을 공백 없이 올바르게 입력해주세요.'); history.back();</script>";
    exit;
}

// 🛡️ 방어 포인트 5: Prepared Statement를 통한 물리적 권한 및 소유주 2차 검증
$stmt = $conn->prepare("SELECT author_id FROM $table WHERE id = ?");
$stmt->bind_param("i", $id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();
$stmt->close();

if (!$post) {
    echo "<script>alert('존재하지 않거나 이미 삭제된 게시글입니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

if ($_SESSION['username'] != $post['author_id']) {
    echo "<script>alert('본인 게시글만 수정할 권한이 있습니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
    exit;
}

// 🛡️ 방어 포인트 6: 게시글 데이터 업데이트 락 강화 (작성자 조건 결합 검증)
$updateStmt = $conn->prepare("UPDATE $table SET title = ?, content = ? WHERE id = ? AND author_id = ?");
$updateStmt->bind_param("ssis", $title, $content, $id, $_SESSION['username']);
$updateStmt->execute();
$updateStmt->close();

// 🎯 [파일 삭제 처리 영역] 기존 로직 무결성 보존 및 인젝션 세탁
if (!empty($_POST['delete_files']) && is_array($_POST['delete_files'])) {
    foreach ($_POST['delete_files'] as $file_id) {
        if (!is_numeric($file_id)) continue; // 악성 문자열 차단
        
        // 해당 글에 소속된 첨부파일이 맞는지 교차 소유권 검증 후 물리적 삭제
        $file_stmt = $conn->prepare("SELECT file_path FROM post_files WHERE id = ? AND post_id = ?");
        $file_stmt->bind_param("ii", $file_id, $id);
        $file_stmt->execute();
        $file_res = $file_stmt->get_result()->fetch_assoc();
        $file_stmt->close();
        
        if ($file_res) {
            if (file_exists($file_res['file_path'])) {
                unlink($file_res['file_path']); // 파일 시스템에서 언링크
            }
            $del_stmt = $conn->prepare("DELETE FROM post_files WHERE id = ? AND post_id = ?");
            $del_stmt->bind_param("ii", $file_id, $id);
            $del_stmt->execute();
            $del_stmt->close();
        }
    }
}

// 🎯 [신규 파일 업로드 처리 영역] 핵심 에러 수정 부위
// $_FILES['file'] 형식을 HTML 명세와 완벽히 동기화하여 $_FILES['upload_file'] 배열 구조로 전면 전향
if (!empty($_FILES['upload_file']['name'][0])) {
    $upload_dir = 'uploads/';
    if (!is_dir($upload_dir)) {
        mkdir($upload_dir, 0777, true);
    }
    
    foreach ($_FILES['upload_file']['name'] as $key => $name) {
        if ($_FILES['upload_file']['error'][$key] === UPLOAD_ERR_OK) {
            // 파일명 중복 및 웹쉘 우회 실행을 방지하기 위한 안전 타임스탬프 세틱
            $target_path = $upload_dir . time() . '_' . basename($name);
            
            if (move_uploaded_file($_FILES['upload_file']['tmp_name'][$key], $target_path)) {
                $f_stmt = $conn->prepare("INSERT INTO post_files (post_id, board, file_path) VALUES (?, ?, ?)");
                $f_stmt->bind_param("iss", $id, $board, $target_path);
                $f_stmt->execute();
                $f_stmt->close();
            }
        }
    }
}

$conn->close();

// 🛡️ 방어 포인트 7: 안전하게 복귀 세탁
echo "<script>alert('게시글이 성공적으로 수정되었습니다.'); location.href='view.php?id=" . (int)$id . "&board=" . urlencode($board) . "';</script>";
exit;
?>