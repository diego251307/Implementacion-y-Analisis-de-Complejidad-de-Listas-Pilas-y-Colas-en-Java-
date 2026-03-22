"""
graficador.py : Visualización de pruebas de estructuras de datos
Entrada : out/datos.csv  (columnas: Estructura, Metodo, N, Run, TiempoPromedio_ns)
Salida  : out/reporte.png: un gráfico con una línea por cada combinación Estructura+Metodo que aparezca en el CSV

La unidad del eje Y se elige automáticamente según el rango de los datos:
  mediana máxima < 1_000 ns  → se muestra en ns
  mediana máxima < 1_000 µs  → se muestra en µs
  si no                      → se muestra en ms
  
Así el gráfico es legible tanto si todas las pruebas son O(1) como si
hay una mezcla de O(1) y O(n) en el mismo reporte.
"""

# Importación de librerías necesarias para la manipulación de datos y la visualización
# Importante tenerlas instaladas en el entorno de Python para que el script funcione correctamente. 
# Pandas se usa para cargar y procesar el CSV, NumPy para cálculos numéricos, y Matplotlib para crear el gráfico y la tabla.
import sys
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.gridspec import GridSpec

# Diseño de la paleta de colores y el estilo global del gráfico fue desarrollado con IA
# Paleta de colores fija con 8 colores distintos.
# Se asigna un color por label (Estructura · Método) en orden de aparición.
# Si hay más de 8 labels, los colores se repiten.
COLORS = [
    "#E63946", "#457B9D", "#2A9D8F", "#E9C46A",
    "#F4A261", "#6A4C93", "#264653", "#A8DADC",
]

# Estilo global aplicado a todas las figuras.
plt.rcParams.update({
    "font.family":       "DejaVu Sans",
    "axes.spines.top":   False,
    "axes.spines.right": False,
    "figure.facecolor":  "#FAFAFA",
    "axes.facecolor":    "#FAFAFA",
    "axes.grid":         True,
    "grid.color":        "#CCCCCC",
    "grid.linestyle":    "--",
    "grid.linewidth":    0.6,
    "grid.alpha":        0.7,
})

# Carga del CSV generado por Java. Si no existe se aborta con mensaje descriptivo.
# El CSV tiene una fila por cada (Estructura, Metodo, N, Run) con el
# tiempo promedio por operación en nanosegundos.
try:
    df = pd.read_csv("out/datos.csv")
except FileNotFoundError:
    print("[Error] No se encontró out/datos.csv")
    sys.exit(1) # Si el archivo no se encuentra, se imprime un mensaje de error y se termina la ejecución del script

# Se crea la columna Label concatenando Estructura y Metodo.
# Esta etiqueta única identifica cada línea del gráfico y cada columna
# de la tabla. Por ejemplo: "SinglyNoTail · pushFront".
df["Label"] = df["Estructura"] + " · " + df["Metodo"]

# Para cada combinación (Label, N) se calculan tres estadísticos
# sobre los runs disponibles en el CSV:

#   Mediana_ns: el valor central, que evita a valores atípicos
#   Q1_ns:      primer cuartil (25% de los runs están por debajo).
#   Q3_ns:      tercer cuartil (75% de los runs están por debajo).

# La distancia entre Q1 y Q3 es el RANGO INTERCUARTIL y representa la dispersión
# de las mediciones. Un RANGO INTERCUARTIL pequeño indica mediciones estables.
df_med = (
    df.groupby(["Label", "N"])["TiempoPromedio_ns"]
    .median().reset_index().rename(columns={"TiempoPromedio_ns": "Mediana_ns"})
)
df_q1 = (
    df.groupby(["Label", "N"])["TiempoPromedio_ns"]
    .quantile(0.25).reset_index().rename(columns={"TiempoPromedio_ns": "Q1_ns"})
)
df_q3 = (
    df.groupby(["Label", "N"])["TiempoPromedio_ns"]
    .quantile(0.75).reset_index().rename(columns={"TiempoPromedio_ns": "Q3_ns"})
)

# Se unen los tres DataFrames en uno solo por Label y N para tener
# mediana, Q1 y Q3 en la misma fila y poder graficarlos juntos.
df_stats = df_med.merge(df_q1, on=["Label","N"]).merge(df_q3, on=["Label","N"])

