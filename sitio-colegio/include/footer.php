</main>
<footer class="pie">
<div class="contenedor pie__rejilla">
<div>
<p class="pie__titulo"><?= escapar(NOMBRE_COLEGIO) ?></p>
<p><?= escapar(LEMA) ?>.</p>
</div>
<div>
<p class="pie__titulo">Navegación</p>
<ul class="pie__lista">
<?php foreach (MENU as $archivo => $texto): ?>
<li><a href="<?= escapar($archivo) ?>"><?= escapar($texto) ?></a></li>
<?php endforeach; ?>
</ul>
</div>
<div>
<p class="pie__titulo">Contacto</p>
<ul class="pie__lista">
<li><?= escapar(DIRECCION) ?></li>
<li>Tel. <?= escapar(TELEFONO) ?></li>
<li><?= escapar(CORREO) ?></li>
</ul>
</div>
</div>
<p class="pie__legal">
© <?= date('Y') ?> <?= escapar(NOMBRE_COLEGIO) ?> · Sitio ficticio con fines académicos.
</p>
</footer>
</body>
</html> 