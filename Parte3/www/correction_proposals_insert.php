<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./correction_proposals.php" />
</head>
<body>
  <?php
    $user = explode(",", $_REQUEST['user']);
    $date = $_REQUEST['date'];
    $time = $_REQUEST['time'];
    $text = $_REQUEST['text'];
    insertCorrectionProposal($user[0], $user[1], $date . ' ' . $time, $text); ?>
  <p>Proposta de correções adicionada com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
