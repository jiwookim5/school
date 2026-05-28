<?php
include 'db.php';
$new_pass = password_hash('1234', PASSWORD_DEFAULT);
$stmt = $conn->prepare("UPDATE users SET password = ? WHERE id = 'admin'");
$stmt->bind_param("s", $new_pass);
$stmt->execute();
echo "비밀번호가 1234로 초기화되었습니다!";
?>