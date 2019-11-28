<?php require __DIR__ . '/lib/lib.php'; ?>
<html>   
<head>
  <meta charset="UTF-8">
  <meta http-equiv="Refresh" content="5; url=./items.php" />
</head>
<body>
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
  <p>Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
