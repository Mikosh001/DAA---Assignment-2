import csv
from collections import defaultdict
from pathlib import Path

import matplotlib.pyplot as plt


ROOT = Path(__file__).resolve().parent
CSV_FILE = ROOT / "results" / "tables" / "results.csv"
PLOT_DIR = ROOT / "results" / "plots"

PANELS = [
    ("Random access", lambda r: r["workload"] == "random_access"),
    ("Search", lambda r: r["workload"] == "search"),
    ("Insertion and removal: beginning",
     lambda r: r["workload"] == "insertion_removal" and r["operation"].endswith("beginning")),
    ("Insertion and removal: middle",
     lambda r: r["workload"] == "insertion_removal" and r["operation"].endswith("middle")),
    ("Priority processing", lambda r: r["workload"] == "priority_processing"),
]


def read_rows():
    with CSV_FILE.open(encoding="utf-8", newline="") as source:
        rows = list(csv.DictReader(source))
    for row in rows:
        row["n"] = int(row["n"])
        row["average_time_ms"] = float(row["average_time_ms"])
        row["average_metric"] = int(row["average_metric"])
    return rows


def draw(rows, value_column, y_label, title, output_name, metric=False):
    figure, axes = plt.subplots(2, 3, figsize=(15, 9))
    axes = axes.flatten()
    colors = plt.get_cmap("tab10").colors

    for panel_index, (panel_title, predicate) in enumerate(PANELS):
        axis = axes[panel_index]
        groups = defaultdict(list)
        for row in rows:
            if predicate(row):
                label = row["structure"] + " " + row["operation"].replace("_", " ")
                groups[label].append(row)
        for series_index, (label, items) in enumerate(sorted(groups.items())):
            items.sort(key=lambda item: item["n"])
            axis.plot(
                [item["n"] for item in items],
                [item[value_column] for item in items],
                marker="o",
                linewidth=1.8,
                markersize=4,
                label=label,
                color=colors[series_index % len(colors)],
            )
        axis.set_title(panel_title)
        axis.set_xscale("log")
        if metric:
            axis.set_yscale("symlog", linthresh=1)
            axis.set_ylim(bottom=0)
        else:
            axis.set_yscale("log")
        axis.set_xlabel("Initial size, n")
        axis.set_ylabel(y_label)
        axis.grid(True, which="both", linewidth=0.4, alpha=0.35)
        axis.legend(fontsize=7)

    axes[-1].axis("off")
    figure.suptitle(title, fontsize=14, fontweight="bold")
    figure.tight_layout(rect=(0, 0, 1, 0.96))
    PLOT_DIR.mkdir(parents=True, exist_ok=True)
    figure.savefig(PLOT_DIR / output_name, dpi=220, bbox_inches="tight")
    plt.close(figure)


def main():
    rows = read_rows()
    if len(rows) != 56:
        raise ValueError("Expected 56 benchmark rows, found " + str(len(rows)))
    draw(
        rows,
        "average_time_ms",
        "Average time (ms)",
        "Execution time vs. input size (average of 5 runs)",
        "time_vs_n.png",
    )
    draw(
        rows,
        "average_metric",
        "Average operation count",
        "Operations, comparisons, and accesses vs. input size",
        "operations_vs_n.png",
        metric=True,
    )
    print("Created results/plots/time_vs_n.png")
    print("Created results/plots/operations_vs_n.png")


if __name__ == "__main__":
    main()
