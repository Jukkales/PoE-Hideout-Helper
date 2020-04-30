mkdir input\us
mkdir input\fr
mkdir input\de
mkdir input\kr
mkdir input\br
mkdir input\ru
mkdir input\sp
mkdir input\th

pypoe_exporter dat json input/HideoutDoodads.json  --file HideoutDoodads.dat
pypoe_exporter dat json input/Hideouts.json  --file Hideouts.dat
pypoe_exporter dat json input/Pet.json  --file Pet.dat

pypoe_exporter dat json input/us/BaseItemTypes.json  --file BaseItemTypes.dat
pypoe_exporter dat json input/fr/BaseItemTypes.json  --file BaseItemTypes.dat -lang French
pypoe_exporter dat json input/de/BaseItemTypes.json  --file BaseItemTypes.dat -lang German
pypoe_exporter dat json input/kr/BaseItemTypes.json  --file BaseItemTypes.dat -lang Korean
pypoe_exporter dat json input/br/BaseItemTypes.json  --file BaseItemTypes.dat -lang Portuguese
pypoe_exporter dat json input/ru/BaseItemTypes.json  --file BaseItemTypes.dat -lang Russian
pypoe_exporter dat json input/sp/BaseItemTypes.json  --file BaseItemTypes.dat -lang Spanish
pypoe_exporter dat json input/th/BaseItemTypes.json  --file BaseItemTypes.dat -lang Thai

pypoe_exporter dat json input/us/WorldAreas.json  --file WorldAreas.dat
pypoe_exporter dat json input/fr/WorldAreas.json  --file WorldAreas.dat -lang French
pypoe_exporter dat json input/de/WorldAreas.json  --file WorldAreas.dat -lang German
pypoe_exporter dat json input/kr/WorldAreas.json  --file WorldAreas.dat -lang Korean
pypoe_exporter dat json input/br/WorldAreas.json  --file WorldAreas.dat -lang Portuguese
pypoe_exporter dat json input/ru/WorldAreas.json  --file WorldAreas.dat -lang Russian
pypoe_exporter dat json input/sp/WorldAreas.json  --file WorldAreas.dat -lang Spanish
pypoe_exporter dat json input/th/WorldAreas.json  --file WorldAreas.dat -lang Thai

pypoe_exporter dat json input/us/Music.json  --file Music.dat
pypoe_exporter dat json input/fr/Music.json  --file Music.dat -lang French
pypoe_exporter dat json input/de/Music.json  --file Music.dat -lang German
pypoe_exporter dat json input/kr/Music.json  --file Music.dat -lang Korean
pypoe_exporter dat json input/br/Music.json  --file Music.dat -lang Portuguese
pypoe_exporter dat json input/ru/Music.json  --file Music.dat -lang Russian
pypoe_exporter dat json input/sp/Music.json  --file Music.dat -lang Spanish
pypoe_exporter dat json input/th/Music.json  --file Music.dat -lang Thai
