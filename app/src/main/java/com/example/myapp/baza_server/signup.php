<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['fullname']) && isset($_POST['email']) && isset($_POST['username']) && isset($_POST['password'])) {
    if ($db->dbConnect()) {


      echo $db->signUp("users", $_POST['fullname'], $_POST['email'], $_POST['username'], $_POST['password']));
?>
