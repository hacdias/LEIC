<?php require __DIR__ . '/lib.php'; ?>
<html>
<head>
  <meta charset="UTF-8">
</head>
<body>
  <p><a href="./anomalies.php">← Página anterior</a></p>
  <?php
    $id = $_REQUEST['id'];

    try {
      removeAnomaly($id);
      echo "<p>Anomalia com id $id removida com sucesso.</p>";
    } catch (PDOException $e) {
      echo "<p>Ocorreu um erro:</p>";
      echo "<p style='color:red'>$e;</p>";
    }
  ?>
</body>
</html>
