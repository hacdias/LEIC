<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./anomalies.php" />
</head>
<body>
  <?php
    $zona = $_REQUEST['zona'];
    $imagem = $_REQUEST['imagem'];
    $lingua = $_REQUEST['lingua'];
    $ts = $_REQUEST['ts'];
    $descricao = $_REQUEST['descricao'];
    $tem_anomalia_redacao = $_REQUEST['tem_anomalia_redacao'];
    insertAnomaly($zona, $imagem, $lingua, $ts, $descricao, $tem_anomalia_redacao); ?>
  <p>Anomalia '<?= $descricao ?>' adicionada com sucesso. Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
