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
    $ts = $_REQUEST['date'] . ' ' . $_REQUEST['time'];
    $descricao = $_REQUEST['descricao'];
    $tem_anomalia_redacao = $_REQUEST['tem_anomalia_redacao'] == 'on' ? 'True' : 'False';

    try {
      insertAnomaly($zona, $imagem, $lingua, $ts, $descricao, $tem_anomalia_redacao);
      echo "<p>Anomalia $descricao adicionada com sucesso.</p>";
    } catch (PDOException $e) {
      echo "<p>Ocorreu um erro:</p>";
      echo "<p style='color:red'>$e;</p>";
    }
  ?>
  <p>Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
