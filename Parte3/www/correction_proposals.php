<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<body>
  <?php
    $correctionProposal = getCorrectionProposals();
    $users = getQualifiedUsers();
  ?>

  <form method="GET" action="./correction_proposals_insert.php">
    <h2>Nova Proposta de Correção</h2>

    <label>Utilizador</label>
    <select name="user">
      <?php foreach ($users as $row): ?>
        <option value="<?=$row['email']?>,<?=$row['nro']?>"><?=$row['email']?>,<?=$row['nro']?></option>
      <?php endforeach; ?>
    </select>
    <br>

    <input type="date" name="date" /><br>
    <input type="time" name="time" /><br>

    <textarea name="text"></textarea>

    <input type="submit" value="Criar" />
  </form>

  <table>
    <tr>
      <th>Email</td>
      <th>Nº</th>
      <th>Data e Hora</th>
      <th>Texto</th>
      <th></th>
    </tr>
    <?php foreach ($correctionProposal as $row): ?>
      <tr>
        <td><?=$row['email']?></td>
        <td><?=$row['nro']?></td>
        <td><?=$row['data_hora']?></td>
        <td><?=$row['texto']?></td>
        <td><a href="./correction_proposals_remove.php?email=<?=$row['email']?>&nro=<?=$row['nro']?>">Remover</a></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>
