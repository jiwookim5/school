<?php
session_start();
include 'db.php';

// 1. 어떤 게시판인지 확인
$board = $_POST['board'] ?? 'free'; 
$table = ($board == 'qna') ? 'posts_qna' : 'posts_free';

if ($_SERVER['REQUEST_METHOD'] == 'POST' && empty($_POST) && $_SERVER['CONTENT_LENGTH'] > 0) {
    echo "<script>alert('업로드 용량 초과입니다.'); history.back();</script>";
    exit;
}

if (!isset($_SESSION['username'])) {
    die("<script>alert('로그인이 필요합니다.'); location.href='login.php';</script>");
}

$title = $_POST['title'];
$content = $_POST['content'];
$author = $_SESSION['username'];

// 2. 게시글 저장 (테이블 이름을 변수 $table로 변경)
$stmt = $conn->prepare("INSERT INTO $table (title, content, author_id) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $title, $content, $author);
$stmt->execute();
$post_id = $conn->insert_id;

// 3. 파일 업로드 처리 (여러 파일 지원)
if (!empty($_FILES['upload_file']['name'][0])) {
    $upload_dir = 'uploads/';
    if (!is_dir($upload_dir)) mkdir($upload_dir, 0777, true);

    foreach ($_FILES['upload_file']['name'] as $key => $name) {
        if ($_FILES['upload_file']['error'][$key] === UPLOAD_ERR_OK) {
            $filename = time() . '_' . basename($name);
            $target_path = $upload_dir . $filename;

            if (move_uploaded_file($_FILES['upload_file']['tmp_name'][$key], $target_path)) {
                $f_stmt = $conn->prepare("INSERT INTO post_files (post_id, file_path) VALUES (?, ?)");
                $f_stmt->bind_param("is", $post_id, $target_path);
                $f_stmt->execute();
            }
        }
    }
}

// 4. 완료 후 해당 게시판으로 리다이렉트
echo "<script>alert('글 작성 완료!'); location.href='index.php?board=$board';</script>";
?>