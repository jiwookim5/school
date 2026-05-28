<?php
session_start();
include 'db.php';

// 1. 로그인 확인
if (!isset($_SESSION['username'])) {
    // 세션이 없으면 현재 상태를 강제로 출력하고 종료
    die("에러: 세션이 만료되었거나 로그인 정보가 없습니다. (세션 데이터: " . print_r($_SESSION, true) . ")");
}

$id = $_POST['id'];
$title = $_POST['title'];
$content = $_POST['content'];

// 2. 게시글 작성자 권한 확인
$stmt = $conn->prepare("SELECT author_id FROM posts WHERE id = ?");
$stmt->bind_param("i", $id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();

if (!$post || $_SESSION['username'] != $post['author_id']) {
    echo "<script>alert('권한이 없습니다.'); location.href='index.php';</script>";
    exit;
}

// 3. 게시글 내용 업데이트
$stmt = $conn->prepare("UPDATE posts SET title=?, content=? WHERE id=?");
$stmt->bind_param("ssi", $title, $content, $id);
$stmt->execute();

// 4. 삭제 체크된 파일 처리
if (!empty($_POST['delete_files'])) {
    foreach ($_POST['delete_files'] as $file_id) {
        // 실제 파일 삭제를 위해 경로 조회
        $file_res = $conn->query("SELECT file_path FROM post_files WHERE id = " . (int)$file_id)->fetch_assoc();
        
        if ($file_res && file_exists($file_res['file_path'])) {
            unlink($file_res['file_path']); // 서버에서 파일 삭제
        }
        
        // DB에서 레코드 삭제
        $conn->query("DELETE FROM post_files WHERE id = " . (int)$file_id);
    }
}

// 5. 새 파일 추가 업로드 처리
if (!empty($_FILES['upload_file']['name'][0])) {
    $upload_dir = 'uploads/';
    if (!is_dir($upload_dir)) mkdir($upload_dir, 0777, true);

    foreach ($_FILES['upload_file']['name'] as $key => $name) {
        if ($_FILES['upload_file']['error'][$key] === UPLOAD_ERR_OK) {
            $filename = time() . '_' . basename($name);
            $target_path = $upload_dir . $filename;

            if (move_uploaded_file($_FILES['upload_file']['tmp_name'][$key], $target_path)) {
                // post_files 테이블에 새 경로 추가
                $f_stmt = $conn->prepare("INSERT INTO post_files (post_id, file_path) VALUES (?, ?)");
                $f_stmt->bind_param("is", $id, $target_path);
                $f_stmt->execute();
            }
        }
    }
}

echo "<script>alert('게시글이 수정되었습니다.'); location.href='view.php?id=$id';</script>";
?>