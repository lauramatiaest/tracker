# 🎯 Tracker de Objetivos Personales

Aplicación de escritorio desarrollada en Java para gestionar y hacer seguimiento de objetivos personales de manera eficiente y organizada.

## 📋 Descripción

Esta aplicación permite a los usuarios crear, editar, eliminar y hacer seguimiento del progreso de sus objetivos personales. Incluye funcionalidades de categorización, historial de progreso y estadísticas detalladas.

## ✨ Características Principales

- **Gestión Completa de Objetivos**: Crear, editar, eliminar y actualizar objetivos
- **Categorización**: Organizar objetivos por categorías personalizables
- **Seguimiento de Progreso**: Registro detallado del avance con historial
- **Sistema de Recordatorios**: Notificaciones automáticas para objetivos próximos a vencer
- **Interfaz Intuitiva**: GUI desarrollada con Java Swing
- **Persistencia de Datos**: Almacenamiento en archivos de texto formateados
- **Filtros y Búsquedas**: Filtrar objetivos por categoría y estado
- **Estadísticas**: Métricas de progreso y análisis de rendimiento

## 💻 Tecnologías Utilizadas

- **Lenguaje**: Java 8+
- **GUI**: Java Swing
- **Arquitectura**: Patrón MVC (Modelo-Vista-Controlador)
- **Persistencia**: Archivos de texto plano (.txt)
- **IDE Recomendado**: Visual Studio Code con Extension Pack for Java

## 📁 Estructura del Proyecto
```bash
TrackerObjetivos/
├── src/
│ ├── modelo/
│ │ ├── Objetivo.java # Clase modelo para objetivos
│ │ ├── Categoria.java # Clase modelo para categorías
│ │ └── RegistroProgreso.java # Clase modelo para historial
│ ├── vista/
│ │ ├── VentanaPrincipal.java # Interfaz principal
│ │ ├── DialogoObjetivo.java # Diálogo para crear/editar objetivos
│ │ └── DialogoRecordatorios.java # Diálogo para mostrar recordatorios
│ ├── controlador/
│ │ ├── ControladorObjetivos.java # Lógica de negocio - objetivos
│ │ └── ControladorCategorias.java # Lógica de negocio - categorías
│ ├── util/
│ │ ├── GestorArchivos.java # Manejo de archivos
│ │ └── ServicioRecordatorios.java # Servicio de recordatorios
│ └── Main.java # Punto de entrada
├── datos/
│ ├── objetivos.txt # Datos de objetivos
│ ├── categorias.txt # Datos de categorías
│ └── progreso.txt # Historial de progreso
└── README.md
```

## 🚀 Requisitos del Sistema

- **Java Runtime Environment (JRE) 8+**
- **Sistema Operativo**: Windows, macOS, Linux
- **Memoria RAM**: Mínimo 512 MB
- **Espacio en Disco**: 50 MB libres

## 📦 Instalación y Configuración

1. **Instalar Java JDK**:
   ```bash
   # Windows (con Chocolatey)
   choco install openjdk11
   
   # macOS (con Homebrew)
   brew install openjdk@11
   
   # Ubuntu/Debian
   sudo apt install openjdk-11-jdk

2. **Configurar VSCode**:
    - Instalar "Extension Pack for Java"
    - Crear .vscode/settings.json:

    {
        "java.project.sourcePaths": ["src"],
        "java.project.outputPath": "build"
    }

3. **Clonar/Descargar el proyecto**:
    ```bash
    git clone [URL-del-repositorio]
    cd TrackerObjetivos

4. **Ejecutar**:
    - Abrir Main.java en VSCode
    - Presionar F5 o Ctrl+F5

## 🎮 Uso de la Aplicación
- **Funcionalidades Principales**
    Crear Objetivo:
    - Clic en "Nuevo Objetivo"
    - Llenar formulario con nombre, descripción, meta, etc.
    - Asignar categoría y prioridad

    Actualizar Progreso:
    - Seleccionar objetivo en la tabla
    - Clic en "Actualizar Progreso"
    - Ingresar nuevo valor y comentario opcional

    Gestionar Categorías:
    - Las categorías se crean automáticamente
    - Incluye: Personal, Trabajo, Salud, Educación

    Ver Historial:
    - Seleccionar objetivo
    - Clic en "Ver Historial"
    - Revisar todos los cambios de progreso
    
    Sistema de Recordatorios:
    - Notificaciones automáticas al iniciar la aplicación
    - Indicador visual de objetivos próximos a vencer
    - Clic en "Ver Recordatorios" para revisar objetivos cercanos a su fecha límite
    - Configurado para alertar sobre objetivos que vencen en los próximos 7 días

    Filtrar Objetivos:
    - Usar filtros por categoría y estado
    - Buscar objetivos específicos

- **Atajos de Teclado**
    - Ctrl+N: Nuevo objetivo
    - Ctrl+E: Editar objetivo seleccionado
    - Ctrl+U: Actualizar progreso
    - Ctrl+H: Ver historial
    - Ctrl+R: Ver recordatorios
    - F5: Actualizar vista
    - Delete: Eliminar objetivo

## 👨‍💻 Autores
- **Laura Matía Estépar**
- **Pablo Bengoechea Pardo**