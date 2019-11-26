<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./anomalies.php" />
</head>
<body>
  <?php
    $id = $_REQUEST['id'];
    removeAnomaly($id); ?>
  <p>Anomalia com id '<?= $id ?>' removida com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
