<?php
session_start();
include 'db.php';

// 1. 어떤 게시판인지 확인 및 화이트리스트 검증 (파라미터 변조 방어)
$board = $_POST['board'] ?? 'free'; 
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free'; // 허용되지 않은 변조 값이 들어오면 free로 강제 고정
}
$table = ($board == 'qna') ? 'posts_qna' : 'posts_free';

// 포스트 전송 용량 초과 체크 (기존 기능 유지)
if ($_SERVER['REQUEST_METHOD'] == 'POST' && empty($_POST) && $_SERVER['CONTENT_LENGTH'] > 0) {
    echo "<script>alert('업로드 용량 초과입니다.'); history.back();</script>";
    exit;
}

// 세션 로그인 검증 (일관된 스크립트 흐름 처리)
if (!isset($_SESSION['username'])) {
    echo "<script>alert('로그인이 필요합니다.'); location.href='login.php';</script>";
    exit;
}

// trim()을 추가하여 무의미한 공백 글로 도배하는 공격 방지
$title = trim($_POST['title'] ?? '');
$content = trim($_POST['content'] ?? '');
$author = $_SESSION['username'];

// 가이드라인 위반 1: 필수값 누락 또는 공백 도배 검증
if (empty($title) || empty($content)) {
    echo "<script>alert('[작성 오류] 제목과 내용을 올바르게 입력해주세요. (공백 불가)'); history.back();</script>";
    exit;
}

// 가이드라인 위반 2: 제목 내 주요 인젝션 기호 감지
if (preg_match("/['\";#]/", $title)) {
    echo "<script>alert('[보안 위반] 제목에 허용되지 않는 특수문자(\' \" ; #)가 포함되어 있습니다.'); history.back();</script>";
    exit;
}

// 가이드라인 위반 3: 제목 길이 검증 (서버/DB 오버플로우 방어)
if (strlen($title) > 300) {
    echo "<script>alert('[작성 오류] 제목이 너무 깁니다. (최대 100자 이내)'); history.back();</script>";
    exit;
}

// 2. 게시글 저장 (Prepared Statement 기존 로직 안전하게 유지)
$stmt = $conn->prepare("INSERT INTO $table (title, content, author_id) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $title, $content, $author);

if (!$stmt->execute()) {
    echo "<script>alert('게시글 저장 중 오류가 발생했습니다.'); history.back();</script>";
    exit;
}
$post_id = $conn->insert_id;
$stmt->close();


// 3. 파일 업로드 처리 (가용성 100% 모드: 모든 확장자 무제한 허용 ⭐⭐⭐)
if (!empty($_FILES['upload_file']['name'][0])) {
    $upload_dir = 'uploads/';
    
    if (!is_dir($upload_dir)) {
        mkdir($upload_dir, 0755, true);
    }

    foreach ($_FILES['upload_file']['name'] as $key => $name) {
        if ($_FILES['upload_file']['error'][$key] === UPLOAD_ERR_OK) {
            
            $tmp_name = $_FILES['upload_file']['tmp_name'][$key];
            
            // 이중 확장자 조작을 무력화하기 위해 맨 뒤 진짜 확장자 추출 후 소문자 통일
            $file_ext = strtolower(pathinfo($name, PATHINFO_EXTENSION));

            // 시스템 구조상 확장자가 아예 유추되지 않는 파일만 최소한으로 차단
            if (empty($file_ext)) {
                echo "<script>alert('[보안 위반] 확장자가 유출되거나 없는 파일은 안전을 위해 업로드할 수 없습니다.'); history.back();</script>";
                exit;
            }

            // 🛡️ 핵심 방어: 난수화 결합 리네이밍
            // 모든 확장자가 저장되도록 허용하되, 해커가 원래 업로드한 파일 이름을 경로로 때려 맞추는 것을 방지
            $filename = time() . '_' . bin2hex(random_bytes(8)) . '.' . $file_ext;
            $target_path = $upload_dir . $filename;

            // 파일을 안전한 난수명으로 이동 저장
            if (move_uploaded_file($tmp_name, $target_path)) {
                $f_stmt = $conn->prepare("INSERT INTO post_files (post_id, file_path) VALUES (?, ?)");
                $f_stmt->bind_param("is", $post_id, $target_path);
                $f_stmt->execute();
                $f_stmt->close();
            }
        }
    }
}

$conn->close();

// 4. 완료 후 해당 게시판으로 리다이렉트
echo "<script>alert('글 작성 완료!'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>";
?>