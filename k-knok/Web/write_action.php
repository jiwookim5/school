<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) {
    die("<script>alert('로그인이 필요합니다.'); location.href='login.php';</script>");
}

$title = $_POST['title'];
$content = $_POST['content'];
$author = $_SESSION['username'];

// 1. 게시글 저장
$stmt = $conn->prepare("INSERT INTO posts (title, content, author_id) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $title, $content, $author);
$stmt->execute();
$post_id = $conn->insert_id;

// 2. 파일 업로드 처리 (여러 파일 지원)
if (!empty($_FILES['upload_file']['name'][0])) {
    $upload_dir = 'uploads/';
    if (!is_dir($upload_dir)) mkdir($upload_dir, 0777, true);

    foreach ($_FILES['upload_file']['name'] as $key => $name) {
        if ($_FILES['upload_file']['error'][$key] === UPLOAD_ERR_OK) {
            $filename = time() . '_' . basename($name);
            $target_path = $upload_dir . $filename;

            if (move_uploaded_file($_FILES['upload_file']['tmp_name'][$key], $target_path)) {
                // post_files 테이블에 경로 저장
                $f_stmt = $conn->prepare("INSERT INTO post_files (post_id, file_path) VALUES (?, ?)");
                $f_stmt->bind_param("is", $post_id, $target_path);
                $f_stmt->execute();
            }
        }
    }
}

echo "<script>alert('글 작성 완료!'); location.href='index.php';</script>";
?>