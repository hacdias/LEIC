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
  <p><a href="./corrections.php">← Página anterior</a></p>
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
</body>
</html>
