# API de Pagamentos - Desafio Técnico FADESP

Este repositório contém a solução para o Desafio Técnico da FADESP. O objetivo do projeto é fornecer uma API RESTful para o recebimento e gerenciamento de status de pagamentos de débitos (pessoas físicas e jurídicas).

## Tecnologias e Dependências

* **Java 17:** Linguagem de programação.
* **Spring Boot (3.5.12):** Framework base da aplicação.
* **Spring Data JPA & Hibernate:** Mapeamento objeto-relacional (ORM) e persistência.
* **H2 Database:** Banco de dados relacional em memória.
* **Jakarta Validation:** Validação de integridade dos dados de entrada.
* **Springdoc OpenAPI (Swagger):** Documentação viva e interface gráfica para testes da API.
* **JUnit 5 & Mockito:** Suíte de testes unitários para a camada de negócios.
* **Lombok:** Redução de código boilerplate.

---

## Como Executar a Aplicação

Como o projeto utiliza o banco de dados H2 embutido, não é necessária nenhuma instalação externa.

**Passo 1. Clone o repositório:**

```bash
git clone https://github.com/luilsonbrandao/api-pagamentos.git
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

## Principais Decisões Técnicas e Arquitetura

Para garantir um código limpo (*Clean Code*), testável e de fácil manutenção, as seguintes abordagens foram aplicadas:

1. **Rich Domain Model (DDD nos Enums):** A inteligência da máquina de estados e as regras de obrigatoriedade de cartão foram encapsuladas dentro dos próprios Enums (`StatusPagamento` e `MetodoPagamento`). Isso evita que a camada de Serviço fique inflada com múltiplos `ifs`, centralizando a regra de negócio no domínio.
2. **Consultas Dinâmicas com JPA Specifications:** Em vez de utilizar consultas JPQL estáticas complexas, a busca de pagamentos foi implementada utilizando o padrão *Specification*. Isso permite a combinação dinâmica de filtros (Código do Débito, CPF/CNPJ e Status) de forma altamente escalável.
3. **Tratamento de Exceções Global (`@RestControllerAdvice`):** Captura centralizada de regras de negócio (`IllegalArgumentException`), falhas de validação (`MethodArgumentNotValidException`) e erros de parse do Jackson. Isso garante que a API sempre retorne um JSON amigável e padronizado, sem vazar *stacktraces* do servidor.
4. **Atomicidade com `@Transactional`:** Operações de escrita no banco de dados são protegidas por transações para garantir que não haja inconsistência de dados em caso de falhas sistêmicas no meio do processo.
5. **DTOs com Records e Validação na Borda:** Utilização de `records` nativos do Java 14+ para garantir imutabilidade na transferência de dados. O uso de Regex (`@Pattern`) na borda da aplicação evita que CPFs/CNPJs ou cartões inválidos cheguem à camada de serviço.
6. **Cobertura de Testes Unitários:** A camada principal de regras de negócio (`PagamentoService`) está coberta por testes unitários utilizando JUnit 5 e Mockito (padrão AAA - *Arrange, Act, Assert*), garantindo a resiliência da máquina de estados.
7. **Separação de Responsabilidades (SRP) com Mappers:** A conversão entre a Entidade de domínio e os DTOs de resposta foi isolada em um componente dedicado (`PagamentoMapper`). Isso previne que a camada de Serviço fique com múltiplas responsabilidades.

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

## End-points da API

Abaixo estão os principais recursos expostos. A documentação completa com os payloads (JSON) está disponível no Swagger.

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/pagamentos` | Recebe e registra um novo pagamento. |
| `PATCH` | `/api/pagamentos/{id}/status` | Atualiza o status seguindo a máquina de estados. |
| `GET` | `/api/pagamentos` | Lista pagamentos (aceita filtros opcionais via Query Params). |
| `DELETE` | `/api/pagamentos/{id}` | Realiza a exclusão lógica (inativação) do registro. |

> Desenvolvido por Luilson Brandão para o Desafio Técnico FADESP.