# Selección automática de unidad según el rango de los datos.

# La idea es que los números en el eje Y y en la tabla queden
# entre 1 y 1000 en la unidad elegida, evitando valores como 0.0XXX
# o 1XXXXXXX que son difíciles de comprender para la complejidad.

# Si la mediana máxima es menor a 1000 ns, todos los valores caben
# bien expresados en nanosegundos. Si supera 1000 ns pero no llega
# a 1_000_000 ns (= 1 ms), se usan microsegundos. Si supera ese umbral
# se usan milisegundos. Esta lógica funciona tanto para benchmarks
# puramente O(1) como para mezclas de O(1) y O(n) en el mismo CSV.
max_ns = df_stats["Mediana_ns"].max()
if max_ns < 1_000:
    factor, unidad = 1,         "ns"
elif max_ns < 1_000_000:
    factor, unidad = 1_000,     "µs"
else:
    factor, unidad = 1_000_000, "ms"

# Se convierten las tres columnas de tiempo a la unidad elegida
# dividiendo por el factor correspondiente (1, 1000 o 1_000_000).
df_stats["Mediana"] = df_stats["Mediana_ns"] / factor
df_stats["Q1"]      = df_stats["Q1_ns"]      / factor
df_stats["Q3"]      = df_stats["Q3_ns"]      / factor

# Lista de labels en el orden en que aparecen en el CSV.
# Se mantiene este orden para que la leyenda y la tabla coincidan
# con el orden de PRUEBAS definido en el Main de Java.
labels = list(df_stats["Label"].unique())
ns     = sorted(df_stats["N"].unique())

# Tabla pivot: filas = valores de N, columnas = labels, celdas = mediana.
# Esta tabla se usa para construir la tabla visual debajo del gráfico.
tabla = df_stats.pivot(index="N", columns="Label", values="Mediana").reindex(ns)
tabla = tabla[labels].round(3)

# Cálculo dinámico del tamaño de la figura.
# El ancho crece con el número de columnas de la tabla para que los
# encabezados no se sobrepongan. 
n_cols_tabla = len(labels)
tabla_height = 1.0 + len(ns) * 0.28
fig_height   = 8.5 + tabla_height

# Se crea la figura con dos paneles apilados verticalmente usando GridSpec.
# Superior es grafica. Inferior es tabla.
fig = plt.figure(figsize=(max(14, n_cols_tabla * 2.2), fig_height), facecolor="#FAFAFA")
gs  = GridSpec(2, 1, figure=fig, height_ratios=[6, tabla_height], hspace=0.42)

ax_plot  = fig.add_subplot(gs[0])
ax_table = fig.add_subplot(gs[1])

# El panel de la tabla solo necesita la tabla en sí.
ax_table.axis("off")

# Dibuja una línea por cada label con su banda de dispersión IQR.
for idx, label in enumerate(labels):
    datos = df_stats[df_stats["Label"] == label].sort_values("N")
    color = COLORS[idx % len(COLORS)]

    # Línea principal con la mediana. marker="o" marca cada punto de dato medido
    ax_plot.plot(
        datos["N"], datos["Mediana"],
        marker="o", linewidth=2.4, markersize=6,
        label=label, color=color, zorder=3,
    )

    # Banda RANGO INTERCUARTÍLICA semitransparente alrededor de la línea.
    # Muestra la dispersión entre runs: una banda estrecha indica
    # mediciones estables, una banda ancha indica alta varianza.
    q1_capped = np.maximum(datos["Q1"], datos["Mediana"] * 0.5)
    q3_capped = np.minimum(datos["Q3"], datos["Mediana"] * 2.5)
    ax_plot.fill_between(
        datos["N"], q1_capped, q3_capped,
        alpha=0.15, color=color, zorder=2,
    )

# Escala logarítmica en ambos ejes.
# En escala lineal, los métodos O(n) con valores grandes "aplastan"
# las diferencias entre los métodos O(1), haciendo
# un grafico no util para comparar. 

# En escala log-log, una línea
# O(1) aparece casi horizontal, una O(n) aparece diagonal
# con pendiente alrededor de 1, y una O(n²) aparece diagonal con pendiente alrededor de 2
# para hacer mas evidente la complejidad empírica de cada método.

