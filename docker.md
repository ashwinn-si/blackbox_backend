# THIS IS THE CMDS FOR DOCKER EXECUTION

## BACKEND CMDS FOR BUILDING AND PUBLISHING A IMAGE

### Building the JAR
``mvn clean package -DskipTests``

### Build Docker Image
``docker build -t [yourname]/blackbox-backend:v<1>.00 .``

### Login to Docker
``docker login``

### Push to Docker
``docker push yourname/blackbox-backend:v<1>.00``


## FRONTEND CMDS TO PULL A IMAGE

### Pulling the image
``docker pull yourname/blackbox-backend:v<1>.00``

### NEED TO CREATED THE ``docker-compose.yml`` file
After setting up run
``docker compose up``



## UPDATE FOLLOW
### For Backend
``mvn clean package -DskipTests ``

`docker build -t yourname/blackbox-backend:v<1>.00 .`

`docker push yourname/blackbox-backend:v<1>.00`

### For Frontend
`docker pull yourname/blackbox-backend:v<1>.00`

`docker compose up -d`


## OTHER IMPORTANT COMMANDS
`docker compose down`

`docker compose up -d --build
`