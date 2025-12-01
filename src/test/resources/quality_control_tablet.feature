#language: es
#author: Stefanía Ospina

@Regresion
Característica: Navegación y trabajo en el módulo de Quality Control (Tablet)

  @QC01
  Esquema del escenario: Acceso al módulo de Quality Control en tablet
    Dado que <usuario> accede a la aplicación de Quality Management en la tablet
    Y la aplicación se ha iniciado correctamente
    Cuando ingresa al módulo de Quality Control desde el launcher
    Entonces debería visualizar la pantalla principal del módulo de Quality Control

    Ejemplos:
      | usuario |
      | "Stefa" |

  # Aquí luego puedes ir agregando más escenarios (creación de reportes, etc.)
  # usando el mismo estilo del proyecto web, pero adaptado a tablet.