ax_plot.set_xscale("log")
ax_plot.set_yscale("log")

# Formato del eje X como potencias de 10 en notación matemática.
# Muestra 10^1, 10^2, etc. Son potencias de 10
ax_plot.xaxis.set_major_formatter(
    mticker.FuncFormatter(lambda x, _: f"$10^{{{int(round(np.log10(x)))}}}$")
)

# Formato del eje Y con números decimales, NO notación científica.
ax_plot.yaxis.set_major_formatter(
    mticker.FuncFormatter(lambda y, _: f"{y:g}")
)

# Márgenes verticales
y_all = df_stats["Mediana"].dropna()
if len(y_all):
    ax_plot.set_ylim(y_all.min() * 0.4, y_all.max() * 3.5)

# Título con la unidad elegida automáticamente para que sea
# claro en qué escala de tiempo se están leyendo los valores.
ax_plot.set_title(
    "Comparativa de estructuras y métodos\n"
    f"(Mediana del tiempo por operación · escala log-log · unidad: {unidad})",
    fontsize=13, fontweight="bold", pad=14,
)
ax_plot.set_xlabel("Tamaño de entrada N", fontsize=11)
ax_plot.set_ylabel(f"Tiempo por operación ({unidad})  [mediana]", fontsize=11)

# Leyenda posicionada fuera del área del gráfico hacia la derecha
ax_plot.legend(
    title="Estructura · Método", loc="upper left",
    bbox_to_anchor=(1.01, 1), borderaxespad=0, framealpha=0.9,
)

# Construcción del contenido de la tabla.
# Las etiquetas de fila muestran N como potencia de 10 (N=10^1, N=10^2...)
# para consistencia con el eje X del gráfico.
# Las celdas muestran la mediana redondeada a 3 decimales.
# Si no hay dato para una combinación como por ejemplo un método O(n) que
# no se midió para N grandes, se muestra - en vez de NaN.
row_labels = [f"N=10^{int(round(np.log10(n)))}" for n in ns]
cell_text  = []
for n_val in ns:
    row = []
    for lbl in labels:
        if lbl not in tabla.columns:
            row.append("—")
        else:
            val = tabla.at[n_val, lbl]
            row.append(f"{val:.3f}" if pd.notna(val) else "—")
    cell_text.append(row)

# Se dibuja la tabla en el panel inferior.
# bbox=[0,0,1,1] hace que ocupe todo el espacio disponible del panel.
tbl = ax_table.table(
    cellText=cell_text,
    rowLabels=row_labels,
    colLabels=labels,
    cellLoc="center",
    loc="upper center",
    bbox=[0.0, 0.0, 1.0, 1.0],
)
tbl.auto_set_font_size(False)
tbl.set_fontsize(8.5)

# scale(1, 1.6) aumenta la altura de las filas para que el texto sea mas legible
tbl.scale(1, 1.6)

# Los encabezados de columna reciben el mismo color que la línea
# correspondiente en el gráfico, para mejor relacion visual entre ambos
for j, lbl in enumerate(labels):
    cell = tbl[(0, j)]
    cell.set_facecolor(COLORS[j % len(COLORS)])
    cell.set_text_props(color="white", fontweight="bold")

# Los encabezados de fila (N=10^x) se les da fondo gris claro 
for i in range(len(ns)):
    tbl[(i + 1, -1)].set_facecolor("#E8E8E8")
    tbl[(i + 1, -1)].set_text_props(fontweight="bold")

# El subtítulo de la tabla se coloca con fig.text() en coordenadas
# de figura absolutas, justo encima del panel de la tabla.
ax_table_pos = ax_table.get_position()
fig.text(
    ax_table_pos.x0 + ax_table_pos.width / 2,
    ax_table_pos.y1 + 0.005,
    f"Mediana {unidad}/op  (tiempo promedio por operación)",
    ha="center", va="bottom", fontsize=9, color="#555",
    fontstyle="italic",
)

# Se guarda la figura como PNG se asigna dpi=200 para buena resolución
outfile = "out/reporte.png"
plt.savefig(outfile, bbox_inches="tight", dpi=200)
print(f"[OK] Guardado: {outfile}")
plt.show()
plt.close(fig)