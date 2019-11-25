<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./items.php" />
</head>
<body>
  <?php
    $descricao = $_REQUEST['descricao'];
    $localizacao = $_REQUEST['localizacao'];
    $latitude = $_REQUEST['latitude'];
    $longitude = $_REQUEST['longitude'];
    insertItem($descricao, $localizacao, $latitude, $longitude); ?>
  <p>Item '<?= $name ?>' adicionado com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
