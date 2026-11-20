<?php
declare(strict_types=1);
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/funciones.php';
// Cada página define $titulo antes de incluir este archivo.
$titulo = $titulo ?? 'Inicio';
?>

<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><?= escapar($titulo) ?> | <?= escapar(NOMBRE_COLEGIO) ?></title>
<meta name="description" content="<?= escapar(NOMBRE_COLEGIO) ?>: preescolar, básica primaria y secundaria. <?= escapar(LEMA) ?>.">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Baloo+2:wght@600;700;800&family=Nunito:wght@400;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="css/style.css">
<script src="js/app.js" defer></script>
</head>
<body>
<a class="saltar" href="#contenido">Saltar al contenido</a>
<header class="cabecera">
<div class="contenedor cabecera__barra">
<a class="marca" href="index.php" aria-label="<?= escapar(NOMBRE_COLEGIO) ?> - Inicio">
<img src="img/logo.svg" alt="" width="44" height="44">
<span><?= escapar(NOMBRE_COLEGIO) ?></span>
</a>
<button class="menu-boton" id="menuBoton" aria-expanded="false" aria-controls="menuPrincipal">
<span class="menu-boton__texto">Menú</span>
<span class="menu-boton__icono" aria-hidden="true"></span>
</button>
<nav class="menu" id="menuPrincipal" aria-label="Principal">
<ul>
<?php foreach (MENU as $archivo => $texto): ?>
<li>
<a href="<?= escapar($archivo) ?>"
class="<?= $archivo === 'agenda.php' ? 'menu__cta' : '' ?>"
<?= esPaginaActual($archivo) ? 'aria-current="page"' : '' ?>>
<?= escapar($texto) ?>
</a>
</li>
<?php endforeach; ?>
</ul>
</nav>
</div>
</header>
<main id="contenido">