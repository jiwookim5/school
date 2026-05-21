<?php
include 'db.php';

// 로그인 안 되어 있으면 로그인창으로
if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit;
}
?>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 목록</title>
</head>
<body>
    <h2>📋 스터디 게시판</h2>
    <p>반갑습니다, <strong><?php echo $_SESSION['username']; ?></strong>님! 
       <a href="write.php">[글쓰기]</a>
    </p>
    
    <table border="1" cellpadding="5" cellspacing="0" style="width: 600px; text-align: center;">
        <tr bgcolor="#f2f2f2">
            <th width="10%">번호</th>
            <th width="50%">제목</th>
            <th width="20%">작성자</th>
            <th width="20%">작성일</th>
        </tr>
        <?php
        // posts와 users를 조인해서 데이터 가져오기
        $sql = "SELECT p.id, p.title, u.username, p.created_at 
                FROM posts p 
                JOIN users u ON p.author_id = u.id 
                ORDER BY p.id DESC";
        $result = $conn->query($sql);

        // [★ 핵심수정] 현재 검색된 총 게시글의 개수를 가상 번호의 시작점으로 잡습니다.
        $virtual_number = $result->num_rows;

        if ($virtual_number > 0) {
            while($row = $result->fetch_assoc()) {
                echo "<tr>";
                // DB의 row['id'] 대신, 1씩 줄어드는 가상 번호를 화면에 찍어줍니다.
                echo "<td>" . $virtual_number . "</td>";
                // 상세보기 링크에는 여전히 DB 고유의 고유 ID(row['id'])를 넘겨주어야 정상 작동합니다!
                echo "<td style='text-align:left; padding-left:10px;'><a href='view.php?id=".$row['id']."'>".htmlspecialchars($row['title'])."</a></td>";
                echo "<td>".htmlspecialchars($row['username'])."</td>";
                echo "<td>".substr($row['created_at'], 0, 10)."</td>";
                echo "</tr>";
                
                // 다음 줄을 위해 번호를 1 감소
                $virtual_number--;
            }
        } else {
            echo "<tr><td colspan='4'>작성된 게시글이 없습니다.</td></tr>";
        }
        ?>
    </table>
</body>
</html>