<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
</head>
<body>
  <?php
    $email = $_REQUEST['email'];
    $nro = $_REQUEST['nro']; ?>

  <p>A alterar a proposta de correção (<?=$email?>, <?=$nro?>)</p>

  <form method="GET" action="./corrections_edit_submit.php">
    <input type="hidden" name="email" value="<?=$email?>" />
    <input type="hidden" name="nro" value="<?=$nro?>" />
    <input type="text" name="text" placeholder='Novo texto' />
    <input type="submit" value="Atualizar" />
  </form>
</body>
</html>
