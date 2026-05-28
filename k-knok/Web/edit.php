<?php
session_start();
ini_set('display_errors', 1);
error_reporting(E_ALL);
include 'db.php';

$post_id = $_GET['id'] ?? null; // id 확인 방어코드 추가

if (!$post_id) {
    die("에러: 게시글 ID가 없습니다.");
}

$stmt = $conn->prepare("SELECT * FROM posts WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();

if (!$post) {
    die("에러: 게시글을 찾을 수 없습니다.");
}

// 권한 확인
if (!isset($_SESSION['username']) || $_SESSION['username'] != $post['author_id']) {
    echo "<script>alert('권한이 없습니다.'); location.href='index.php';</script>";
    exit;
}
?>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 수정</title>
</head>
<body>
    <h2>게시글 수정</h2>
    <form action="edit_action.php" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="id" value="<?php echo $post['id']; ?>">
        
        <p>제목: <input type="text" name="title" value="<?php echo htmlspecialchars($post['title']); ?>" required></p>
        <p>내용:</p>
        <textarea name="content" rows="10" cols="50" required><?php echo htmlspecialchars($post['content']); ?></textarea>
        
        <br>
        <button type="submit">수정 완료</button>
        <a href="view.php?id=<?php echo $post['id']; ?>">[취소]</a>
    </form>
</body>
</html>