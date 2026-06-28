<?php
// main.php
session_start();
include 'db.php';

// 🛡️ 보안 보완: 로그인하지 않은 상태로 접근 시 로그인 페이지로 안전하게 튕겨냅니다.
if (!isset($_SESSION['username'])) {
    header("Location: login.php");
    exit;
}
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>K.knock Security Forum - 메인</title>
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .main-container {
            background-color: #ffffff;
            padding: 50px 40px;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
            width: 100%;
            max-width: 650px;
            text-align: center;
        }
        
        /* 로고 및 메인 타이틀 */
        .forum-logo {
            font-size: 26px;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 5px;
            letter-spacing: -0.5px;
        }
        .forum-logo span {
            color: #007bff;
        }
        h2 {
            font-size: 16px;
            color: #64748b;
            margin-top: 0;
            margin-bottom: 35px;
            font-weight: 500;
        }

        /* 공통 보안 알림 배너 스타일 */
        .welcome-banner {
            background-color: #f8fafc;
            border-left: 4px solid #007bff;
            padding: 15px 20px;
            border-radius: 4px;
            text-align: left;
            margin-bottom: 30px;
            border-top: 1px solid #e2e8f0;
            border-right: 1px solid #e2e8f0;
            border-bottom: 1px solid #e2e8f0;
        }
        .welcome-banner .user-greet {
            font-size: 14px;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 5px;
        }
        .welcome-banner .user-greet em {
            color: #007bff;
            font-style: normal;
        }
        .welcome-banner .notice-text {
            font-size: 12.5px;
            color: #64748b;
            line-height: 1.5;
        }

        /* 🗂️ 게시판 선택 카드 레이아웃 */
        .board-menu-list {
            display: flex;
            gap: 20px;
            margin-bottom: 35px;
        }
        .board-card {
            flex: 1;
            background-color: #ffffff;
            border: 2px solid #e2e8f0;
            border-radius: 8px;
            padding: 25px 20px;
            text-decoration: none;
            color: #1e293b;
            transition: all 0.25s ease;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
        }
        .board-card:hover {
            border-color: #007bff;
            box-shadow: 0 8px 20px rgba(0, 123, 255, 0.08);
            transform: translateY(-3px);
        }
        .board-card .icon {
            font-size: 32px;
            margin-bottom: 12px;
        }
        .board-card .title {
            font-size: 16px;
            font-weight: 700;
            margin-bottom: 6px;
        }
        .board-card .desc {
            font-size: 12.5px;
            color: #94a3b8;
            text-align: center;
        }

        /* 🚪 하단 유틸리티 링크 (로그아웃) */
        .footer-actions {
            border-top: 1px solid #edf2f7;
            padding-top: 20px;
        }
        .btn-logout {
            color: #64748b;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            transition: color 0.15s;
            padding: 8px 16px;
            border-radius: 4px;
        }
        .btn-logout:hover {
            color: #ef4444;
            background-color: #fee2e2;
        }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>

    <div class="main-container">
        <div class="forum-logo">K.knock Security <span>Forum</span></div>
        <h2>보안 실습 포럼에 오신 것을 환영합니다</h2>

        <div class="welcome-banner">
            <div class="user-greet">👋 안녕하세요, <em><?php echo htmlspecialchars($_SESSION['username']); ?></em>님!</div>
            <div class="notice-text">원하시는 게시판 카드를 선택하여 모의 해킹 공방전 실습 및 안전한 시큐어 코딩 분석을 진행해 주세요.</div>
        </div>

        <div class="board-menu-list">
            <a href="index.php?board=free" class="board-card">
                <span class="icon">💬</span>
                <span class="title">자유게시판</span>
                <span class="desc">자유로운 의견과 일상을 공유하는 공간</span>
            </a>
            
            <a href="index.php?board=qna" class="board-card">
                <span class="icon">❓</span>
                <span class="title">질문게시판</span>
                <span class="desc">보안 취약점 및 코딩 질의응답 (Q&A)</span>
            </a>
        </div>

        <div class="footer-actions">
            <a href="logout.php" class="btn-logout">🔒 안전하게 로그아웃</a>
        </div>
    </div>

</body>
</html>