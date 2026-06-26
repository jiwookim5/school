<?php
// download.php
session_start();
include 'db.php';

// 세션 로그인 검증 추가 (비인가 사용자의 파일 무단 다운로드 방지)
if (!isset($_SESSION['username'])) {
    die("<script>alert('로그인이 필요합니다.'); location.href='login.php';</script>");
}

$file = $_GET['file'] ?? ''; 

if (empty($file)) {
    die("<script>alert('존재하지 않는 파일입니다.'); history.back();</script>");
}

/* 🛡️ 보안 포인트: Directory Traversal (상위 디렉토리 탐색) 공격 방어
  해커가 download.php?file=../../../../etc/passwd 처럼 조작하는 것을 
  basename() 함수가 파일명만 쏙 떼어내어 완벽하게 방어합니다.
*/
$filename_only = basename($file);
$safe_file_path = 'uploads/' . $filename_only;

if (file_exists($safe_file_path)) {
    // 시스템 버퍼를 깔끔하게 비워 파일이 깨지는 현상 방지
    while (ob_get_level() > 0) {
        ob_end_clean();
    }

    // 아파치 엔진을 거치지 않고 PHP가 바이트 단위로 직접 읽어 브라우저에 다운로드로 전달
    header('Content-Description: File Transfer');
    header('Content-Type: application/octet-stream');
    
    // 원본 난수화 파일명으로 다운로드 처리
    header('Content-Disposition: attachment; filename="' . $filename_only . '"');
    header('Expires: 0');
    header('Cache-Control: must-revalidate');
    header('Pragma: public');
    header('Content-Length: ' . filesize($safe_file_path));
    
    readfile($safe_file_path);
    exit;
} else {
    echo "<script>alert('파일이 서버에 존재하지 않습니다.'); history.back();</script>";
    exit;
}
?>