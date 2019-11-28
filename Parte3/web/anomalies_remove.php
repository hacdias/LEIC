<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./anomalies.php" />
  <meta char="UTF-8">
</head>
<body>
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
  <p>Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
