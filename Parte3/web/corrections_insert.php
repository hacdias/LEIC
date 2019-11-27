<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./corrections.php" />
</head>
<body>
  <?php
    $user = $_REQUEST['user'];
    $anomaly = $_REQUEST['anomaly'];
    $text = $_REQUEST['text'];
    try {
      insertCorrection($user, $anomaly, $text);
      echo "<p>Correção e proposta de correção adicionadas com sucesso.</p>";
    } catch (PDOException $e) {
      echo "<p>Ocorreu um erro:</p>";
      echo "<p style='color:red'>$e;</p>";
    }
  ?>
  <p>Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
