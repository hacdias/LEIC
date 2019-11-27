<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<body>
  <p><a href="./index.php">← Página incial</a></p>

  <?php
    $corrections = getCorrections();
    $users = getQualifiedUsers();
    $correctionProposal = getCorrectionProposals();
    $incidences = getIncidences();
  ?>

  <form method="GET" action="./corrections_insert.php">
    <h2>Nova Correção & Proposta de Correção</h2>

    <label>Utilizador</label>
    <select name="user">
      <?php foreach ($users as $row): ?>
        <option value="<?=$row['email']?>"><?=$row['email']?></option>
      <?php endforeach; ?>
    </select>
    <br>

    <label>Incidência</label>
    <select name="anomaly">
      <?php foreach ($incidences as $row): ?>
        <option value="<?=$row['anomalia_id']?>"><?=$row['anomalia_id']?></option>
      <?php endforeach; ?>
    </select>
    <br>

    <textarea name="text"></textarea>

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
