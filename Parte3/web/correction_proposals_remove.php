<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./corrections.php" />
</head>
<body>
  <?php
    $email = $_REQUEST['email'];
    $nro = $_REQUEST['nro'];
    removeCorrectionProposal($email, $nro); ?>
  <p>Proposta de correção removida com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
