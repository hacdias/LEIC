<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./corrections.php" />
  <meta charset="UTF-8">
</head>
<body>
  <?php
    $email = $_REQUEST['email'];
    $nro = $_REQUEST['nro'];
    $text = $_REQUEST['text'];
    try {
      editCorrectionProposal($email, $nro, $text);
      echo "<p>Correção e proposta editada com sucesso.</p>";
    } catch (PDOException $e) {
      echo "<p>Ocorreu um erro:</p>";
      echo "<p style='color:red'>$e;</p>";
    }
  ?>
  <p>Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
