#!/bin/bash
#@autor Guillemain
chemin=`pwd`
clear
echo "\t_________________________________________"
echo "\t| Pour deployer tout le bazard merci de |"
echo "\t|  vous mettre dans le src du projet,   |"
echo "\t|_______________________________________|"
echo ""
echo " Deploiement pour les machines : "

cd ../../..
for i in $*;
do echo -n "|\t"
echo $i;
done
#Lancement
cmpt=6000
for i in $*;
do
ssh $i "cd $chemin && java hdfs/HdfsServer $cmpt "& 
cmpt=$((($cmpt)+1))
done
echo "Deployé !"
echo ""
