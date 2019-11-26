<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./incidences.php" />
</head>
<body>
  <?php
    $email = $_REQUEST['email'];
    $anomaly = $_REQUEST['anomaly'];
    $item = $_REQUEST['item'];
    insertIncidence($anomaly, $item, $email); ?>
  <p>Incidência adicionada com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
