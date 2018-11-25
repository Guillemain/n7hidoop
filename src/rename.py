## Script pour complèté les en-tête d'un fichier par l'argument
import sys
import os

file = os.open(sys.argv[1],"r+")
file.write("/* " + sys.argv[2] + " */")
file.close()