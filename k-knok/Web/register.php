<?php
// register.php
include 'db.php';

// 세션 시작 (공격 방어용 토큰이나 카운터 활용 대비)
if (session_status() == PHP_SESSION_NONE) {
    session_start();
}

// 사용자가 회원가입 버튼을 눌러서 POST 요청을 보냈을 때만 실행
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = trim($_POST['username']);
    $password = trim($_POST['password']);

    // 1. 필수 입력값 검증
    if (empty($username) || empty($password)) {
        echo "<script>alert('아이디와 비밀번호를 모두 입력해주세요.'); history.back();</script>";
        exit;
    }

    /* 🛡️ 보안 강화 1: 입력값 길이 및 문자열 필터링 (클라이언트 우회 차단)
      해커는 개발자 도구로 HTML의 maxlength를 지우고 공격 스크립트를 보낼 수 있습니다.
      따라서 백엔드(PHP)에서 글자 수와 입력 형태를 반드시 2차 검증해야 합니다.
    */
    if (strlen($username) > 20 || strlen($password) > 32) {
        echo "<script>alert('입력 허용 길이를 초과했습니다.'); history.back();</script>";
        exit;
    }

    // 아이디에 알파벳, 숫자만 허용 (SQL 인젝션 및 XSS 원천 차단)
    // 공방전에서 아이디에 특수문자를 쓸 일은 공격용 외엔 없습니다.
    if (!preg_match('/^[a-zA-Z0-9_]+$/', $username)) {
        echo "<script>alert('아이디는 영문, 숫자, 언더바(_)만 사용 가능합니다.'); history.back();</script>";
        exit;
    }

    // 2. 비밀번호 안전하게 해시화
    $hashed_password = password_hash($password, PASSWORD_BCRYPT);

    // 3. SQL 인젝션 방지를 위한 Prepared Statement 사용 (기존 좋은 코드 유지)
    // 먼저 아이디 중복 체크
    $stmt = $conn->prepare("SELECT username FROM users WHERE username = ?");
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        // 보안 팁: 아이디가 이미 있다는 메시지는 유지하되, 상세한 SQL 에러 유출은 철저히 막음
        echo "<script>alert('이미 존재하는 아이디입니다.'); history.back();</script>";
        $stmt->close();
        exit;
    }
    $stmt->close();

    // 4. 중복이 없으면 DB에 인서트
    $stmt = $conn->prepare("INSERT INTO users (username, password) VALUES (?, ?)");
    $stmt->bind_param("ss", $username, $hashed_password);

    if ($stmt->execute()) {
        echo "<script>alert('회원가입이 완료되었습니다! 로그인해주세요.'); location.href='login.php';</script>";
    } else {
        // 🛡️ 보안 강화 2: 시스템 에러 추상화
        // DB 에러 메시지($conn->error)를 화면에 뿌리면 해커에게 힌트가 되므로 절대 금지
        echo "<script>alert('회원가입 처리 중 오류가 발생했습니다.'); history.back();</script>";
    }
    $stmt->close();
    exit;
}
?>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="robots" content="noindex, nofollow">
    <title>K.knock Security Forum - 회원가입</title>
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f4f7f6;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }
        .register-container {
            background-color: #ffffff;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            width: 350px;
            text-align: center;
        }
        .register-container h2 {
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
            border-color: #28a745;
            box-shadow: 0 0 5px rgba(40, 167, 69, 0.2);
        }
        .register-button {
            width: 100%;
            padding: 12px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .register-button:hover {
            background-color: #218838;
        }
        .login-link {
            margin-top: 20px;
            display: block;
            color: #666;
            font-size: 14px;
            text-decoration: none;
        }
        .login-link:hover {
            text-decoration: underline;
            color: #28a745;
        }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="register-container">
        <h2>K.knock 회원가입</h2>
        <form action="register.php" method="POST" autocomplete="off" onsubmit="return validateRegisterForm()">
            <div class="input-group">
                <label for="username">아이디</label>
                <input type="text" id="username" name="username" placeholder="생성할 아이디를 입력하세요" maxlength="20" required>
            </div>
            <div class="input-group">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" maxlength="32" required>
            </div>
            <button type="submit" class="register-button">가입하기</button>
        </form>
        <a href="login.php" class="login-link">이미 계정이 있으신가요? (로그인하기)</a>
    </div>

    <script>
        document.addEventListener('contextmenu', event => event.preventDefault());
        document.addEventListener('keydown', function(e) {
            if (e.keyCode == 123 || 
                (e.ctrlKey && e.shiftKey && (e.keyCode == 73 || e.keyCode == 74)) || 
                (e.ctrlKey && e.keyCode == 85)) {
                e.preventDefault();
                return false;
            }
        });

        function validateRegisterForm() {
            const usernameInput = document.getElementById('username').value;
            // 프론트엔드에서도 알파벳, 숫자, 언더바만 허용하도록 검증 정규식 동기화
            const validPattern = /^[a-zA-Z0-9_]+$/;
            
            if (!validPattern.test(usernameInput)) {
                alert("아이디는 영문, 숫자, 언더바(_)만 사용 가능합니다.");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>