# API de Pagamentos - Desafio Técnico FADESP

Este repositório contém a solução para o Desafio Técnico da FADESP. O objetivo do projeto é fornecer uma API RESTful para o recebimento e gerenciamento de status de pagamentos de débitos (pessoas físicas e jurídicas).

## Tecnologias e Dependências

* **Java 17:** Linguagem de programação.
* **Spring Boot (3.5.12):** Framework principal da aplicação.
* **Spring Data JPA & Hibernate:** Mapeamento objeto-relacional (ORM) e persistência.
* **H2 Database:** Banco de dados relacional em memória.
* **Jakarta Validation:** Validação de integridade dos dados de entrada (Regex para CPF/CNPJ e Cartões).
* **Springdoc OpenAPI (Swagger):** Documentação viva e interface gráfica para testes da API.
* **Lombok:** Redução de código boilerplate (Getters, Setters, Construtores).

---

## Como Executar a Aplicação

Como o projeto utiliza o banco de dados H2 embutido, não é necessária nenhuma instalação externa.

**Passo 1. Clone o repositório:**

```bash
git clone [https://github.com/luilsonbrandao/api-pagamentos.git](https://github.com/luilsonbrandao/api-pagamentos.git)
```
**Passo 2. Importe na sua IDE** (IntelliJ, Eclipse, VS Code) como um projeto Maven.

**Passo 3. Execute a classe principal:** `ApiPagamentosApplication.java`.

A aplicação estará rodando na porta `8080`.

### Acessando as Ferramentas Embutidas

* **Swagger UI (Testes da API):** http://localhost:8080/swagger-ui.html
* **Console do Banco H2:** http://localhost:8080/h2-console
    * *JDBC URL:* `jdbc:h2:mem:fadesp_db`
    * *User:* `sa` (sem senha)
---

## Estrutura do Banco de Dados (Tabela: `tb_pagamento`)

Para manter a simplicidade solicitada no desafio, o domínio foi modelado em uma única tabela contendo os dados da transação e o status de processamento.

| Coluna | Tipo (Java) | Tipo (SQL) | Restrição | Descrição |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | `PRIMARY KEY` | Identificador único gerado automaticamente. |
| `codigo_debito` | `Integer` | `INTEGER` | `NOT NULL` | Código identificador do débito. |
| `cpf_cnpj_pagador` | `String` | `VARCHAR` | `NOT NULL` | Documento validado por Regex (11 ou 14 dígitos). |
| `metodo_pagamento`| `Enum` | `VARCHAR` | `NOT NULL` | BOLETO, PIX, CARTAO_CREDITO ou CARTAO_DEBITO. |
| `numero_cartao` | `String` | `VARCHAR` | `NULLABLE` | Obrigatório apenas para cartão (16 dígitos). |
| `valor` | `BigDecimal`| `DECIMAL` | `NOT NULL` | Valor financeiro da transação. |
| `status` | `Enum` | `VARCHAR` | `NOT NULL` | Estado atual processado pela máquina de estados. |

---

## Principais Decisões Técnicas

* **Uso de DTOs (Data Transfer Objects) com `records`:** Em vez de expor a Entidade do banco de dados diretamente nos controllers, utilizei DTOs para garantir o desacoplamento entre a camada de apresentação e a de persistência, aproveitando a imutabilidade nativa dos records do Java.
* **Validação de Inputs na Borda:** Utilização de Expressões Regulares (Regex) via `@Pattern` no DTO para garantir que CPFs/CNPJs e Números de Cartão tenham o tamanho e os caracteres corretos antes mesmo de chegarem à camada de Serviço, evitando processamento desnecessário e chamadas inúteis ao banco de dados.
