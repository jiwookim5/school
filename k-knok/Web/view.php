<?php
session_start();
include 'db.php';

// 🛡️ 방어 포인트 1: board 파라미터 화이트리스트 검증
$board = $_GET['board'] ?? 'free';
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free'; 
}

$post_id = $_GET['id'] ?? null;

// 🛡️ 방어 포인트 2: 정수형 파라미터 강제 검증
if (!$post_id || !is_numeric($post_id)) {
    die("<script>alert('올바르지 않은 접근입니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>");
}

// 2. 게시판 종류에 따른 테이블 명 설정
$table = ($board == 'qna') ? 'posts_qna' : 'posts_free';

// 3. Prepared Statement를 이용한 게시글 안전 조회
$stmt = $conn->prepare("SELECT * FROM $table WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();
$stmt->close();

// 🛡️ 방어 포인트 3: 존재하지 않는 글에 대한 에러 추상화
if (!$post) {
    die("<script>alert('존재하지 않거나 삭제된 게시글입니다.'); location.href='index.php?board=" . htmlspecialchars($board) . "';</script>");
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
        .post-content { min-height: 150px; line-height: 1.6; }
        .file-area { margin-top: 20px; padding: 10px; background: #f0f0f0; }
        .comment-area { margin-top: 30px; border-top: 2px solid #eee; padding-top: 20px; }
        .comment { 
            border-bottom: 1px solid #eee; 
            padding: 10px 0; 
            white-space: pre-wrap; 
        }
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
                $file_param = basename($f['file_path']);
                echo "<a href='download.php?file=" . htmlspecialchars($file_param) . "'>" . htmlspecialchars($name) . "</a><br>";
            }
            $f_stmt->close();
            ?>
        </div>

        <div class="comment-area">
            <h3>댓글</h3>
            <?php
            /* 🎯 고유 게시판 조회 연동: 
               단일 테이블에서 현재 게시글 ID와 현재 게시판 종류(free/qna)가 
               모두 완벽히 일치하는 해당 영역의 댓글만 정확하게 바인딩하여 긁어옵니다.
            */
            $c_stmt = $conn->prepare("SELECT * FROM comments WHERE post_id = ? AND board = ? ORDER BY created_at ASC");
            $c_stmt->bind_param("is", $post_id, $board);
            $c_stmt->execute();
            $comments = $c_stmt->get_result();
            while ($c = $comments->fetch_assoc()) {
                echo "<div class='comment'>";
                echo "<strong>" . htmlspecialchars($c['author_id']) . "</strong>: " . htmlspecialchars($c['content']);
                
                /* 🛡️ 방어 포인트 4: 댓글 링크 변수 보호 및 board 인자 유지 */
                if (isset($_SESSION['username']) && $_SESSION['username'] == $c['author_id']) {
                    echo " <small><a href='comment_edit.php?id=" . htmlspecialchars($c['id']) . "&post_id=" . htmlspecialchars($post_id) . "&board=" . htmlspecialchars($board) . "'>[수정]</a>";
                    echo " <a href='comment_delete_action.php?id=" . htmlspecialchars($c['id']) . "&post_id=" . htmlspecialchars($post_id) . "&board=" . htmlspecialchars($board) . "' onclick='return confirm(\"삭제하시겠습니까?\");'>[삭제]</a></small>";
                }
                echo "</div>";
            }
            $c_stmt->close();
            ?>
            
            <form action="comment_action.php" method="POST">
                <input type="hidden" name="post_id" value="<?php echo htmlspecialchars($post_id); ?>">
                <input type="hidden" name="board" value="<?php echo htmlspecialchars($board); ?>">
                <textarea name="content" required placeholder="댓글을 입력하세요"></textarea><br>
                <button type="submit">댓글 작성</button>
            </form>
        </div>

        <div style="margin-top:20px;">
            <a href="index.php?board=<?php echo htmlspecialchars($board); ?>">[목록으로]</a>
            
            <?php if (isset($_SESSION['username']) && $_SESSION['username'] == $post['author_id']) { ?>
                | <a href='edit.php?id=<?php echo htmlspecialchars($post['id']); ?>&board=<?php echo htmlspecialchars($board); ?>'>[수정]</a>
                | <a href='delete_action.php?id=<?php echo htmlspecialchars($post['id']); ?>&board=<?php echo htmlspecialchars($board); ?>' onclick="return confirm('정말 삭제하시겠습니까?');">[삭제]</a>
            <?php } ?>
        </div>
    </div>
</body>
</html>