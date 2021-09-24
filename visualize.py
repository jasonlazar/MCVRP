#!/usr/bin/env python3

"""Visualize the routes that are contained in a file.

The user must provide the path of the routes file as a command line argument.

It is assumed that the first line of the file contains the path of the instance file.
The output file with the visualized routes is saved with the same name as the instance.
"""

import sys

import matplotlib.pyplot as plt
from matplotlib.path import Path
import matplotlib.patches as patches

colours = ["b", "r", "y", "g", "c", "m", "w"]

with open(sys.argv[1], "r") as routeFile:
    dimension = 0
    instanceName = routeFile.readline().strip()
    with open(instanceName, "r") as instanceFile:
        line = instanceFile.readline().strip()
        coords = []
        while line:
            if line.startswith("DIMENSION"):
                dimension = int(line.split(":")[1].strip())
            elif line.startswith("NODE_COORD_SECTION"):
                for _ in range(dimension):
                    line = instanceFile.readline().strip()
                    nodeCoords = list(map(int, line.split()))
                    coords.append((nodeCoords[1], nodeCoords[2]))
            line = instanceFile.readline().strip()

    fig, ax = plt.subplots()
    line = routeFile.readline()
    i = -1
    while line:
        line = line.strip()
        if line.startswith("Vehicle"):
            i += 1
            route = line.split(":")[1].strip()
            route = list(map(int, route.split("->")))
            # verts = [coords[x] for x in route]
            verts = [coords[x-1] for x in route]
            codes = [Path.LINETO for _ in route]
            codes[0] = Path.MOVETO
            codes[-1] = Path.CLOSEPOLY
            path = Path(verts, codes)
            patch = patches.PathPatch(path, edgecolor=colours[i%len(colours)], facecolor='none', lw=2)
            ax.add_patch(patch)
        line = routeFile.readline()

    Xs = [x[0] for x in coords]
    Ys = [x[1] for x in coords]
    min_x = min(Xs) * 0.95
    max_x = max(Xs) * 1.05
    min_y = min(Ys) * 0.95
    max_y = max(Ys) * 1.05

    ax.set_xlim(min_x, max_x)
    ax.set_ylim(min_y, max_y)
    ax.grid(True)
    
    datasets_index = instanceName.find("datasets")
    if datasets_index != -1:
        outfile = instanceName[datasets_index:].split("/", 1)[1].replace("/", "_")
    else:
        outfile = instanceName.rsplit("/", 1)[0]
    outfile = outfile[:outfile.rfind("vrp")] + "png"
    plt.savefig(outfile)
