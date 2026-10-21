# Microservice User

Microsserviço de **cadastro de usuários** em **Java 21 e Spring Boot**. Ao receber um novo usuário, ele salva no seu próprio banco **PostgreSQL** e publica uma mensagem no **RabbitMQ**. Quem envia o e-mail de boas-vindas é o outro serviço: o [Microservice_Email](https://github.com/GustavoSoaresbcge/Microservice_Email).

> 🐳 **Quer ver o sistema inteiro funcionando?** Use o repositório [Microservice_Build_Environment](https://github.com/GustavoSoaresbcge/Microservice_Build_Environment): com um único comando ele sobe os dois serviços, o PostgreSQL e o RabbitMQ.

## Como funciona

```mermaid
flowchart LR
    C[Cliente / Postman] -->|POST /users| U[Microservice_User :8081]
    U --- P[(PostgreSQL ms-user)]
    U -->|publica mensagem| Q[(Fila default.email)]
    Q -.->|consumida por| E[Microservice_Email]
```

O cadastro **não espera** o envio do e-mail: o serviço responde assim que salva o usuário e publica a mensagem.

## Endpoint

| Método | Rota | Corpo | Descrição |
|---|---|---|---|
| `POST` | `/users` | `{"name": "Maria", "email": "maria@exemplo.com"}` | Cadastra o usuário e publica a mensagem de boas-vindas |

`name` e `email` são obrigatórios, e o `email` precisa ter formato válido.

## Tecnologias

Java 21 · Spring Boot · Spring Data JPA · Spring AMQP · RabbitMQ · PostgreSQL · Docker · JUnit 5 · Mockito · Maven

## Rodando só este serviço (pela IDE ou pelo Maven)

Para rodar o sistema completo, o caminho mais simples é o [Microservice_Build_Environment](https://github.com/GustavoSoaresbcge/Microservice_Build_Environment). Para rodar só este serviço, você precisa de:

- Java 21 e Maven
- Um PostgreSQL com o banco `ms-user` criado (`CREATE DATABASE "ms-user";`), em `localhost:5432` com usuário `postgres`
- Um RabbitMQ acessível (local ou na nuvem, como o CloudAMQP)

As informações sensíveis vêm de **variáveis de ambiente**, e não do código:

| Variável | O que é | Exemplo |
|---|---|---|
| `DB_PASSWORD_USER` | Senha do PostgreSQL | `minha_senha` |
| `RABBITMQ_ADDRESSES_USER` | Endereço do RabbitMQ | `amqp://guest:guest@localhost:5672` |

Defina as variáveis e rode:

```bash
# Linux / macOS
export DB_PASSWORD_USER=minha_senha
export RABBITMQ_ADDRESSES_USER=amqp://guest:guest@localhost:5672
mvn spring-boot:run
```

```powershell
# Windows (PowerShell)
$env:DB_PASSWORD_USER="minha_senha"
$env:RABBITMQ_ADDRESSES_USER="amqp://guest:guest@localhost:5672"
mvn spring-boot:run
```

Na IDE, as mesmas variáveis entram em *Run Configurations → Environment*.

Para testar:

```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Maria", "email": "maria@exemplo.com"}'
```

> **Atenção:** a fila `default.email` é criada pelo **Microservice_Email** ao iniciar. Se o User publicar antes de o Email ter subido pelo menos uma vez, a mensagem se perde, porque não há fila para recebê-la. Suba o Email primeiro.

## Testes

```bash
mvn test
```

Os testes unitários usam **JUnit 5 e Mockito** e verificam, entre outras coisas, que a mensagem publicada no RabbitMQ contém os dados corretos do usuário cadastrado.

## Decisões de projeto e limitações

- **Mensageria em vez de chamada direta:** o cadastro não depende do envio do e-mail, e uma falha no e-mail não derruba este serviço.
- **Banco próprio:** este serviço é o único dono dos dados de usuários.
- **Limitação conhecida:** a mensagem é publicada dentro da transação do cadastro. Se o *commit* falhar depois da publicação, o e-mail sai para um usuário que não foi salvo. A correção planejada é publicar só depois do commit (`@TransactionalEventListener(AFTER_COMMIT)`). A solução completa seria o padrão *Transactional Outbox*.

## Próximos passos

- [ ] Publicar a mensagem só depois do commit (`AFTER_COMMIT`)
- [ ] Testes de integração com Testcontainers
- [ ] Pipeline de CI com GitHub Actions

## Autor

**Gustavo Soares** · [LinkedIn](https://www.linkedin.com/in/devgustavosoares) · [gustavosoaresbcge@gmail.com](mailto:gustavosoaresbcge@gmail.com)