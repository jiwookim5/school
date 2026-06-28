<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) { 
    die("로그인이 필요합니다.");
}

$comment_id = $_GET['id'];
$post_id = $_GET['post_id'];

$stmt = $conn->prepare("SELECT * FROM comments WHERE id = ?");
$stmt->bind_param("i", $comment_id);
$stmt->execute();
$comment = $stmt->get_result()->fetch_assoc();

if ($comment['author_id'] != $_SESSION['username']) {
    die("본인만 수정 가능합니다.");
}
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>K.knock Security Forum - 댓글 수정</title>
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            padding: 40px 20px;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 80vh;
        }
        .edit-container {
            background-color: #ffffff;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 600px;
        }
        h2 {
            margin-top: 0;
            color: #333;
            font-size: 22px;
            margin-bottom: 20px;
            border-bottom: 2px solid #ddd;
            padding-bottom: 10px;
            text-align: left;
        }
        
        /* 타 파일(write, edit)과 일관성을 유지하는 통합 보안 가이드 배너 */
        .policy-banner-box {
            background-color: #f8f9fa;
            border-left: 4px solid #007bff; 
            border-top: 1px solid #dee2e6;
            border-right: 1px solid #dee2e6;
            border-bottom: 1px solid #dee2e6;
            border-radius: 4px;
            padding: 16px;
            margin-bottom: 25px;
            text-align: left;
        }
        .policy-banner-box .policy-main-title {
            color: #007bff;
            font-size: 14px;
            font-weight: 700;
            margin-bottom: 6px;
        }
        .policy-banner-box ul {
            margin: 0;
            padding-left: 16px;
            color: #495057;
            font-size: 12.5px;
            line-height: 1.6;
        }

        .form-group {
            margin-bottom: 20px;
            text-align: left;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-size: 14px;
            font-weight: 500;
        }
        .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 15px;
            outline: none;
            resize: vertical;
        }
        .form-group textarea:focus {
            border-color: #007bff;
            box-shadow: 0 0 5px rgba(0, 123, 255, 0.1);
        }
        
        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 25px;
        }
        .btn-submit {
            flex: 1;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 15px;
            font-weight: 500;
            cursor: pointer;
            transition: background-color 0.2s;
        }
        .btn-submit:hover { background-color: #0056b3; }
        .btn-cancel {
            padding: 12px 20px;
            background-color: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-size: 15px;
            text-align: center;
            transition: background-color 0.2s;
        }
        .btn-cancel:hover { background-color: #5a6268; }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="edit-container">
        <h2>💬 댓글 수정</h2>
        
        <div class="policy-banner-box">
            <div class="policy-main-title">ℹ️ 댓글 수정 보안 안내</div>
            <ul>
                <li>공백 도배글 방지를 위해 공백이나 엔터로만 이루어진 내용의 수정은 제한됩니다.</li>
                <li>타인의 세션을 탈취하려는 악성 스크립트 코드 주입 시 즉시 계정이 제재될 수 있습니다.</li>
            </ul>
        </div>
        
        <form action="comment_edit_action.php" method="POST" onsubmit="return validateCommentForm()">
            <input type="hidden" name="comment_id" value="<?php echo $comment_id; ?>">
            <input type="hidden" name="post_id" value="<?php echo $post_id; ?>">
            <input type="hidden" name="board" value="<?php echo htmlspecialchars($_GET['board'] ?? 'free'); ?>">
            
            <div class="form-group">
                <label for="content">댓글 내용 수정</label>
                <textarea id="content" name="content" rows="6" required><?php echo htmlspecialchars($comment['content']); ?></textarea>
            </div>
            
            <div class="btn-group">
                <a href="view.php?id=<?php echo htmlspecialchars($post_id); ?>&board=<?php echo htmlspecialchars($_GET['board'] ?? 'free'); ?>" class="btn-cancel">취소</a>
                <button type="submit" class="btn-submit">수정 완료</button>
            </div>
        </form>
    </div>

    <script>
        // 프론트엔드 공백 검증 스크립트 결합
        function validateCommentForm() {
            const content = document.getElementById('content').value;
            if (content.trim() === "") {
                alert("[수정 거절] 댓글 내용을 입력해 주세요. (공백 불가)");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>