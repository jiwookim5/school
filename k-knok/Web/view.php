<?php
include 'db.php';

$post_id = $_GET['id'];

// 글 가져오기
$stmt = $conn->prepare("SELECT p.*, u.username FROM posts p JOIN users u ON p.author_id = u.id WHERE p.id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$result = $stmt->get_result();
$post = $result->fetch_assoc();

if (!$post) {
    echo "<script>alert('존재하지 않는 게시글입니다.'); location.href='index.php';</script>";
    exit;
}
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title><?php echo htmlspecialchars($post['title']); ?></title>
</head>
<body>
    <h2>게시글 상세보기</h2>
    <hr>
    <p><strong>제목:</strong> <?php echo htmlspecialchars($post['title']); ?></p>
    <p><strong>작성자:</strong> <?php echo htmlspecialchars($post['username']); ?> | <strong>작성일:</strong> <?php echo $post['created_at']; ?></p>
    <hr>
    <div style="width: 600px; min-height: 200px; border: 1px solid #ccc; padding: 10px; white-space: pre-wrap;"><?php echo htmlspecialchars($post['content']); ?></div>
    <hr>
    
    <a href="index.php">[목록으로]</a>
    <?php 
    // 내가 쓴 글일 때만 수정/삭제 버튼 노출
    if ($_SESSION['user_id'] == $post['author_id']) {
        echo " | <a href='edit.php?id=".$post['id']."'>[수정하기]</a>";
        echo " | <a href='delete_action.php?id=".$post['id']."' onclick=\"return confirm('정말 이 게시글을 삭제하시겠습니까?');\">[삭제하기]</a>";
    }
    ?>

    <br><br>
    <h3>💬 댓글</h3>
    
    <form action="comment_action.php" method="POST">
        <input type="hidden" name="post_id" value="<?php echo $post['id']; ?>">
        <textarea name="content" rows="3" cols="60" placeholder="댓글을 입력해주세요." required></textarea>
        <br>
        <button type="submit">댓글 등록</button>
    </form>
    <br>

    <div style="width: 600px;">
        <?php
        // 댓글 테이블과 유저 테이블 조인
        $commentSql = "SELECT c.*, u.username FROM comments c JOIN users u ON c.author_id = u.id WHERE c.post_id = ? ORDER BY c.id ASC";
        $cStmt = $conn->prepare($commentSql);
        $cStmt->bind_param("i", $post_id);
        $cStmt->execute();
        $commentResult = $cStmt->get_result();

        if ($commentResult->num_rows > 0) {
            while($cRow = $commentResult->fetch_assoc()) {
                echo "<div style='border-bottom: 1px dashed #ccc; padding: 8px 0;'>";
                echo "<strong>" . htmlspecialchars($cRow['username']) . "</strong>: ";
                echo htmlspecialchars($cRow['content']);
                
                // 내가 쓴 댓글일 때만 [수정] 및 [삭제] 버튼 노출
        if ($_SESSION['user_id'] == $cRow['author_id']) {
            echo " <a href='comment_edit.php?id=" . $cRow['id'] . "&post_id=" . $post_id . "' style='font-size:11px; color:blue; text-decoration:none;'>[수정]</a>";
            // [★확인] 이 주소 줄이 정확해야 합니다!
            echo " <a href='comment_delete_action.php?id=" . $cRow['id'] . "&post_id=" . $post_id . "' onclick=\"return confirm('댓글을 삭제하시겠습니까?');\" style='font-size:11px; color:red; text-decoration:none;'>[삭제]</a>";
            }
                echo "</div>";
            }
        } else {
            echo "<p style='color:#888; font-size:13px;'>첫 댓글을 남겨보세요!</p>";
        }
        ?>
    </div>
    </body>
</html>