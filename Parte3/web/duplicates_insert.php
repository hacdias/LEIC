<?php require __DIR__ . '/lib.php'; ?>
<html>
<head>
  <meta charset="UTF-8">
</head>
<body>
  <p><a href="./duplicates.php">← Página anterior</a></p>
  <?php
    $item1 = $_REQUEST['item1'];
    $item2 = $_REQUEST['item2'];

    if ($item1 == $item2) {
      echo "<p>Os items têm que ser diferentes.</p>";
    }

    if ($item1 > $item2) {
      $temp = $item1;
      $item1 = $item2;
      $item2 = $temp;
    }

    if ($item1 != $item2) {
      try {
        insertDuplicate($item1, $item2);
        echo "<p>Items " . $item1 . " e " . $item2 . " marcados como duplicados.</p>";
      } catch (PDOException $e) {
        echo "<p>Ocorreu um erro:</p>";
        echo "<p style='color:red'>$e;</p>";
      }
    }
  ?>
</body>
</html>
