<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta charset="UTF-8"> 
  <meta http-equiv="Refresh" content="5; url=./items.php" />
</head>
<body>
  <?php
    $descricao = $_REQUEST['descricao'];
    $localizacao = $_REQUEST['localizacao'];
    $latitude = $_REQUEST['latitude'];
    $longitude = $_REQUEST['longitude'];

    try {
      insertItem($descricao, $localizacao, $latitude, $longitude); 
      echo "<p>Item '$descricao ' adicionado com sucesso.</p>";
    } catch (PDOException $e) {
      echo "<p>Ocorreu um erro:</p>";
      echo "<p style='color:red'>$e;</p>";
    }

    ?>
  <p>Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
