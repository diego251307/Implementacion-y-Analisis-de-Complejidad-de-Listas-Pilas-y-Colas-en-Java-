# graficador.py — Visualización de benchmarks de estructuras de datos.

import sys
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.gridspec import GridSpec

COLORS = [
    "#E63946", "#457B9D", "#2A9D8F", "#E9C46A",
    "#F4A261", "#6A4C93", "#264653", "#A8DADC",
    "#FF6B6B", "#43AA8B", "#F8961E", "#577590",
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

# --- Carga de datos ---------------------------------------------------------

try:
    df = pd.read_csv("out/datos_3F.csv", dtype={"Estructura": str, "Metodo": str})
except FileNotFoundError:
    print("[Error] No se encontró out/datos.csv")
    sys.exit(1)

df = df[df["N"] > 0].dropna(subset=["N"])
df["Label"] = df["Estructura"] + " · " + df["Metodo"]

# Mediana por (Label, N).
agg = (
    df.groupby(["Label", "N"])["Tiempo_ns"]
      .median()
      .reset_index()
      .rename(columns={"Tiempo_ns": "Mediana_ns"})
)

# Unidad automática según rango de datos.
max_ns = agg["Mediana_ns"].max()
factor, unidad = (1, "ns") if max_ns < 1_000 else (1_000, "µs")

agg["Mediana"] = agg["Mediana_ns"] / factor

labels = list(agg["Label"].unique())
ns     = sorted(agg["N"].unique())

# Tabla pivot mediana por Label × N. Redondeo a 3 decimales.
tabla = (
    agg.pivot(index="N", columns="Label", values="Mediana")
       .reindex(ns)[labels].round(3)
)

# --- Layout dinámico --------------------------------------------------------

n_cols  = len(labels)
tabla_h = 1.0 + len(ns) * 0.28
fig_h   = 8.5 + tabla_h
fig_w   = max(14, n_cols * 2.2)

fig = plt.figure(figsize=(fig_w, fig_h), facecolor="#FAFAFA")
gs  = GridSpec(2, 1, figure=fig, height_ratios=[6, tabla_h], hspace=0.42)

ax_plot  = fig.add_subplot(gs[0])
ax_table = fig.add_subplot(gs[1])
ax_table.axis("off")

# --- Gráfico ----------------------------------------------------------------

for idx, label in enumerate(labels):
    datos = agg[agg["Label"] == label].sort_values("N")
    color = COLORS[idx % len(COLORS)]
    ax_plot.plot(datos["N"], datos["Mediana"],
                 marker="o", linewidth=2.4, markersize=6,
                 label=label, color=color, zorder=3)

ax_plot.set_xscale("log")
ax_plot.set_yscale("log")

ax_plot.xaxis.set_major_formatter(
    mticker.FuncFormatter(lambda x, _: f"$10^{{{int(round(np.log10(x)))}}}$"))
ax_plot.yaxis.set_major_formatter(
    mticker.FuncFormatter(lambda y, _: f"{y:g}"))

y_all = agg["Mediana"].dropna()
if len(y_all):
    ax_plot.set_ylim(y_all.min() * 0.4, y_all.max() * 3.5)

ax_plot.set_title(
    "Comparativa de estructuras y métodos\n"
    f"(Mediana del tiempo por operación · escala log-log · unidad: {unidad})",
    fontsize=13, fontweight="bold", pad=14)
ax_plot.set_xlabel("Tamaño de entrada N", fontsize=11)
ax_plot.set_ylabel(f"Tiempo por operación ({unidad})  [mediana]", fontsize=11)
ax_plot.legend(title="Estructura · Método", loc="upper left",
                bbox_to_anchor=(1.01, 1), borderaxespad=0, framealpha=0.9)

# --- Tabla ------------------------------------------------------------------

row_labels = [f"N=10^{int(round(np.log10(n)))}" for n in ns]
cell_text  = []
for n_val in ns:
    row = []
    for lbl in labels:
        val = tabla.at[n_val, lbl] if lbl in tabla.columns else np.nan
        # Cambio aquí: de :.4f a :.3f
        row.append(f"{val:.3f}" if pd.notna(val) else "—")
    cell_text.append(row)

tbl = ax_table.table(
    cellText=cell_text, rowLabels=row_labels, colLabels=labels,
    cellLoc="center", loc="upper center", bbox=[0.0, 0.0, 1.0, 1.0])
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

ax_pos = ax_table.get_position()
fig.text(ax_pos.x0 + ax_pos.width / 2, ax_pos.y1 + 0.005,
         f"Mediana {unidad}/op  (tiempo promedio por operación)",
         ha="center", va="bottom", fontsize=9, color="#555", fontstyle="italic")

# --- Guardar ----------------------------------------------------------------

outfile = "out/datos_3F.png"
plt.savefig(outfile, bbox_inches="tight", dpi=200)
print(f"[OK] Guardado: {outfile}")
plt.show()
plt.close(fig)