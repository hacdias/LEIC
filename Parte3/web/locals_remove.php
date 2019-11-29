<?php require __DIR__ . '/lib.php'; ?>
<html>
<head>
  <meta charset="UTF-8">
</head>
<body>
  <p><a href="./locals.php">← Página anterior</a></p>
  <?php
    $name = $_REQUEST['name'];
    $latitude = $_REQUEST['latitude'];
    $longitude = $_REQUEST['longitude'];

    try {
      removeLocal($latitude, $longitude);
      echo "<p>Local $name  removido com sucesso. </p>";
    } catch (PDOException $e) {
      echo "<p>Ocorreu um erro:</p>";
      echo "<p style='color:red'>$e;</p>";
    }
    ?>
</body>
</html>
