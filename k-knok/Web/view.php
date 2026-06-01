<?php
session_start();
include 'db.php';

$post_id = $_GET['id'] ?? null;
if (!$post_id) { die("에러: 게시글 ID가 전달되지 않았습니다."); }

$stmt = $conn->prepare("SELECT * FROM posts WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();

if (!$post) { die("에러: 존재하지 않는 게시글입니다."); }
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title><?php echo htmlspecialchars($post['title']); ?></title>
    <style>
        body { font-family: sans-serif; background-color: #f9f9f9; padding: 20px; }
        .post-container { max-width: 700px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; border: 1px solid #ddd; }
        .post-content { min-height: 150px; line-height: 1.6; }
        .file-area { margin-top: 20px; padding: 10px; background: #f0f0f0; }
        .comment-area { margin-top: 30px; border-top: 2px solid #eee; padding-top: 20px; }
        .comment { border-bottom: 1px solid #eee; padding: 10px 0; }
        textarea { width: 100%; height: 60px; margin-top: 10px; }
    </style>
</head>
<body>
    <div class="post-container">
        <h2><?php echo htmlspecialchars($post['title']); ?></h2>
        <p>작성자: <?php echo htmlspecialchars($post['author_id']); ?> | 작성일: <?php echo $post['created_at']; ?></p>
        <div class="post-content"><?php echo nl2br(htmlspecialchars($post['content'])); ?></div>

        <div class="file-area">
            <strong>📎 첨부파일:</strong><br>
            <?php
            $f_stmt = $conn->prepare("SELECT file_path FROM post_files WHERE post_id = ?");
            $f_stmt->bind_param("i", $post_id);
            $f_stmt->execute();
            $res = $f_stmt->get_result();
            while ($f = $res->fetch_assoc()) {
                $name = preg_replace('/^\d+_/', '', basename($f['file_path']));
                echo "<a href='{$f['file_path']}' download>" . htmlspecialchars($name) . "</a><br>";
            }
            ?>
        </div>

        <div class="comment-area">
            <h3>댓글</h3>
            <?php
            $c_stmt = $conn->prepare("SELECT * FROM comments WHERE post_id = ? ORDER BY created_at ASC");
            $c_stmt->bind_param("i", $post_id);
            $c_stmt->execute();
            $comments = $c_stmt->get_result();
            while ($c = $comments->fetch_assoc()) {
                echo "<div class='comment'>";
                echo "<strong>" . htmlspecialchars($c['author_id']) . "</strong>: " . htmlspecialchars($c['content']);
                echo nl2br(htmlspecialchars($c['content']));
                // 댓글 작성자 본인일 경우에만 수정/삭제 버튼 표시
                if (isset($_SESSION['username']) && $_SESSION['username'] == $c['author_id']) {
                    echo " <small><a href='comment_edit.php?id=" . $c['id'] . "&post_id=" . $post_id . "'>[수정]</a>";
                    echo " <a href='comment_delete_action.php?id=" . $c['id'] . "&post_id=" . $post_id . "' onclick='return confirm(\"삭제하시겠습니까?\");'>[삭제]</a></small>";
    }
    echo "</div>";
}
            ?>
            
            <form action="comment_action.php" method="POST">
                <input type="hidden" name="post_id" value="<?php echo $post_id; ?>">
                <textarea name="content" required placeholder="댓글을 입력하세요"></textarea><br>
                <button type="submit">댓글 작성</button>
            </form>
        </div>

        <div style="margin-top:20px;">
            <a href="index.php">[목록으로]</a>
            <?php if (isset($_SESSION['username']) && $_SESSION['username'] == $post['author_id']) { ?>
                | <a href='edit.php?id=<?php echo $post['id']; ?>'>[수정]</a>
            <?php } ?>
        </div>
    </div>
</body>
</html>