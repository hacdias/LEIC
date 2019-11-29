<?php require __DIR__ . '/lib.php'; ?>
<html>   
<head>
  <meta charset="UTF-8">
</head>
<body>
  <p><a href="./items.php">← Página anterior</a></p>
  <?php
    $id = $_REQUEST['id'];

    try {
      removeItem($id);
      echo "<p>Item com id $id removido com sucesso.</p>";
    } catch (PDOException $e) {
      echo "<p>Ocorreu um erro:</p>";
      echo "<p style='color:red'>$e;</p>";
    }

    ?>
</body>
</html>
