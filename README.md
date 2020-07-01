# GraphiGO_backend
Backend para visualizador de ejecucuion de codigo escrito en Chocopy.

Prerequisitos:
- Java 14.0.1 o mayor
- Apache Maven 3.6.3 o mayor

Instrucciones para instalar (utilizando el IDE IntelliJ IDEA):

- Clonar el repositorio
- Marcar como sources del proyecto la carpeta AntlrGenerated
- Ejecutar la clase Main


Despues de ejecutar, el programa quedara escuchando en el puerto 4567.
Para probar que funcione se puede hacer una peticion a http://localhost:4567/hello,
la respuesta debe ser "Hello World"

Nota: Al ejecutar el programa saldra el siguiente warning que se debe ignorar:

SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".   
SLF4J: Defaulting to no-operation (NOP) logger implementation          
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
