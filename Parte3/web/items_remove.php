<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./items.php" />
</head>
<body>
  <?php
    $id = $_REQUEST['id'];
    $locals = removeItem($id); ?>
  <p>Item com id '<?= $id ?>' removido com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
