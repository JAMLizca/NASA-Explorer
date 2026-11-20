<?php
declare(stric_types=1);

// Datos generales del colegio

const NOMBRE_COLEGIO = 'Institución Educativa Riverita';
const LEMA = 'Por un futuro mejor';
const TELEFONO = '+57 320876543';
const CORREO = 'institucionriverita@edu.co';
const DIRECCION = 'Vereda Riverita, Huila,Rivera';

// Menú principal
const MENU = [
    'index.php' => 'Inicio',
    'nosotros.php' => 'Nosotros',
    'experiencias.php' => 'Experiencias',
    'prematricula.php' => 'Pre-matricula',
    'agenda.php' => 'Agenda tu cita',
];

// Listas usadas por los formularios
const GRADOS = [
'jardin' => 'Jardín (4 años)',
'transicion' => 'Transición (5 años)',
'1' => 'Primero', '2' => 'Segundo', '3' => 'Tercero',
'4' => 'Cuarto', '5' => 'Quinto', '6' => 'Sexto', '7' => 'Septimo','8' => 'Octavo','9' => 'Noveno','10' => 'Decimo','11' => 'Once',
];
const PARENTESCOS = ['Madre', 'Padre', 'Abuela/o', 'Tía/o', 'Acudiente legal'];
const TIPOS_CITA = [
'visita' => 'Visita guiada al colegio',
'entrevista' => 'Entrevista de admisión',
'reunion' => 'Reunión informativa',
];
const CITAS_POR_FRANJA = 2; // cupo máximo de citas por fecha y hora
const FRANJAS = [
'08:00' => '8:00 a. m.',
'09:30' => '9:30 a. m.',
'11:00' => '11:00 a. m.',
'14:00' => '2:00 p. m.',
'15:30' => '3:30 p. m.',
];
?>