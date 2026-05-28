<?php
$file = $_GET['file']; // 예: download.php?file=uploads/12345_test.jpg
if (file_exists($file)) {
    header('Content-Description: File Transfer');
    header('Content-Type: application/octet-stream');
    header('Content-Disposition: attachment; filename="'.basename($file).'"');
    readfile($file);
}
?>