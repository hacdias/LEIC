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
  <title>Anomalias</title>
  <meta charset="UTF-8">
</head>
<body>
  <p><a href="./index.php">← Página incial</a></p>

  <form method="GET" action="./anomalies_insert.php">
    <h2>Nova Anomalia</h2>
    <input required type="text" name="zona" placeholder="Zona x1, y1, x2, y2" />
    <input required type="text" name="imagem" placeholder="Imagem (bits)" />
    <input required type="text" name="lingua" placeholder="Lingua" />
    <input required type="date" name="date" />
    <input required type="time" name="time" />
    <input required type="text" name="descricao" placeholder="Descrição" />
    <input type="checkbox" name="tem_anomalia_redacao" />
    <label for="exampleCheck1">Tem anomalia de redação</label>
    <input type="submit" value="Criar" />
  </form>

  <form method="GET">
    <h2>Anomalias entre locais públicos</h2>
    <?php
    $locals = [];
    
    try {
      $locals = getLocals();
    } catch (PDOException $e) {
      echo "<p>Não foi possível obter os locais.</p>";
      echo "<p style='color:red'>$e;</p></body></html>";
      die(1);
    } ?>

    <input type="hidden" name="type" value="locals" />

    <div>
      <label for="local1">Local 1</label>
      <select required name="local1" id="local1">
        <option selected disabled>Escolha...</option>
        <?php foreach ($locals as $row): ?>
        <option value="<?=$row['latitude']?>,<?=$row['longitude']?>"><?=$row['nome']?> (<?=$row['latitude']?>, <?=$row['longitude']?>)</option>
       <?php endforeach; ?>
      </select>
    </div>

    <div>
      <label for="local2">Local 2</label>
      <select required name="local2" id="local2">
        <option selected disabled>Escolha...</option>
        <?php foreach ($locals as $row): ?>
        <option value="<?=$row['latitude']?>,<?=$row['longitude']?>"><?=$row['nome']?> (<?=$row['latitude']?>, <?=$row['longitude']?>)</option>
       <?php endforeach; ?>
      </select>
    </div>
    <br>

    <input type="submit" value="Procurar" />
  </form>

  <form method="GET">
    <h2>Anomalias entre</h2>
    <input type="hidden" name="type" value="between" />

    <input required type="number" min="-90" max="90" name="latitude" placeholder="Latitude" />
    <input required type="number" min="-180" max="180" name="longitude" placeholder="Longitude" />

    <input required type="number" name="dlatitude" placeholder="Delta Latitude" />
    <input required type="number" name="dlongitude" placeholder="Delta Longitude" />

    <input type="submit" value="Procurar" />
  </form>

  <?php
    $type = @($_REQUEST['type']);
    $anomalies = [];

    try {
      if ($type == 'between') {
        $latitude = $_REQUEST['latitude'];
        $longitude = $_REQUEST['longitude'];
        $dlatitude = $_REQUEST['dlatitude'];
        $dlongitude = $_REQUEST['dlongitude'];
  
        echo "<h2>Anomalias em ($latitude, $longitude) ± 𝚫($dlatitude, $dlongitude) <a href='./anomalies.php'><button>Limpar</button></a></h2>";
        $anomalies = getAnomaliesAround($latitude, $longitude, $dlatitude, $dlongitude);
      } else if ($type == 'locals') {
        $local1 = explode(",", $_REQUEST['local1']);
        $local2 = explode(",", $_REQUEST['local2']);
  
        echo "<h2>Anomalias entre ($local1[0], $local1[1]) e  ($local2[0], $local2[1]) <a href='./anomalies.php'><button>Limpar</button></a></h2>";
        $anomalies = getAnomaliesBetween($local1[0], $local1[1], $local2[0], $local2[1]); 
      } else {
        $anomalies = getAnomalies();
        echo "<h2>Anomalias</h2>";
      }
    } catch (PDOException $e) {
      echo "<p>Não foi possível obter as anomalias.</p>";
      echo "<p style='color:red'>$e->getMessage();</p></body></html>";
      die(1);
    }
  ?>

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