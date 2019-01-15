#!/bin/bash
chemin=`pwd`
clear
echo "_________________________________________"
echo "| Pour deployer tout le bazard merci de |"
echo "|  vous mettre dans le src du projet,   |"
echo "|_______________________________________|"
echo ""
echo " Deploiement pour les machines : "

cd ../../..
for i in $*;
do echo -n "|\t"
echo $i;
done
#Lancement
cmpt=5000
for i in $*;
do
ssh $i "cd $chemin && java hdfs/HdfsServer $cmpt "& 
cmpt=$((($cmpt)+1))
done
echo "fini "
echo ""