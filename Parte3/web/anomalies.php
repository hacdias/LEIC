<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <title>Anomalias</title>
</head>
<body>
  <?php $anomalies = getAnomalies();?>

  <form method="GET" action="./anomalies_insert.php">
    <h2>Nova Anomalia</h2>
    <input type="text" name="zona" placeholder="Zona x1, y1, x2, y2" />
    <input type="text" name="imagem" placeholder="Imagem (bits)" />
    <input type="text" name="lingua" placeholder="Lingua" />
    <input type="date" name="date" />
    <input type="time" name="time" />
    <input type="text" name="descricao" placeholder="Descrição" />
    <input type="checkbox" name="tem_anomalia_redacao" />
    <label for="exampleCheck1">Tem anomalia de redação</label>
    <input type="submit" value="Criar" />
  </form>

  <form method="GET" action="./anomalies_between_locals.php">
    <h2>Anomalias entre:</h2>
    <?php $locals = getLocals(); ?>

    <div>
      <label for="local1">Local 1</label>
      <select name="local1" id="local1">
        <option selected>Escolha...</option>
        <?php foreach ($locals as $row): ?>
        <option value="<?=$row['latitude']?>,<?=$row['longitude']?>"><?=$row['nome']?> <?=$row['latitude']?>, <?=$row['longitude']?></option>
       <?php endforeach; ?> 
      </select>
    </div>

    <div>
      <label for="local2">Local 2</label>
      <select name="local2" id="local2">
        <option selected>Escolha...</option>
        <?php foreach ($locals as $row): ?>
        <option value="<?=$row['latitude']?>,<?=$row['longitude']?>"><?=$row['nome']?> <?=$row['latitude']?>, <?=$row['longitude']?></option>
       <?php endforeach; ?> 
      </select>
    </div>

    <input type="submit" value="Procurar" />
  </form>

  <table>
    <tr>
      <th scope="col">ID</td>
      <th scope="col">Zona</th>
      <th scope="col">Imagem</th>
      <th scope="col">Língua</th>
      <th scope="col">Data e Hora</th>
      <th scope="col">Descrição</th>
      <th scope="col">Anomalia de Redação</th>
      <th scope="col"></th>
    </tr>
    <?php foreach ($anomalies as $row): ?>
      <tr>
        <th scope="row"><?=$row['id']?></th>
        <td><?=$row['zona']?></td>
        <td><?=$row['imagem']?></td>
        <td><?=$row['lingua']?></td>
        <td><?=$row['ts']?></td>
        <td><?=$row['descricao']?></td>
        <td><?=$row['tem_anomalia_redacao'] ? 'Sim' : 'Não' ?></td>
        <td><a href="./anomalies_remove.php?id=<?=$row['id']?>">Remover</a></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>