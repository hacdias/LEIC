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
  <p><a href="./index.php">← Página incial</a></p>

  <?php
    $corrections = [];
    $users = [];
    $correctionProposal = [];
    $incidences = [];

    try {
      $corrections = getCorrections();
      $users = getQualifiedUsers();
      $correctionProposal = getCorrectionProposals();
      $incidences = getIncidences();
    } catch (PDOException $e) {
      echo "<p>Não foi possível obter os dados.</p>";
      echo "<p style='color:red'>$e->getMessage();</p></body></html>";
      die(1);
    }
  ?>

  <form method="GET" action="./corrections_insert.php">
    <h2>Nova Correção & Proposta de Correção</h2>

    <label>Utilizador</label>
    <select required name="user">
      <?php foreach ($users as $row): ?>
        <option value="<?=$row['email']?>"><?=$row['email']?></option>
      <?php endforeach; ?>
    </select>
    <br>
    <br>

    <label>Incidência</label>
    <select required name="anomaly">
      <?php foreach ($incidences as $row): ?>
        <option value="<?=$row['anomalia_id']?>"><?=$row['anomalia_id']?></option>
      <?php endforeach; ?>
    </select>
    <br>
    <br>

    <label>Texto</label>
    <br>
    <textarea required name="text"></textarea>
    <br>
    <br>

    <input type="submit" value="Criar" />
  </form>

  <h2>Correções e Propostas</h2>

  <table>
    <tr>
      <th>Email</td>
      <th>Nº</th>
      <th>Anomalia</th>
      <th>Data e Hora</th>
      <th>Texto</th>
      <th></th>
      <th></th>
    </tr>
    <?php foreach ($corrections as $row): ?>
      <tr>
        <td><?=$row['email']?></td>
        <td><?=$row['nro']?></td>
        <td><?=$row['anomalia_id']?></td>
        <td><?=$row['data_hora']?></td>
        <td><?=$row['texto']?></td>
        <td><a href="./corrections_remove.php?email=<?=$row['email']?>&nro=<?=$row['nro']?>&anomaly=<?=$row['anomalia_id']?>">Remover</a></td>
        <td><a href="./corrections_edit.php?email=<?=$row['email']?>&nro=<?=$row['nro']?>">Editar</a></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>
