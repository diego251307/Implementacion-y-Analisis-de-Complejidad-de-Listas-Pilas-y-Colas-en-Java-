"""
graficador.py  -  Visualización de benchmarks de estructuras de datos
Entrada : datos.csv  (columnas: Estructura, Metodo, N, Run, TiempoPromedio_ns)
Salida  : reporte_final_<Estructura>.png  (gráfico + tabla resumen)
"""

import sys
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.gridspec import GridSpec

# Paleta de colores fija: un color por método para que las líneas sean
# fáciles de distinguir incluso cuando hay muchos métodos en el mismo gráfico
COLORS = [
    "#E63946", "#457B9D", "#2A9D8F", "#E9C46A",
    "#F4A261", "#6A4C93", "#264653", "#A8DADC",
]

# Estilo global de todas las figuras: fondo claro, sin bordes superiores ni
# derechos (menos ruido visual), y grilla punteada gris para guiar el ojo
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

# Intentamos cargar el CSV generado por Java. Si no existe, no hay nada que graficar
try:
    df = pd.read_csv("datos.csv")
except FileNotFoundError:
    print("[Error] No se encontró datos.csv")
    sys.exit(1)

# Los tiempos vienen en nanosegundos desde Java. Los convertimos a microsegundos
# porque los números son más legibles en la tabla (ej. 0.045 µs en vez de 45 ns)
df["TiempoPromedio_us"] = df["TiempoPromedio_ns"] / 1000.0

# Calculamos la mediana por grupo (Estructura, Metodo, N).
# Usamos la mediana en lugar de la media porque es más resistente a los picos
# ocasionales que genera el GC de Java o el scheduler del sistema operativo
df_med = (
    df.groupby(["Estructura", "Metodo", "N"])["TiempoPromedio_us"]
    .median()
    .reset_index()
    .rename(columns={"TiempoPromedio_us": "Mediana_us"})
)

# También calculamos el primer y tercer cuartil para dibujar la banda de
# dispersión alrededor de cada línea. Esa banda muestra qué tan estables
# fueron las mediciones entre los distintos runs
df_q1 = (
    df.groupby(["Estructura", "Metodo", "N"])["TiempoPromedio_us"]
    .quantile(0.25).reset_index().rename(columns={"TiempoPromedio_us": "Q1"})
)
df_q3 = (
    df.groupby(["Estructura", "Metodo", "N"])["TiempoPromedio_us"]
    .quantile(0.75).reset_index().rename(columns={"TiempoPromedio_us": "Q3"})
)

# Unimos mediana, Q1 y Q3 en un solo DataFrame para tener todo junto al graficar
df_stats = df_med.merge(df_q1, on=["Estructura", "Metodo", "N"]).merge(df_q3, on=["Estructura", "Metodo", "N"])

