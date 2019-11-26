<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./correction_proposals.php" />
</head>
<body>
  <?php
    $user = $_REQUEST['user'];
    $date = $_REQUEST['date'];
    $time = $_REQUEST['time'];
    $text = $_REQUEST['text'];
    insertCorrectionProposal($user, $date . ' ' . $time, $text); ?>
  <p>Proposta de correções adicionada com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
