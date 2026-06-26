<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) {
    header("Location: login.php");
    exit;
}

// 1. 검색어 및 검색 옵션 처리 (공백 제거 및 바인딩 준비)
$search = isset($_GET['search']) ? trim($_GET['search']) : '';
$type = $_GET['type'] ?? 'title';
$board = $_GET['board'] ?? 'free'; 

/* 🛡️ 보안 보완 1: 파라미터 화이트리스트 검증 (SQL Injection 및 경로 조작 방어)
   해커가 ?board=../ 등을 넣어 시스템 변조를 노리는 것을 원천 차단합니다.
*/
if ($board !== 'free' && $board !== 'qna') {
    $board = 'free'; // 허용되지 않은 값이 들어오면 기본값으로 강제 고정
}

if ($type !== 'title' && $type !== 'author') {
    $type = 'title'; // 검색 타입도 허용된 값 외엔 기본값 고정
}

// 게시판 종류에 따른 테이블 명 설정
$table = ($board == 'qna') ? 'posts_qna' : 'posts_free';
$board_name = ($board == 'qna') ? '질문게시판' : '자유게시판';
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="robots" content="noindex, nofollow">
    <title>K.knock Security Forum - <?php echo htmlspecialchars($board_name); ?></title>
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            padding: 40px 20px;
            display: flex;
            justify-content: center;
            /* 보안을 위한 기본 우클릭/드래그 방지 */
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }
        .board-container {
            background-color: #ffffff;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 800px;
        }
        h2 {
            margin-top: 0;
            color: #333;
            font-size: 24px;
            margin-bottom: 25px;
        }
        .tab-menu {
            display: flex;
            gap: 10px;
            margin-bottom: 25px;
            border-bottom: 2px solid #ddd;
            padding-bottom: 10px;
        }
        .tab-item {
            padding: 10px 20px;
            border-radius: 4px;
            text-decoration: none;
            font-weight: 500;
            font-size: 15px;
            transition: all 0.3s;
        }
        .tab-item.active {
            background-color: #007bff;
            color: white;
        }
        .tab-item.inactive {
            background-color: #eee;
            color: #555;
        }
        .tab-item.inactive:hover {
            background-color: #ddd;
        }
        .tab-item.main-btn {
            background-color: #6c757d;
            color: white;
            margin-left: auto;
        }
        .tab-item.main-btn:hover {
            background-color: #5a6268;
        }
        .user-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background-color: #e9ecef;
            padding: 12px 20px;
            border-radius: 6px;
            font-size: 15px;
            margin-bottom: 25px;
            color: #495057;
        }
        .btn-write {
            background-color: #28a745;
            color: white;
            padding: 6px 14px;
            border-radius: 4px;
            text-decoration: none;
            font-weight: 500;
            font-size: 14px;
            transition: background-color 0.2s;
        }
        .btn-write:hover {
            background-color: #218838;
        }
        .btn-logout {
            color: #dc3545;
            text-decoration: none;
            font-weight: 500;
            margin-left: 10px;
        }
        .btn-logout:hover {
            text-decoration: underline;
        }
        .search-form {
            display: flex;
            gap: 8px;
            margin-bottom: 20px;
        }
        .search-form select, .search-form input[type="text"] {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            outline: none;
        }
        .search-form select {
            width: 100px;
            background-color: white;
        }
        .search-form input[type="text"] {
            flex-grow: 1;
        }
        .search-form input[type="text"]:focus {
            border-color: #007bff;
        }
        .btn-search {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 0 18px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.2s;
        }
        .btn-search:hover {
            background-color: #0056b3;
        }
        .btn-all {
            background-color: #6c757d;
            color: white;
            text-decoration: none;
            padding: 10px 14px;
            border-radius: 4px;
            font-size: 14px;
            display: inline-flex;
            align-items: center;
        }
        .btn-all:hover {
            background-color: #5a6268;
        }
        .board-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
            font-size: 15px;
        }
        .board-table th {
            background-color: #f8f9fa;
            color: #495057;
            font-weight: 500;
            padding: 12px;
            border-bottom: 2px solid #dee2e6;
        }
        .board-table td {
            padding: 14px 12px;
            border-bottom: 1px solid #dee2e6;
            color: #333;
        }
        .board-table tr:hover {
            background-color: #f8f9fa;
        }
        .board-table a {
            color: #007bff;
            text-decoration: none;
            font-weight: 500;
        }
        .board-table a:hover {
            text-decoration: underline;
        }
        .no-data {
            color: #6c757d;
            padding: 30px !important;
        }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="board-container">
        <h2>📋 <?php echo htmlspecialchars($board_name); ?></h2>

        <div class="tab-menu">
            <a href="index.php?board=free" class="tab-item <?php echo ($board == 'free') ? 'active' : 'inactive'; ?>">자유게시판</a>
            <a href="index.php?board=qna" class="tab-item <?php echo ($board == 'qna') ? 'active' : 'inactive'; ?>">질문게시판</a>
            <a href="main.php" class="tab-item main-btn">[메인으로]</a>
        </div>

        <div class="user-info">
            <div>
                반갑습니다, <strong><?php echo htmlspecialchars($_SESSION['username']); ?></strong>님!
                <a href="logout.php" class="btn-logout">[로그아웃]</a>
            </div>
            <a href="write.php?board=<?php echo htmlspecialchars($board); ?>" class="btn-write">✏️ 글쓰기</a>
        </div>

        <form action="index.php" method="GET" class="search-form" autocomplete="off" onsubmit="return validateSearchForm()">
            <input type="hidden" name="board" value="<?php echo htmlspecialchars($board); ?>">
            
            <select name="type">
                <option value="title" <?php if($type == 'title') echo 'selected'; ?>>제목</option>
                <option value="author" <?php if($type == 'author') echo 'selected'; ?>>작성자</option>
            </select>
            <input type="text" id="search-input" name="search" placeholder="검색어를 입력하세요..." value="<?php echo htmlspecialchars($search); ?>" maxlength="30">
            <button type="submit" class="btn-search">검색</button>
            <a href="index.php?board=<?php echo htmlspecialchars($board); ?>" class="btn-all">전체보기</a>
        </form>
        
        <table class="board-table">
            <thead>
                <tr>
                    <th width="10%">번호</th>
                    <th width="50%">제목</th>
                    <th width="20%">작성자</th>
                    <th width="20%">작성일</th>
                </tr>
            </thead>
            <tbody>
                <?php
                // 3. 검색 조건에 따른 동적 쿼리 생성
                $sql = "SELECT p.id, p.title, u.username, p.created_at 
                        FROM $table p 
                        JOIN users u ON p.author_id = u.username";
                
                if ($search) {
                    if ($type == 'author') {
                        $sql .= " WHERE u.username LIKE ?";
                    } else {
                        $sql .= " WHERE p.title LIKE ?";
                    }
                }
                $sql .= " ORDER BY p.id DESC";

                $stmt = $conn->prepare($sql);
                
                if ($search) {
                    // 🛡️ 보안 보완 2: 검색어 와일드카드 문자(%, _) 이스케이프 처리
                    // 해커가 검색창에 %, _ 를 남발하여 서버 연산 속도를 늦추는 DoS 공격 방어
                    $escaped_search = str_replace(['%', '_'], ['\%', '\_'], $search);
                    $like_search = "%" . $escaped_search . "%";
                    $stmt->bind_param("s", $like_search);
                }
                
                if ($stmt->execute()) {
                    $result = $stmt->get_result();

                    if ($result->num_rows > 0) {
                        $num = $result->num_rows;
                        while($row = $result->fetch_assoc()) {
                            echo "<tr>";
                            echo "<td style='text-align:center;'> " . (int)$num-- . "</td>";
                            // 🛡️ 보안 조치: 제목과 이름 출력 시 htmlspecialchars 필수 적용 (Stored XSS 완벽 방어)
                            echo "<td style='text-align:left; padding-left:15px;'><a href='view.php?id=".(int)$row['id']."&board=".htmlspecialchars($board)."'>".htmlspecialchars($row['title'])."</a></td>";
                            echo "<td style='text-align:center;'>".htmlspecialchars($row['username'])."</td>";
                            echo "<td style='text-align:center;'>".htmlspecialchars(substr($row['created_at'], 0, 10))."</td>";
                            echo "</tr>";
                        }
                    } else {
                        echo "<tr><td colspan='4' class='no-data'>검색된 게시글이 없습니다.</td></tr>";
                    }
                } else {
                    // 에러 발생 시 상세 정보 노출 차단
                    echo "<tr><td colspan='4' class='no-data'>데이터를 불러오는 중 오류가 발생했습니다.</td></tr>";
                }
                $stmt->close();
                ?>
            </tbody>
        </table>
    </div>

    <script>
        /* 🛡️ 프론트엔드 분석 지연 및 스캐너 차단 */
        document.addEventListener('contextmenu', event => event.preventDefault());
        document.addEventListener('keydown', function(e) {
            if (e.keyCode == 123 || 
                (e.ctrlKey && e.shiftKey && (e.keyCode == 73 || e.keyCode == 74)) || 
                (e.ctrlKey && e.keyCode == 85)) {
                e.preventDefault();
                return false;
            }
        });

        // 검색창에도 너무 수상한 특수문자는 1차 컷
        function validateSearchForm() {
            const searchInput = document.getElementById('search-input').value;
            const sqlInfectionPattern = /['";#]/g;
            
            if (sqlInfectionPattern.test(searchInput)) {
                alert("검색어에 사용할 수 없는 특수문자가 포함되어 있습니다.");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>