<?php
session_start();
include 'db.php';

// 🛡️ 방어 포인트 1: 비로그인 사용자의 우회 접근 전면 차단
if (!isset($_SESSION['username'])) {
    header("Location: login.php");
    exit;
}

// 🛡️ 방어 포인트 2: board 파라미터 변조 방어 (화이트리스트 검증)
// 해커가 ?board=posts_free;-- 같은 값을 주입하여 SQL 구문을 파괴하는 행위를 차단합니다.
$board = $_GET['board'] ?? 'free'; 
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free'; // 허용되지 않은 변조 값이 들어오면 free로 강제 고정
}

$post_id = $_GET['id'] ?? null;
// 정수형 파라미터 검증 (숫자가 아닌 비정상 입력 값 필터링)
if (!$post_id || !is_numeric($post_id)) {
    echo "<script>alert('올바르지 않은 접근입니다.'); location.href='index.php?board=$board';</script>";
    exit;
}

$table = ($board == 'qna') ? 'posts_qna' : 'posts_free';

// 3. Prepared Statement를 통한 게시글 안전 조회
$stmt = $conn->prepare("SELECT * FROM $table WHERE id = ?");
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post = $stmt->get_result()->fetch_assoc();
$stmt->close();

/* 🛡️ 방어 포인트 3: 타인 게시글 내용 수집 및 양식 탈취 공격 방어
   해커가 글 번호(?id=상대방글번호)를 임의로 대입하여 수정 창을 띄울 때,
   타인의 글 제목과 본문 내용을 몰래 훔쳐보거나(BOLA 취약점) 양식을 가로채 변조하는 것을 
   현재 로그인한 세션 유저명과 작성자 ID 대조로 물리적인 진입 자체를 완전히 불사릅니다.
*/
if (!$post || $_SESSION['username'] != $post['author_id']) {
    echo "<script>alert('권한이 없거나 없는 게시글입니다.'); location.href='index.php?board=$board';</script>";
    exit;
}
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>K.knock Security Forum - 게시글 수정</title>
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            padding: 40px 20px;
            display: flex;
            justify-content: center;
        }
        .edit-container {
            background-color: #ffffff;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 680px;
        }
        h2 {
            margin-top: 0;
            color: #333;
            font-size: 22px;
            margin-bottom: 20px;
            border-bottom: 2px solid #ddd;
            padding-bottom: 10px;
        }
        
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
            font-size: 14.5px;
            font-weight: 700;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .policy-grid {
            display: table;
            width: 100%;
        }
        .policy-section {
            display: table-cell;
            width: 50%;
            vertical-align: top;
            padding-right: 10px;
        }
        .policy-section:last-child {
            padding-right: 0;
            border-left: 1px dashed #dee2e6;
            padding-left: 15px;
        }
        .policy-section-title {
            font-size: 13px;
            font-weight: 700;
            color: #495057;
            margin-bottom: 6px;
        }
        .policy-banner-box ul {
            margin: 0;
            padding-left: 16px;
            color: #495057;
            font-size: 12.5px;
            line-height: 1.6;
        }
        .policy-banner-box code {
            color: #495057;
            font-weight: bold;
            background: #fff;
            padding: 1px 4px;
            border: 1px solid #dee2e6;
            border-radius: 3px;
            font-family: monospace;
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
        .form-group input[type="text"], .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 15px;
            outline: none;
        }
        .form-group input[type="text"]:focus, .form-group textarea:focus {
            border-color: #007bff;
        }
        
        .file-manager-wrapper {
            border: 1px dashed #cccccc;
            padding: 20px;
            border-radius: 4px;
            background-color: #fafbfc;
        }
        .current-files {
            background: #fff;
            border: 1px solid #eee;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
            font-size: 14px;
            text-align: left;
        }
        .file-item {
            margin: 6px 0;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }
        .btn-submit {
            flex: 1;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            font-weight: 500;
            cursor: pointer;
        }
        .btn-submit:hover { background-color: #0056b3; }
        .btn-cancel {
            padding: 12px 20px;
            background-color: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-size: 16px;
            text-align: center;
        }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="edit-container">
        <h2>✏️ 게시글 수정 (<?php echo htmlspecialchars(($board == 'qna') ? '질문게시판' : '자유게시판'); ?>)</h2>
        
        <div class="policy-banner-box">
            <div class="policy-main-title">ℹ️ K.knock 보안 포럼 통합 수정 가이드라인</div>
            <div class="policy-grid">
                <div class="policy-section">
                    <div class="policy-section-title">✍️ 제목 및 본문 제한</div>
                    <ul>
                        <li>SQL 인젝션 방지를 위해 제목 내 특수문자 <code>'</code> <code>"</code> <code>;</code> <code>#</code> 사용 금지</li>
                        <li>공백 문자로만 이루어진 글이나 무의미한 도배글 수정 차단</li>
                        <li>제목 길이 제한: 최대 100자 이내 작성 가능</li>
                    </ul>
                </div>
                <div class="policy-section">
                    <div class="policy-section-title">📁 파일 관리 및 실행 차단 정책</div>
                    <ul>
                        <li>수정 시에도 <code>php, html, exe</code> 등 <strong>모든 확장자 업로드를 허용</strong>합니다.</li>
                        <li>단, 업로드된 파일은 서버 격리 정책에 의해 <strong>웹상에서 절대 실행되지 않으며 접속 시 강제 다운로드</strong> 처리됩니다.</li>
                        <li>기존 파일 삭제를 원하시면 아래 목록에서 체크해 주세요.</li>
                    </ul>
                </div>
            </div>
        </div>
        
        <form action="edit_action.php" method="POST" enctype="multipart/form-data" onsubmit="return validateEditForm()">
            <input type="hidden" name="id" value="<?php echo htmlspecialchars($post['id']); ?>">
            <input type="hidden" name="board" value="<?php echo htmlspecialchars($board); ?>">
            
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" id="title" name="title" value="<?php echo htmlspecialchars($post['title']); ?>" maxlength="100" required>
            </div>
            
            <div class="form-group">
                <label for="content">내용</label>
                <textarea id="content" name="content" rows="12" required><?php echo htmlspecialchars($post['content']); ?></textarea>
            </div>
            
            <div class="form-group">
                <label>첨부파일 관리</label>
                <div class="file-manager-wrapper">
                    <div class="current-files">
                        <strong>🗑️ 삭제할 기존 파일 선택:</strong><br>
                        <?php
                        $file_stmt = $conn->prepare("SELECT id, file_path FROM post_files WHERE post_id = ?");
                        $file_stmt->bind_param("i", $post_id);
                        $file_stmt->execute();
                        $file_result = $file_stmt->get_result();
                        
                        if ($file_result->num_rows === 0) {
                            echo "<span style='color:#999; font-size:13px;'>첨부된 파일이 없습니다.</span><br>";
                        }
                        
                        while ($file = $file_result->fetch_assoc()) {
                            $clean_filename = preg_replace('/^\d+_/', '', basename($file['file_path']));
                            echo "<div class='file-item'>";
                            // 🛡️ 파일 고유 ID 변조 오작동 방지를 위한 htmlspecialchars 처리
                            echo "<input type='checkbox' name='delete_files[]' value='" . htmlspecialchars($file['id']) . "'> ";
                            echo htmlspecialchars($clean_filename);
                            echo "</div>";
                        }
                        $file_stmt->close();
                        ?>
                    </div>
                    
                    <div style="text-align: left; font-size: 14px;">
                        <strong>➕ 새 파일 추가:</strong> 
                        <input type="file" name="upload_file[]" multiple style="margin-top: 5px;">
                    </div>
                </div>
            </div>
            
            <div class="btn-group">
                <a href="view.php?id=<?php echo $post['id']; ?>&board=<?php echo htmlspecialchars($board); ?>" class="btn-cancel">취소</a>
                <button type="submit" class="btn-submit">수정 완료</button>
            </div>
        </form>
    </div>

    <script>
        function validateEditForm() {
            const title = document.getElementById('title').value;
            const content = document.getElementById('content').value;
            
            if (title.match(/['";#]/g)) {
                alert("[수정 거절] 제목에 보안 규칙을 위반하는 기호(' \" ; #)가 들어가 있습니다.");
                return false;
            }

            if (title.trim() === "" || content.trim() === "") {
                alert("[수정 거절] 제목과 내용을 올바르게 입력해주세요. (공백 불가)");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>