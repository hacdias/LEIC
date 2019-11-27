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
    insertCorrection($user, $anomaly, $text); ?>
  <p>Correção e proposta de correção adicionadas com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
