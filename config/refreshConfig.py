import sys

Fichier = open('lunch.sh','w')     
Fichier.write('sh ../config/daemonMapDeploy.sh')
for arg in sys.argv[1:]:
    Fichier.write(' ')
    Fichier.write(arg)
Fichier.close()
##===##
Fichier = open('hdfsDeploy.sh','w')
Fichier.write('sh ../config/deploy.sh')
for arg in sys.argv[1:]:
    Fichier.write(' ')
    Fichier.write(arg)
Fichier.close()
###====###
Fichier = open('listeMachines.txt','w')
Fichier.write(sys.argv[1])
for arg in sys.argv[2:]:
    Fichier.write('\n')
    Fichier.write(arg)
Fichier.close()
###===###
Fichier = open('UNlunch.sh','w')
Fichier.write('sh ../config/undeploy.sh')
for arg in sys.argv[1:]:
    Fichier.write(' ')
    Fichier.write(arg)
Fichier.close()
    