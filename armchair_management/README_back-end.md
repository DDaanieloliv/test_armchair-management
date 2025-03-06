
## **Back-End**

### **Pré-requisitos Back-End**

Antes de começar, certifique-se de ter instalado:

- [Docker](https://www.docker.com/) ou [MySQL](https://www.mysql.com/).

- [Java](https://www.java.com/pt-BR/) (versão 21).

- Um editor de código, como [IntelliJ IDEA](https://www.jetbrains.com/pt-br/idea/).


---

### **1. Clone o Repositório**

Clone o repositório do projeto para o seu ambiente local:

```bash
git clone https://github.com/DDaanieloliv/test_armchair-management.git
cd test_armchair-management/armchair-management
```

---

### **2. Configure o Ambiente**

Certifique-se de que o ambiente está configurado corretamente.

#### **Usando Docker (Recomendado)**:

1. Acesse a pasta `local`:

```bash
cd test_armchair-management/armchair-management/local
```


2. Execute o comando para subir o container do MySQL:

```bash
docker compose up
```


3. Para acessar o banco de dados posteriormente:

    - Liste os containers em execução:

      ``docker ps``

    - Anote o **CONTAINER ID** ou **NOME** do banco de dados.

    - Acesse o MySQL no container:

      ``docker exec -it <CONTAINER_ID ou NOME> mysql -u root -p``

    - A senha é: `password`.

4. Comandos úteis para visualizar o banco de dados:

```sql
USE my_db;
SELECT * FROM tb_seats;
SELECT * FROM pessoa;
```


#### **Sem Docker**:

- Certifique-se de que o MySQL está instalado e rodando na sua máquina.

- Configure as credenciais do banco de dados no arquivo `application.properties` do Spring Boot.


---

### **3. Inicie o Servidor de Desenvolvimento**

1. Navegue até a pasta do projeto:

```
cd test_armchair-management/armchair-management
```

2. Execute o seguinte comando para rodar a API:

```
mvn spring-boot:run
```

3. Por padrão, o servidor estará disponível em:

```
http://localhost:8080
```


---
