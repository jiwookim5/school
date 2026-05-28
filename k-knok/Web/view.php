<?php
session_start();
include 'db.php';

$post_id = $_GET['id'] ?? null;

if (!$post_id) {
    die("에러: 게시글 ID가 전달되지 않았습니다.");
}

$stmt = $conn->prepare("SELECT * FROM posts WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$result = $stmt->get_result();
$post = $result->fetch_assoc();

if (!$post) {
    die("에러: 존재하지 않는 게시글입니다.");
}
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title><?php echo htmlspecialchars($post['title']); ?></title>
    <style>
        body { font-family: sans-serif; background-color: #f9f9f9; padding: 20px; }
        .post-container { max-width: 700px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; border: 1px solid #ddd; }
        .post-header { border-bottom: 2px solid #eee; margin-bottom: 20px; }
        .post-meta { color: #777; font-size: 0.9em; margin-bottom: 20px; }
        .post-content { min-height: 200px; line-height: 1.6; font-size: 1.1em; }
        .file-area { margin-top: 30px; padding: 15px; background: #f0f0f0; border-radius: 5px; }
        .btn-area { margin-top: 30px; border-top: 1px solid #eee; padding-top: 15px; }
        a { text-decoration: none; color: #007bff; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="post-container">
        <div class="post-header">
            <h2><?php echo htmlspecialchars($post['title']); ?></h2>
        </div>
        <div class="post-meta">
            작성자: <?php echo htmlspecialchars($post['author_id']); ?> | 
            작성일: <?php echo $post['created_at']; ?>
        </div>
        <div class="post-content">
            <?php echo nl2br(htmlspecialchars($post['content'])); ?>
        </div>

        <div class="file-area">
            <strong>📎 첨부파일:</strong><br>
            <?php
            $file_stmt = $conn->prepare("SELECT file_path FROM post_files WHERE post_id = ?");
            $file_stmt->bind_param("i", $post_id);
            $file_stmt->execute();
            $file_result = $file_stmt->get_result();

            if ($file_result->num_rows > 0) {
                while ($file = $file_result->fetch_assoc()) {
                    $file_name = basename($file['file_path']);
                    $display_name = preg_replace('/^\d+_/', '', $file_name);
                    echo "<a href='" . htmlspecialchars($file['file_path']) . "' download>" . htmlspecialchars($display_name) . "</a><br>";
                }
            } else {
                echo "첨부된 파일이 없습니다.";
            }
            ?>
        </div>

        <div class="btn-area">
            <a href="index.php">[목록으로]</a>
            <?php if (isset($_SESSION['username']) && $_SESSION['username'] == $post['author_id']) { ?>
                | <a href='edit.php?id=<?php echo $post['id']; ?>'>[수정]</a>
                | <a href='delete_action.php?id=<?php echo $post['id']; ?>' onclick="return confirm('삭제하시겠습니까?');">[삭제]</a>
            <?php } ?>
        </div>
    </div>
</body>
</html>