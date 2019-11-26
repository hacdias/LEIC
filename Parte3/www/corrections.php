<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<body>
  <?php
    $corrections = getCorrections();
    $correctionProposal = getCorrectionProposals();
    $incidences = getIncidences();
  ?>

  <form method="GET" action="./corrections_insert.php">
    <h2>Nova Correção</h2>

    <label>Incidência</label>
    <select name="anomaly">
      <?php foreach ($incidences as $row): ?>
        <option value="<?=$row['anomalia_id']?>"><?=$row['anomalia_id']?></option>
      <?php endforeach; ?> 
    </select>
    <br>

    <label>Proposta de Correção</label>
    <select name="proposal">
      <?php foreach ($correctionProposal as $row): ?>
        <option value="<?=$row['email']?>,<?=$row['nro']?>"><?=$row['email']?>,<?=$row['nro']?></option>
      <?php endforeach; ?> 
    </select>
    <br>

    <input type="submit" value="Criar" />
  </form>

  <table>
    <tr>
      <th>Email</td>
      <th>Nº</th>
      <th>Anomalia</th>
      <th></th>
    </tr>
    <?php foreach ($corrections as $row): ?>
      <tr>
        <td><?=$row['email']?></td>
        <td><?=$row['nro']?></td>
        <td><?=$row['anomalia_id']?></td>
        <td><a href="./corrections_remove.php?email=<?=$row['email']?>&nro=<?=$row['nro']?>&anomaly=<?=$row['anomalia_id']?>">Remover</a></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>
