#!/bin/bash
echo "El siguiente script utiliza herramientas java, tanto para"
echo "la generacion de claves publica y privada, como para su procesamiento."

echo ""
echo "Scripts para Manejo de claves en Sinapuli: "
echo "_________________________________________"
echo ""


genKeystore(){

	echo -n "Ingrese el nombre de nueva su keystore: "
	read keystoreName
	keytool -genkeypair -alias sinapuli -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -keystore "$keystoreName.jks"

}


hashDocument(){

	echo -n "Ingrese el nombre del documento: "
	read docPath
	echo -n "Ingrese el nombre del archivo de salida: "
	read outputPath

	java HashDocument "$docPath" "$outputPath"		

}

checkHashDocument(){

	echo -n "Ingrese el nombre del documento: "
	read docPath
	echo -n "Ingrese el nombre del archivo con el hash: "
	read outputPath

	java CheckHashDocument "$docPath" "$outputPath"
	

}

genSig(){

	echo -n "Ingrese el nombre del archivo a firmar: "
	read nameOfFileToSign
	echo -n "Ingrese el nombre de su archivo keystore: "
	read keystore
	echo -n "Ingrese la password para el keystore: "
	read password
	echo -n "Ingrese el nombre del archivo de salida para la firma: "
	read sign
	echo -n "Ingrese el nombre del archivo de salida para su clave publica: "
	read publicKey

	java GenSig "$nameOfFileToSign" "$keystore" "$password" "$sign" "$publicKey"
	

}

verSig(){

	echo -n "Ingrese el nombre del archivo con la clave publica: "
	read publickeyfile
	echo -n "Ingrese el nombre del archivo firmado: "
	read signaturefile
	echo -n "Ingrese el nombre del archivo original a verificar: "
	read datafile

	java VerSig "$publickeyfile" "$signaturefile" "$datafile"

}

PS3="Seleccione una opcion: "
options=(
"Quiero generar mi keystore" 
"Quiero Hashear un documento" 
"Quiero comparar un documento con un hash" 
"Quiero firmar un documento" 
"Quiero verificar la firma de un documento" 
"Salir")


select opt in "${options[@]}"
do
    case $opt in
        "Quiero generar mi keystore" )
            echo "eligio la opcion 1"
            genKeystore ;;
        "Quiero Hashear un documento" )
            echo "eligio la opcion 2"
	    hashDocument ;;
        "Quiero comparar un documento con un hash" )
            echo "eligio la opcion 3"
            checkHashDocument ;;
        "Quiero firmar un documento" )
            echo "eligio la opcion 4"
            genSig ;;
        "Quiero verificar la firma de un documento" )
            echo "eligio la opcion 5"
            verSig ;;
        "Salir")
            break
            ;;
        *) echo invalid option;;
    esac
done