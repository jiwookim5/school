<?php
session_start();
include 'db.php';

if (!isset($_SESSION['username'])) { // 로그인 세션 키를 'username'으로 통일하는 것이 좋습니다.
    header("Location: login.php");
    exit;
}

// 1. 검색어 처리
$search = $_GET['search'] ?? '';
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 목록</title>
</head>
<body>
    <h2>📋 게시판</h2>
    <p>
        반갑습니다, <strong><?php echo $_SESSION['username']; ?></strong>님! 
        <a href="write.php">[글쓰기]</a> | 
        <a href="logout.php" style="color: red;">[로그아웃]</a>
    </p>

    <form action="index.php" method="GET" style="margin-bottom: 20px;">
        <input type="text" name="search" placeholder="제목 검색" value="<?php echo htmlspecialchars($search); ?>">
        <button type="submit">검색</button>
        <a href="index.php">[전체보기]</a>
    </form>
    
    <table border="1" cellpadding="5" cellspacing="0" style="width: 600px; text-align: center;">
        <tr bgcolor="#f2f2f2">
            <th width="10%">번호</th>
            <th width="50%">제목</th>
            <th width="20%">작성자</th>
            <th width="20%">작성일</th>
        </tr>
        <?php
        // 3. 검색어에 따른 쿼리 동적 변경
        if ($search) {
            $sql = "SELECT p.id, p.title, u.username, p.created_at 
                    FROM posts p 
                    JOIN users u ON p.author_id = u.username 
                    WHERE p.title LIKE ? 
                    ORDER BY p.id DESC";
            $stmt = $conn->prepare($sql);
            $like_search = "%" . $search . "%";
            $stmt->bind_param("s", $like_search);
        } else {
            $sql = "SELECT p.id, p.title, u.username, p.created_at 
                    FROM posts p 
                    JOIN users u ON p.author_id = u.username 
                    ORDER BY p.id DESC";
            $stmt = $conn->prepare($sql);
        }
        
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $num = $result->num_rows; // 번호 출력을 위해 전체 개수 저장
            while($row = $result->fetch_assoc()) {
                echo "<tr>";
                echo "<td>" . $num-- . "</td>"; // 번호를 역순으로 출력
                echo "<td style='text-align:left; padding-left:10px;'><a href='view.php?id=".$row['id']."'>".htmlspecialchars($row['title'])."</a></td>";
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