# Generamos un gráfico independiente por cada estructura que aparezca en el CSV.
# Así si corriste solo Stack y Queue, no aparece nada de List, y viceversa
for est in df_stats["Estructura"].unique():

    sub     = df_stats[df_stats["Estructura"] == est]
    metodos = sorted(sub["Metodo"].unique())
    ns      = sorted(sub["N"].unique())

    # Construimos la tabla pivot que irá debajo del gráfico.
    # Filas = valores de N, columnas = métodos, celdas = mediana en µs
    tabla = sub.pivot(index="N", columns="Metodo", values="Mediana_us").reindex(ns)
    tabla = tabla.round(3)

    # La altura de la figura crece con el número de filas de la tabla para que
    # las celdas no queden aplastadas cuando hay muchos tamaños N distintos
    n_cols_tabla = len(metodos)
    tabla_height = 1.0 + len(ns) * 0.28
    fig_height   = 8.5 + tabla_height

    fig = plt.figure(figsize=(max(14, n_cols_tabla * 1.8), fig_height), facecolor="#FAFAFA")

    # hspace para que el xlabel del gráfico no se pise con la tabla
    gs = GridSpec(2, 1, figure=fig,
                  height_ratios=[6, tabla_height],
                  hspace=0.42)

    ax_plot  = fig.add_subplot(gs[0])
    ax_table = fig.add_subplot(gs[1])
    ax_table.axis("off")

    # Línea por método con sus puntos y su banda IQR
    for idx, metodo in enumerate(metodos):
        datos = sub[sub["Metodo"] == metodo].sort_values("N")
        color = COLORS[idx % len(COLORS)]

        ax_plot.plot(
            datos["N"], datos["Mediana_us"],
            marker="o", linewidth=2.4, markersize=6,
            label=metodo, color=color, zorder=3,
        )

        # La banda IQR se capea para que en N pequeño (donde la varianza es
        # alta por tener pocos datos) no explote y tape las demás líneas
        q1_capped = np.maximum(datos["Q1"], datos["Mediana_us"] * 0.5)
        q3_capped = np.minimum(datos["Q3"], datos["Mediana_us"] * 2.5)
        ax_plot.fill_between(
            datos["N"], q1_capped, q3_capped,
            alpha=0.15, color=color, zorder=2,
        )

    # Escala log-log: imprescindible para comparar métodos O(1) con O(n).
    # En escala lineal la diferencia aplasta todo y no se ve nada útil
    ax_plot.set_xscale("log")
    ax_plot.set_yscale("log")

    # Mostramos los ticks del eje X como 10^k para que quede claro que
    # son potencias de 10 y no números intermedios raros
    ax_plot.xaxis.set_major_formatter(
        mticker.FuncFormatter(lambda x, _: f"$10^{{{int(round(np.log10(x)))}}}$")
    )

    # El eje Y en formato decimal plano evita la notación científica que
    # dificulta comparar valores a simple vista
    ax_plot.yaxis.set_major_formatter(
        mticker.FuncFormatter(lambda y, _: f"{y:g}")
    )

    # Damos un margen vertical para que las líneas no queden pegadas al borde
    y_all = sub["Mediana_us"].dropna()
    if len(y_all):
        ax_plot.set_ylim(y_all.min() * 0.4, y_all.max() * 3.5)

    ax_plot.set_title(
        f"Análisis de Complejidad — {est}\n"
        f"(Mediana del tiempo por operación · escala log-log)",
        fontsize=13, fontweight="bold", pad=14,
    )
    ax_plot.set_xlabel("Tamaño de entrada N", fontsize=11)
    ax_plot.set_ylabel("Tiempo por operación (µs)  [mediana]", fontsize=11)

    # Leyenda fuera del área del gráfico para no tapar las líneas
    ax_plot.legend(
        title="Método", loc="upper left",
        bbox_to_anchor=(1.01, 1), borderaxespad=0, framealpha=0.9,
    )

    # Armamos el texto de cada celda de la tabla, mostrando "—" cuando
    # no hay dato para esa combinación de método y N (ej. métodos O(n)
    # que se saltaron N grandes para no tardar horas)
    row_labels = [f"N=10^{int(round(np.log10(n)))}" for n in ns]
    cell_text  = []
    for n_val in ns:
        row = []
        for m in metodos:
            if m not in tabla.columns:
                row.append("—")
            else:
                val = tabla.at[n_val, m]
                row.append(f"{val:.3f}" if pd.notna(val) else "—")
        cell_text.append(row)

    tbl = ax_table.table(
        cellText=cell_text,
        rowLabels=row_labels,
        colLabels=metodos,
        cellLoc="center",
        loc="upper center",
        bbox=[0.0, 0.0, 1.0, 1.0],
    )
    tbl.auto_set_font_size(False)
    tbl.set_fontsize(8.5)
    tbl.scale(1, 1.6)


    for j, metodo in enumerate(metodos):
        cell = tbl[(0, j)]
        cell.set_facecolor(COLORS[j % len(COLORS)])
        cell.set_text_props(color="white", fontweight="bold")


    for i in range(len(ns)):
        tbl[(i + 1, -1)].set_facecolor("#E8E8E8")
        tbl[(i + 1, -1)].set_text_props(fontweight="bold")


    ax_table_pos = ax_table.get_position()
    fig.text(
        ax_table_pos.x0 + ax_table_pos.width / 2,
        ax_table_pos.y1 + 0.005,
        "Mediana µs/op  (tiempo promedio por operación)",
        ha="center", va="bottom", fontsize=9, color="#555",
        fontstyle="italic",
    )

    outfile = f"reporte_final_{est}.png"
    plt.savefig(outfile, bbox_inches="tight", dpi=200)
    print(f"[OK] Guardado: {outfile}")
    plt.show()
    plt.close(fig)