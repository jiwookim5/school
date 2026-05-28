<?php
// 1. 세션을 시작합니다.
session_start();

// 2. 세션의 모든 정보를 삭제합니다.
session_unset();

// 3. 세션을 완전히 파괴합니다.
session_destroy();

echo "<script>alert('로그아웃 되었습니다.'); location.href='register.php';</script>";exit;
?>