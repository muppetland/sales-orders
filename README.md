# sales-orders
Instrucciones para la utilización de la colección de APIs del ejercicio del documento “Staff Backend Software Engineer - Technical Challenge (003).pdf”

Lo que deben considerar es que las APIs son enrutadas por medio de un Gateway que apunta a la dirección localhost:6661, esta compuerta permitirá enrutar el trafico de las peticiones que se hagan de los 3 servicios que se tienen para la ejecución de este proyecto, para temas de buenas prácticas los puertos no se han expuesto de los microservicios a excepto el del servidor eureka, mysql server y api Gateway, todos los demás contenedores están ocultos, la única manera de acceder a ellos es mediante las rutas que se definieron en la colección de endpoints que se incluyen en la carpeta utils de la raíz del repositorio.

El esquema de la base de datos se construye de manera automática al realizar el primer despliegue de los microservicios, de igual manera los scripts y un diagrama sencillo del esquema de la base de datos se encuentra dentro del repositorio en la carpeta utils de la raíz del repositorio.

La versión de java que se utiliza en este proyecto es la versión 17, de igual manera el proyecto contiene comunicaciones entre los microservicios mediante webClient, se tiene documentación de las 3 instancias del proyecto, estás están apoyada con swagger y las rutas se encuentran en la colección de postman para su consulta y puedan revisar el tipo de dato que se solicita en cada endpoint, las pruebas unitarias se realizaron a nivel CRUD.

Una vez indicado lo más importante, basta con ejecutar desde su terminal el archivo “Docker-compose.yml” que se encuentra en raíz del repositorio y con eso podrán iniciar la validación del proyecto, de igual manera se incluyen algunas evidencias del despliegue del proyecto en Docker y la manera en que estás intercomunicadas y funcionando de manera correcta en base a la solicitud del documento enviado, cualquier duda o comentario estoy a la orden.


