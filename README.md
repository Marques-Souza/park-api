# Park API

Esse projeto é uma API REST para organizar a rotina de um estacionamento. Com ela dá para cadastrar clientes e vagas, registrar a entrada e a saída dos veículos, consultar o histórico de permanências e gerar um relatório em PDF.

Fiz a API com Java e Spring Boot. A ideia foi deixar o fluxo do estacionamento bem definido: no check-in, o sistema procura uma vaga livre; no check-out, calcula o valor, aplica o desconto quando for o caso e libera a vaga.

## O que tem no projeto

- Cadastro de usuários e clientes, com validação de e-mail e CPF.
- Autenticação com JWT e acesso separado entre `ADMIN` e `USER`.
- Cadastro e consulta de vagas.
- Check-in com número de recibo e associação à primeira vaga disponível.
- Check-out com cálculo da tarifa e liberação da vaga.
- Histórico paginado por CPF ou pelo cliente autenticado.
- Relatório em PDF com o histórico do cliente.
- Testes de integração dos principais fluxos da API.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC e Spring Data JPA
- Spring Security e JWT
- PostgreSQL
- H2 para testes
- JasperReports
- Springdoc OpenAPI / Swagger
- Maven
- Docker e Docker Compose

## Como o estacionamento funciona

As principais entidades são:

- **User**: conta de acesso e perfil do usuário.
- **Client**: nome e CPF do cliente, vinculados a uma conta.
- **ParkingSpot**: código da vaga e situação (`FREE` ou `OCCUPIED`).
- **ClientParkingSpot**: registro de cada permanência, com veículo, recibo, vaga, horários, valor e desconto.

No check-in, o sistema não permite que uma placa já tenha outra permanência ativa. A vaga usada é escolhida entre as que estão livres. No check-out, a vaga volta para `FREE`.

A tarifa segue estas faixas:

- Até 15 minutos: **R$ 5,00**.
- De 16 a 60 minutos: **R$ 9,25**.
- Depois de 60 minutos: **R$ 1,75 por bloco adicional de 15 minutos**.
- A cada 10 permanências concluídas pelo cliente, é aplicado um desconto de **30%** sobre a tarifa daquela saída.

## Acesso e autenticação

O login é feito em `POST /api/v1/auth`. Se os dados estiverem corretos, a API retorna um token JWT, que deve ser enviado nas próximas chamadas:

```http
Authorization: Bearer SEU_TOKEN
```

Há dois perfis:

- `ADMIN`: gerencia vagas e faz check-in e check-out; também consulta o histórico por CPF.
- `USER`: consulta os próprios dados e o próprio histórico, além de solicitar o relatório em PDF.

O cadastro público em `POST /api/v1/users` cria contas com perfil `USER`. Para usar as operações de `ADMIN`, é necessário provisionar uma conta com esse perfil no banco de dados.

## Documentação da API

Com a aplicação em execução, a documentação interativa fica em:

[`http://localhost:8080/docs-park.html`](http://localhost:8080/docs-park.html)

## Endpoints principais

Todas as rotas abaixo usam o prefixo `/api/v1`. As rotas protegidas precisam de um token JWT válido.

| Método | Rota | Acesso | O que faz |
|---|---|---|---|
| `POST` | `/auth` | Público | Autentica e retorna um token |
| `POST` | `/users` | Público | Cria uma conta `USER` |
| `GET` | `/users` | `ADMIN` | Lista usuários |
| `GET` | `/users/{id}` | `ADMIN` ou o próprio `USER` | Consulta uma conta |
| `PATCH` | `/users/{id}/password` | `ADMIN` ou o próprio `USER` | Altera a senha |
| `POST` | `/clients` | `USER` | Cadastra os dados do cliente vinculado à conta |
| `GET` | `/clients` | Autenticado | Lista clientes de forma paginada |
| `GET` | `/clients/{id}` | Autenticado | Consulta um cliente |
| `GET` | `/clients/details` | `USER` | Consulta os dados ligados à própria conta |
| `POST` | `/parking-spots` | `ADMIN` | Cadastra uma vaga |
| `GET` | `/parking-spots/{code}` | Autenticado | Consulta uma vaga pelo código |
| `POST` | `/parkings/check-in` | `ADMIN` | Registra a entrada de um veículo |
| `GET` | `/parkings/check-in/{receipt}` | `ADMIN` ou `USER` | Consulta uma permanência ativa pelo recibo |
| `PUT` | `/parkings/check-out/{receipt}` | `ADMIN` | Registra a saída e calcula a cobrança |
| `GET` | `/parkings/cpf/{cpf}` | `ADMIN` | Consulta o histórico por CPF |
| `GET` | `/parkings/me` | `USER` | Consulta o próprio histórico |
| `GET` | `/parkings/report` | `USER` | Gera o relatório PDF do próprio cliente |

As consultas de histórico aceitam paginação, por exemplo: `/parkings/me?page=0&size=5`.

## Executando com Docker

É o jeito mais rápido de subir a API junto com o PostgreSQL. Com Docker e Docker Compose instalados, rode na raiz do projeto:

```bash
docker compose up --build
```

A API fica em `http://localhost:8080` e o banco em `localhost:5432`. Para parar os containers:

```bash
docker compose down
```

O Compose configura um banco chamado `park`, com usuário e senha `postgres`. As credenciais e o segredo JWT definidos no arquivo são para desenvolvimento local; troque esses valores antes de usar a aplicação em qualquer ambiente compartilhado ou de produção.

## Executando sem Docker

Você vai precisar de Java 21 e de um PostgreSQL em execução. Crie um banco chamado `park` e configure as variáveis no terminal antes de iniciar a aplicação. No PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/park"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET="troque-por-um-segredo-longo-e-proprio"
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS, use `export` para definir as mesmas variáveis e rode `./mvnw spring-boot:run`.

## Testes

Os testes usam H2 e cobrem autenticação, usuários, clientes, vagas e o fluxo de estacionamento. Para executá-los:

```bash
.\mvnw.cmd test
```

No Linux ou macOS:

```bash
./mvnw test
```
