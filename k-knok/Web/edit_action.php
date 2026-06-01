<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) {
    die("에러: 로그인 정보가 없습니다.");
}

$id = $_POST['id'];
$board = $_POST['board'] ?? 'free';
$title = $_POST['title'];
$content = $_POST['content'];
$table = ($board == 'qna') ? 'posts_qna' : 'posts_free'; // 테이블 선택

$stmt = $conn->prepare("SELECT author_id FROM $table WHERE id = ?");
$stmt->bind_param("i", $id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();

if (!$post || $_SESSION['username'] != $post['author_id']) {
    echo "<script>alert('권한이 없습니다.'); location.href='index.php?board=$board';</script>";
    exit;
}

// 업데이트
$stmt = $conn->prepare("UPDATE $table SET title=?, content=? WHERE id=?");
$stmt->bind_param("ssi", $title, $content, $id);
$stmt->execute();

// 파일 처리 (기존과 동일)
if (!empty($_POST['delete_files'])) {
    foreach ($_POST['delete_files'] as $file_id) {
        $file_res = $conn->query("SELECT file_path FROM post_files WHERE id = " . (int)$file_id)->fetch_assoc();
        if ($file_res && file_exists($file_res['file_path'])) unlink($file_res['file_path']);
        $conn->query("DELETE FROM post_files WHERE id = " . (int)$file_id);
    }
}
if (!empty($_FILES['upload_file']['name'][0])) {
    $upload_dir = 'uploads/';
    if (!is_dir($upload_dir)) mkdir($upload_dir, 0777, true);
    foreach ($_FILES['upload_file']['name'] as $key => $name) {
        if ($_FILES['upload_file']['error'][$key] === UPLOAD_ERR_OK) {
            $target_path = $upload_dir . time() . '_' . basename($name);
            if (move_uploaded_file($_FILES['upload_file']['tmp_name'][$key], $target_path)) {
                $f_stmt = $conn->prepare("INSERT INTO post_files (post_id, file_path) VALUES (?, ?)");
                $f_stmt->bind_param("is", $id, $target_path);
                $f_stmt->execute();
            }
        }
    }
}

echo "<script>alert('수정되었습니다.'); location.href='view.php?id=$id&board=$board';</script>";
?>