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
  <?php
    $email = $_REQUEST['email'];
    $nro = $_REQUEST['nro']; ?>

  <p>A alterar a proposta de correção (<?=$email?>, <?=$nro?>)</p>

  <form method="GET" action="./corrections_edit_submit.php">
    <input type="hidden" name="email" value="<?=$email?>" />
    <input type="hidden" name="nro" value="<?=$nro?>" />
    <input required type="text" name="text" placeholder='Novo texto' />
    <input type="submit" value="Atualizar" />
  </form>
</body>
</html>
