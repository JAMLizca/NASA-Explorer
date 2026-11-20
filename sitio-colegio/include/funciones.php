<?php
declare(strict_types=1);
/** Escapa un texto para mostrarlo de forma segura en HTML. */
function escapar(string $texto): string{
return htmlspecialchars($texto, ENT_QUOTES, 'UTF-8');
}
/** Lee un campo de texto de $_POST (o $_GET) y le quita espacios sobrantes. */
function leerTexto(array $origen, string $clave): string
{
$valor = $origen[$clave] ?? '';
return is_string($valor) ? trim($valor) : '';
}
/** Comprueba que el texto tenga una longitud dentro del rango (cuenta tildes bien). */
function longitudValida(string $texto, int $min, int $max): bool
{
$largo = mb_strlen($texto);
return $largo >= $min && $largo <= $max;
}
function correoValido(string $correo): bool
{
return filter_var($correo, FILTER_VALIDATE_EMAIL) !== false;
}
/** Teléfono colombiano sencillo: entre 7 y 10 dígitos (celular o fijo). */
function telefonoValido(string $telefono): bool
{
return preg_match('/^[0-9]{7,10}$/', $telefono) === 1;
}
/** Devuelve un objeto fecha si el texto tiene formato AAAA-MM-DD válido; si no, null. */
function leerFecha(string $texto): ?DateTimeImmutable
{
$fecha = DateTimeImmutable::createFromFormat('!Y-m-d', $texto);
$errores = DateTimeImmutable::getLastErrors();
if ($fecha === false || ($errores !== false && ($errores['warning_count'] > 0 || $errores['error_count'] > 0))) {
return null;
}
return $fecha;
}
/** Edad en años cumplidos a la fecha de hoy. */
function calcularEdad(DateTimeImmutable $nacimiento): int
{
return $nacimiento->diff(new DateTimeImmutable('today'))->y;
}
/** Clase CSS del enlace del menú según la página actual. */
function esPaginaActual(string $archivo): bool
{
return basename($_SERVER['SCRIPT_NAME'] ?? '') === $archivo;

/** Devuelve el HTML del mensaje de error de un campo (o texto vacío si no hay error). */
function errorCampo(array $errores, string $campo): string
{
if (!isset($errores[$campo])) {
return '';
}
return '<p class="error" id="error-' . escapar($campo) . '">' . escapar($errores[$campo]) . '</p>';
}
/**
* Evita la "inyección de fórmulas" al abrir el CSV en Excel:
* una celda que empiece por = + - @ se interpretaría como fórmula.
*/
function celdaSegura(string $valor): string
{
return preg_match('/^[=+\-@]/', $valor) === 1 ? "'" . $valor : $valor;
}
/**
 * * Guarda una fila en un archivo CSV dentro de /datos (carpeta fuera de lo público).
* Solo para práctica: use datos FICTICIOS. En un sistema real se usa base de datos.
*/
function guardarRegistro(string $archivo, array $fila): bool
{
$ruta = __DIR__ . '/../datos/' . $archivo;
$fila = array_map(fn($celda) => celdaSegura((string) $celda), $fila);
$gestor = fopen($ruta, 'ab');
if ($gestor === false) {
return false;
}
$ok = false;
if (flock($gestor, LOCK_EX)) {
$ok = fputcsv($gestor, $fila, ',', '"', '\\') !== false;
flock($gestor, LOCK_UN);
}
fclose($gestor);
return $ok;
}

/**
* Cuenta cuántas citas hay ya registradas para una fecha y franja.
* Columnas de citas.csv: 0 registro, 1 radicado, 2 acudiente, 3 teléfono, 4 correo,
* 5 tipo, 6 fecha, 7 franja, 8 niños, 9 comentarios.
*/
function citasEnFranja(string $fecha, string $franja): int
{
$ruta = __DIR__ . '/../datos/citas.csv';
if (!is_readable($ruta)) {
return 0;
}
$total = 0;
$gestor = fopen($ruta, 'rb');
if ($gestor === false) {
return 0;
}
while (($fila = fgetcsv($gestor, 0, ',', '"', '\\')) !== false) {
if (($fila[6] ?? '') === $fecha && ($fila[7] ?? '') === $franja) {
$total++;
}
}
fclose($gestor);
return $total;
}