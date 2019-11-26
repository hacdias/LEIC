<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./corrections.php" />
</head>
<body>
  <?php
    $anomaly = $_REQUEST['anomaly'];
    $proposal = explode(",", $_REQUEST['proposal']);
    insertCorrection($proposal[0], $proposal[1], $anomaly); ?>
  <p>Correção adicionada com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
