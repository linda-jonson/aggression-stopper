SET pwd=%cd%
ECHO %pwd%

docker run -it --rm -v "%pwd%:/usr/src/mymaven" -v "%pwd%/.m2:/root/.m2" -w /usr/src/mymaven maven:3.8.1-openjdk-17-slim mvn clean package
