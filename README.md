# TradeMaster Transactions
Este projeto é uma aplicação Spring Boot que gerencia transações financeiras, integrando-se a um banco de dados PostgreSQL e a um servidor RabbitMQ para processamento de mensagens.

<u> Pré-requisitos</u>

Antes de executar o projeto, certifique-se de que você possui os seguintes requisitos instalados:

- Java 21: Download JDK 21
- Docker: Instalar Docker
- Docker Compose: Instalar Docker Compose
- Maven: Instalar Maven

<u>Configuração do Ambiente</u>

Variáveis de Ambiente
O projeto utiliza variáveis de ambiente para configurar conexões com o banco de dados e o RabbitMQ. 


env
Copy
# Banco de Dados PostgreSQL
- POSTGRES_DB=trademaster
- POSTGRES_USER=eduardo
- POSTGRES_PASSWORD=eduardo

# RabbitMQ
- RABBITMQ_DEFAULT_USER=guest
- RABBITMQ_DEFAULT_PASS=guest

O Docker Compose já configura o banco de dados automaticamente e o RabbitMQ

Executando o Projeto
1. Clone o Repositório

- git clone https://github.com/eduaugusto10/trademaster.git

2. Construa o Projeto
   Se você estiver usando Maven, execute o seguinte comando para construir o projeto:

- mvn clean package

Isso gerará o arquivo transactions-0.0.1-SNAPSHOT.jar na pasta target/.

3. Execute com Docker Compose
- docker-compose up


Construirá a imagem Docker da aplicação.

Iniciará um contêiner PostgreSQL.

Iniciará um contêiner RabbitMQ.

Iniciará a aplicação Spring Boot.

4. Acesse a Aplicação
   Após a execução, a aplicação estará disponível em:

Aplicação Spring Boot: http://localhost:8080

RabbitMQ Management UI: http://localhost:15672 (usuário: guest, senha: guest)

PostgreSQL: Acessível na porta 5432

Autor: <strong>Eduardo Augusto Gomes de Oliveira</strong>