<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) {
    header("Location: login.php");
    exit;
}

// 1. 검색어, 검색 옵션, 게시판 종류 처리
$search = $_GET['search'] ?? '';
$type = $_GET['type'] ?? 'title';
$board = $_GET['board'] ?? 'free'; // 기본값은 자유게시판(free)

// 게시판 종류에 따른 테이블 명 설정
$table = ($board == 'qna') ? 'posts_qna' : 'posts_free';
$board_name = ($board == 'qna') ? '질문게시판' : '자유게시판';
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title><?php echo $board_name; ?></title>
</head>
<body>
    <h2>📋 <?php echo $board_name; ?></h2>

    <div style="margin-bottom: 20px;">
        <a href="index.php?board=free" style="padding: 10px; border: 1px solid #ccc; text-decoration:none; background: <?php echo ($board == 'free') ? '#ddd' : '#eee'; ?>;">자유게시판</a>
        <a href="index.php?board=qna" style="padding: 10px; border: 1px solid #ccc; text-decoration:none; background: <?php echo ($board == 'qna') ? '#ddd' : '#eee'; ?>;">질문게시판</a>
        <a href="main.php" style="margin-left: 20px;">[메인으로]</a>
    </div>

    <p>
        반갑습니다, <strong><?php echo $_SESSION['username']; ?></strong>님! 
        <a href="write.php?board=<?php echo htmlspecialchars($board); ?>">[글쓰기]</a> | 
        <a href="logout.php" style="color: red;">[로그아웃]</a>
    </p>

    <form action="index.php" method="GET" style="margin-bottom: 20px;">
        <input type="hidden" name="board" value="<?php echo htmlspecialchars($board); ?>">
        
        <select name="type">
            <option value="title" <?php if($type == 'title') echo 'selected'; ?>>제목</option>
            <option value="author" <?php if($type == 'author') echo 'selected'; ?>>작성자</option>
        </select>
        <input type="text" name="search" placeholder="검색어 입력" value="<?php echo htmlspecialchars($search); ?>">
        <button type="submit">검색</button>
        <a href="index.php?board=<?php echo htmlspecialchars($board); ?>">[전체보기]</a>
    </form>
    
    <table border="1" cellpadding="5" cellspacing="0" style="width: 600px; text-align: center;">
        <tr bgcolor="#f2f2f2">
            <th width="10%">번호</th>
            <th width="50%">제목</th>
            <th width="20%">작성자</th>
            <th width="20%">작성일</th>
        </tr>
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
            $like_search = "%" . $search . "%";
            $stmt->bind_param("s", $like_search);
        }
        
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $num = $result->num_rows;
            while($row = $result->fetch_assoc()) {
                echo "<tr>";
                echo "<td>" . $num-- . "</td>";
                echo "<td style='text-align:left; padding-left:10px;'><a href='view.php?id=".$row['id']."&board=".htmlspecialchars($board)."'>".htmlspecialchars($row['title'])."</a></td>";
                echo "<td>".htmlspecialchars($row['username'])."</td>";
                echo "<td>".substr($row['created_at'], 0, 10)."</td>";
                echo "</tr>";
            }
        } else {
            echo "<tr><td colspan='4'>검색된 게시글이 없습니다.</td></tr>";
        }
        ?>
    </table>
</body>
</html>