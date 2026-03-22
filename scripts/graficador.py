"""
graficador.py  -  Visualización de benchmarks de estructuras de datos
Entrada : out/datos.csv  (columnas: Estructura, Metodo, N, Run, TiempoPromedio_ns)
Salida  : out/reporte.png  — un gráfico con una línea por cada combinación
          Estructura+Metodo que aparezca en el CSV
"""

import sys
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.gridspec import GridSpec

COLORS = [
    "#E63946", "#457B9D", "#2A9D8F", "#E9C46A",
    "#F4A261", "#6A4C93", "#264653", "#A8DADC",
]

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

try:
    df = pd.read_csv("out/datos.csv")
except FileNotFoundError:
    print("[Error] No se encontró out/datos.csv")
    sys.exit(1)

df["TiempoPromedio_us"] = df["TiempoPromedio_ns"] / 1000.0

# Creamos una etiqueta única por combinación Estructura+Metodo
# Esa etiqueta es lo que se muestra en la leyenda y en la tabla
df["Label"] = df["Estructura"] + " · " + df["Metodo"]

df_med = (
    df.groupby(["Label", "N"])["TiempoPromedio_us"]
    .median().reset_index().rename(columns={"TiempoPromedio_us": "Mediana_us"})
)
df_q1 = (
    df.groupby(["Label", "N"])["TiempoPromedio_us"]
    .quantile(0.25).reset_index().rename(columns={"TiempoPromedio_us": "Q1"})
)
df_q3 = (
    df.groupby(["Label", "N"])["TiempoPromedio_us"]
    .quantile(0.75).reset_index().rename(columns={"TiempoPromedio_us": "Q3"})
)
df_stats = df_med.merge(df_q1, on=["Label","N"]).merge(df_q3, on=["Label","N"])

labels = list(df_stats["Label"].unique())  # orden de aparición en el CSV
ns     = sorted(df_stats["N"].unique())

tabla = df_stats.pivot(index="N", columns="Label", values="Mediana_us").reindex(ns)
tabla = tabla[labels].round(3)  # mantiene el orden de las columnas

n_cols_tabla = len(labels)
tabla_height = 1.0 + len(ns) * 0.28
fig_height   = 8.5 + tabla_height

fig = plt.figure(figsize=(max(14, n_cols_tabla * 2.2), fig_height), facecolor="#FAFAFA")
gs  = GridSpec(2, 1, figure=fig, height_ratios=[6, tabla_height], hspace=0.42)

ax_plot  = fig.add_subplot(gs[0])
ax_table = fig.add_subplot(gs[1])
ax_table.axis("off")

for idx, label in enumerate(labels):
    datos = df_stats[df_stats["Label"] == label].sort_values("N")
    color = COLORS[idx % len(COLORS)]

    ax_plot.plot(
        datos["N"], datos["Mediana_us"],
        marker="o", linewidth=2.4, markersize=6,
        label=label, color=color, zorder=3,
    )

    q1_capped = np.maximum(datos["Q1"], datos["Mediana_us"] * 0.5)
    q3_capped = np.minimum(datos["Q3"], datos["Mediana_us"] * 2.5)
    ax_plot.fill_between(
        datos["N"], q1_capped, q3_capped,
        alpha=0.15, color=color, zorder=2,
    )

ax_plot.set_xscale("log")
ax_plot.set_yscale("log")

ax_plot.xaxis.set_major_formatter(
    mticker.FuncFormatter(lambda x, _: f"$10^{{{int(round(np.log10(x)))}}}$")
)
ax_plot.yaxis.set_major_formatter(
    mticker.FuncFormatter(lambda y, _: f"{y:g}")
)

y_all = df_stats["Mediana_us"].dropna()
if len(y_all):
    ax_plot.set_ylim(y_all.min() * 0.4, y_all.max() * 3.5)

ax_plot.set_title(
    "Comparativa de estructuras y métodos\n(Mediana del tiempo por operación · escala log-log)",
    fontsize=13, fontweight="bold", pad=14,
)
ax_plot.set_xlabel("Tamaño de entrada N", fontsize=11)
ax_plot.set_ylabel("Tiempo por operación (µs)  [mediana]", fontsize=11)
ax_plot.legend(
    title="Estructura · Método", loc="upper left",
    bbox_to_anchor=(1.01, 1), borderaxespad=0, framealpha=0.9,
)

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
tbl.scale(1, 1.6)

for j, lbl in enumerate(labels):
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

outfile = "out/reporte.png"
plt.savefig(outfile, bbox_inches="tight", dpi=200)
print(f"[OK] Guardado: {outfile}")
plt.show()
plt.close(fig)