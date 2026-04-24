# SportConnect

Aplicación móvil Android para la gestión y reserva de actividades deportivas.
Conecta a usuarios interesados en actividades físicas con profesionales del deporte que las ofrecen.

## Descripción

SportConnect permite:
- A los **usuarios básicos**: buscar, filtrar y reservar actividades deportivas.
- A los **profesionales**: publicar y gestionar sus actividades.
- Al **administrador**: supervisar usuarios y actividades del sistema.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje de programación principal |
| Android Studio | Entorno de desarrollo |
| Firebase Authentication | Gestión de usuarios y roles |
| Firebase Firestore | Base de datos en tiempo real |
| Material Design | Componentes de interfaz de usuario |
| MVVM | Patrón arquitectónico |
| Coroutines | Operaciones asíncronas |
| ViewBinding | Acceso seguro a las vistas XML |

## Instalación y ejecución

1. Clona el repositorio: `git clone https://github.com/RaquelPalomo/SportConnect.git`
2. Abre el proyecto en **Android Studio**
3. Añade el archivo `google-services.json` en la carpeta `app/` (no incluido por seguridad)
4. Pulsa **Run ▶** para compilar y ejecutar

## Estructura del proyecto

    app/src/main/java/com/example/sportconnect/
    ├── data/
    │   ├── model/          → Clases de datos (Usuario, Actividad, Reserva)
    │   └── repository/     → Acceso a Firebase (Auth, Actividad, Profesional, Admin)
    ├── ui/
    │   ├── auth/           → Login y Registro
    │   ├── basic/          → Pantallas usuario básico
    │   ├── professional/   → Pantallas usuario profesional
    │   └── admin/          → Panel de administración
    ├── viewmodel/          → Lógica de negocio y estado de UI
    └── utils/              → Utilidades y constantes

## Roles de usuario

| Rol | Funcionalidades |
|---|---|
| Básico | Buscar, filtrar y reservar actividades |
| Profesional | Crear y gestionar actividades propias |
| Master | Supervisar usuarios y actividades |

## Requisitos

- Android 8.0 (API 26) o superior
- Conexión a internet

## Autora

**Raquel Palomo Muñoz**  
Tutor: Carlos Rufiángel García  
Ciclo: Desarrollo de Aplicaciones Multiplataforma (DAM)  
Año: 2026