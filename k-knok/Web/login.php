<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="robots" content="noindex, nofollow">
    <title>K.knock Security Forum - 로그인</title>
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f4f7f6;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            /* 마우스 드래그 및 우클릭 방지 스타일 (해커의 소스코드 분석을 지연시킴) */
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }
        .login-container {
            background-color: #ffffff;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            width: 350px;
            text-align: center;
        }
        .login-container h2 {
            margin-bottom: 30px;
            color: #333;
        }
        .input-group {
            margin-bottom: 20px;
            text-align: left;
        }
        .input-group label {
            display: block;
            margin-bottom: 8px;
            color: #666;
            font-size: 14px;
        }
        .input-group input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 16px;
        }
        .input-group input:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 5px rgba(0, 123, 255, 0.2);
        }
        .login-button {
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .login-button:hover {
            background-color: #0056b3;
        }
        .register-link {
            margin-top: 20px;
            display: block;
            color: #666;
            font-size: 14px;
            text-decoration: none;
        }
        .register-link:hover {
            text-decoration: underline;
            color: #007bff;
        }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="login-container">
        <h2>K.knock 게시판</h2>
        
        <form action="login_action.php" method="POST" autocomplete="off" onsubmit="return validateForm()">
            <div class="input-group">
                <label for="username">아이디</label>
                <input type="text" id="username" name="username" placeholder="아이디를 입력하세요" maxlength="20" required>
            </div>
            <div class="input-group">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" maxlength="32" required>
            </div>
            <button type="submit" class="login-button">로그인</button>
        </form>
        <a href="register.php" class="register-link">아직 회원이 아니신가요? (회원가입)</a>
    </div>

    <script>
        /* 🛡️ 클라이언트 사이드 1차 방어 스크립트 */
        
        // 1. 개발자 도구(F12) 및 우클릭 차단으로 소스 코드 분석 지연
        document.addEventListener('contextmenu', event => event.preventDefault());
        document.addEventListener('keydown', function(e) {
            if (e.keyCode == 123) { // F12 차단
                e.preventDefault();
                return false;
            }
            if (e.ctrlKey && e.shiftKey && (e.keyCode == 73 || e.keyCode == 74)) { // Ctrl+Shift+I, Ctrl+Shift+J 차단
                e.preventDefault();
                return false;
            }
            if (e.ctrlKey && e.keyCode == 85) { // Ctrl+U (소스 보기) 차단
                e.preventDefault();
                return false;
            }
        });

        // 2. 입력값 검증 (특수문자가 포함된 명백한 SQL Injection 시도를 1차적으로 컷)
        function validateForm() {
            const usernameInput = document.getElementById('username').value;
            
            // SQL Injection에 주로 사용되는 특수문자들 (', ", --, #, =, 등) 검사 정규식
            const sqlInfectionPattern = /['"--;#=]/g;
            
            if (sqlInfectionPattern.test(usernameInput)) {
                alert("입력할 수 없는 특수문자가 포함되어 있습니다.");
                return false; // 서버(login_action.php)로 데이터를 보내지 않고 차단
            }
            return true;
        }
    </script>
</body>
</html>