<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./locals.php" />
</head>
<body>
  <?php
    $name = $_REQUEST['name'];
    $locals = removeLocal($name); ?>
  <p>Local '<?= $name ?>' removido com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
