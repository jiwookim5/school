<?php
session_start();
if (!isset($_SESSION['username'])) {
    echo "<script>alert('로그인이 필요합니다.'); location.href='login.php';</script>";
    exit;
}

$board = $_GET['board'] ?? 'free';
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free';
}

$board_name = ($board == 'qna') ? '질문게시판' : '자유게시판';
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>K.knock Security Forum - 글쓰기</title>
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            padding: 40px 20px;
            display: flex;
            justify-content: center;
        }
        .write-container {
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
        
        /* 🚨 가용성 100% 인프라 보안 정책에 맞는 통합 안내 패널 */
        .policy-banner-box {
            background-color: #f8f9fa;
            border-left: 4px solid #007bff; /* 안전을 강조하는 파란색 테두리 */
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
        .file-upload-wrapper {
            border: 1px dashed #cccccc;
            padding: 20px;
            border-radius: 4px;
            background-color: #fafbfc;
            text-align: center;
        }
        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }
        .btn-submit {
            flex: 1;
            padding: 12px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            font-weight: 500;
            cursor: pointer;
        }
        .btn-submit:hover { background-color: #218838; }
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
    <div class="write-container">
        <h2>✏️ 글 작성 (<?php echo htmlspecialchars($board_name); ?>)</h2>
        
        <div class="policy-banner-box">
            <div class="policy-main-title">ℹ️ K.knock 보안 포럼 통합 업로드 가이드라인</div>
            <div class="policy-grid">
                <div class="policy-section">
                    <div class="policy-section-title">✍️ 제목 및 본문 제한</div>
                    <ul>
                        <li>SQL 인젝션 방지를 위해 제목 내 특수문자 <code>'</code> <code>"</code> <code>;</code> <code>#</code> 사용 금지</li>
                        <li>공백 문자로만 이루어진 글이나 무의미한 도배글 차단</li>
                        <li>제목 길이 제한: 최대 100자 이내 작성 가능</li>
                    </ul>
                </div>
                <div class="policy-section">
                    <div class="policy-section-title">📁 실행 권한 제어 보안 정책 (가용성 100%)</div>
                    <ul>
                        <li>본 포럼은 소스코드 공유를 위해 <code>php, html, js, sh, py, exe</code> 등 <strong>모든 확장자 업로드를 허용</strong>합니다.</li>
                        <li>단, 업로드된 파일은 서버 격리 정책에 의해 <strong>웹상에서 절대 실행되지 않으며 접속 시 강제 다운로드</strong> 처리됩니다.</li>
                        <li>확장자가 아예 식별되지 않는 가짜 파일은 업로드가 거절됩니다.</li>
                    </ul>
                </div>
            </div>
        </div>
        
        <form action="write_action.php" method="POST" enctype="multipart/form-data" onsubmit="return validateWriteForm()">
            <input type="hidden" name="board" value="<?php echo htmlspecialchars($board); ?>">
            
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" id="title" name="title" placeholder="제목을 입력하세요 (제한 기호 사용 시 등록 거절)" maxlength="100" required>
            </div>
            
            <div class="form-group">
                <label for="content">내용</label>
                <textarea id="content" name="content" rows="12" placeholder="내용을 입력해 주세요..." required></textarea>
            </div>
            
            <div class="form-group">
                <label>파일 첨부</label>
                <div class="file-upload-wrapper">
                    <input type="file" id="upload_file" name="upload_file[]" multiple>
                </div>
            </div>
            
            <div class="btn-group">
                <a href="index.php?board=<?php echo htmlspecialchars($board); ?>" class="btn-cancel">취소</a>
                <button type="submit" class="btn-submit">작성 완료</button>
            </div>
        </form>
    </div>

    <script>
        function validateWriteForm() {
            const title = document.getElementById('title').value;
            const content = document.getElementById('content').value;
            
            if (title.match(/['";#]/g)) {
                alert("[작성 거절] 제목에 보안 규칙을 위반하는 기호(' ; #)가 들어가 있습니다.");
                return false;
            }

            if (title.trim() === "" || content.trim() === "") {
                alert("[작성 거절] 제목과 내용을 올바르게 채워주세요. (공백 불가)");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>