<?php 
  try {
    require __DIR__ . '/lib.php';
   } catch (PDOException $e) {
    echo "<p style='color:red'>Não foi possível ligar à base de dados!</p>";
    die(1);
   }
?>

<html>
<head>
  <meta charset="UTF-8"> 
</head>
<body>
  <p><a href="./items.php">← Página anterior</a></p>
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
</body>
</html>
