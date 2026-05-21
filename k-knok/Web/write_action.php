<?php
include 'db.php';

// 로그인 안 한 상태로 접근하면 쫓아내기
if (!isset($_SESSION['user_id'])) {
    echo "<script>alert('로그인이 필요한 서비스입니다.'); location.href='login.php';</script>";
    exit;
}

$title = $_POST['title'];
$content = $_POST['content'];
$author_id = $_SESSION['user_id']; // 로그인한 testuser의 ID(1)가 자동으로 들어감!

$stmt = $conn->prepare("INSERT INTO posts (title, content, author_id) VALUES (?, ?, ?)");
$stmt->bind_param("ssi", $title, $content, $author_id);

if ($stmt->execute()) {
    echo "<script>alert('글 작성이 완료되었습니다!'); location.href='index.php';</script>";
} else {
    echo "오류 발생: " . $conn->error;
}
?>