# StreamFIAP — Sistema de Streaming

Sistema de gerenciamento de conteúdos e aluguéis para plataforma de streaming desenvolvido em Java Spring Boot com JPA.

---

## 👥 Integrantes do Grupo

| Nome Completo | RM |
| :--- | :--- |
| Gabriel Jorge Coutinho | 565441 |

---

## 🐛 Parte 1: Tabela de Bugs Corrigidos

| ID | Sintoma (O que falhava) | Causa Raiz | Correção Realizada | Conceito de POO / Java |
| :--- | :--- | :--- | :--- | :--- |
| **BUG01** | Busca por ID inexistente retornava erro genérico ou `null`. | Ausência de lançamento de exceção específica no repositório. | Lançada `ConteudoNaoEncontradoException` no `orElseThrow`. | Tratamento de Exceções Unchecked |
| **BUG02** | Busca de usuário por ID inexistente causava erro de servidor 500. | Falta de tratamento da busca do usuário na camada de Controller/Service. | Mapeada a exceção `UsuarioNaoEncontradoException`. | Exceções de Domínio / Spring MVC |
| **BUG03** | Cadastro de conteúdo permitia salvar campos obrigatórios nulos ou em branco. | Ausência de validação nos setters e construtores das entidades de modelo. | Criados métodos estáticos de validação no modelo lançando `DadosInvalidosException`. | Encapsulamento e Invariantes de Classe |
| **BUG04** | Aluguel permitia usuário menor de idade alugar conteúdo restrito. | Falta de verificação entre `usuario.getIdade()` e `conteudo.getClassificacaoEtaria()`. | Criada validação dedicada lançando `ClassificacaoIndicativaException`. | Regras de Negócio e Controle de Acesso |
| **BUG05** | Aluguel permitia alugar conteúdo já indisponível (`disponivel = false`). | Falta de verificação do status de disponibilidade antes de efetuar o débito. | Adicionada verificação lançando `ConteudoIndisponivelException`. | Estado do Objeto e Consistência |
| **BUG06** | Aluguel debitava saldo de usuário com créditos insuficientes. | Método de débito não validava se o saldo era menor que o preço do aluguel. | Criada validação do saldo lançando `CreditosInsuficientesException`. | Encapsulamento / Invariantes de Estado |
| **BUG07** | Valor do aluguel podia ser debitado com número negativo ou zero. | Método `debitarCreditos` aceitava parâmetros negativos. | Adicionada trava no método `debitarCreditos` lançando `DadosInvalidosException`. | Defesa em Profundidade / Validação |
| **BUG08** | Séries permitiam ser criadas com número de temporadas igual a zero ou negativo. | Construtor e setter de `Serie` não validavam o intervalo numérico. | Adicionada trava `numeroTemporadas <= 0` lançando `DadosInvalidosException`. | Invariantes de Domínio |
| **BUG09** | Documentário aceitava criação com tema nulo ou apenas com espaços em branco. | Setter de `Documentario` não tratava `isBlank()`. | Adicionada verificação no setter lançando `DadosInvalidosException`. | Validação de Dados de Entrada |
| **BUG10** | Status HTTP no cadastro de usuários retornava 200 OK em vez de 201 Created. | Uso do padrão `ResponseEntity.ok()` na rota POST do controller. | Alterado para `ResponseEntity.status(HttpStatus.CREATED)`. | Padronização REST / HTTP API |
| **BUG11** | Status HTTP no cadastro de filmes retornava id do cliente. | Instâncias salvas reaproveitavam estado recebido sem recriar objeto limpo. | Recriada nova instância no Controller para persistência limpa pelo JPA. | Persistência JPA e Idempotência |
| **BUG12** | Exceções de aluguel retornavam erro 500 para o cliente. | Ausência de um manipulador global de exceções na aplicação. | Criado `GlobalExceptionHandler` mapeando status HTTP (400, 403, 404, 409, 422). | Spring `@RestControllerAdvice` |

---

## 🧹 Parte 2: Ajustes de Clean Code

| ID | Código / Local | Violação de Clean Code | Refatoração Realizada |
| :--- | :--- | :--- | :--- |
| **CC01** | `AluguelController` | Violação de SRP (Controller com lógica de negócio e validações inline). | Extraída toda a lógica para o serviço `AluguelService`. |
| **CC02** | `AluguelService` | Método "Faz-Tudo" (`alugar` acumulava múltiplas validações). | Decomposto em métodos privados com responsabilidade única (`validarClassificacao`, `validarDisponibilidade`, `validarCreditos`). |
| **CC03** | `Controllers` | Injeção de dependência via campo (`@Autowired`). | Substituída por Injeção de Dependência via Construtor (`final` fields). |
| **CC04** | `GlobalExceptionHandler` | Código duplicado na criação de respostas de erro JSON. | Criado método privado auxiliar `respostaErro(HttpStatus, String)`. |
| **CC05** | `Conteudo` | Repetição de código de validação de textos nas subclasses. | Criado método utilitário estático `validarTexto` na classe pai. |
| **CC06** | `UsuarioController` | Uso de código HTTP numérico hardcoded (`status(201)`). | Alterado para enum expressivo do Spring `HttpStatus.CREATED`. |

---

## ❓ Parte 3: Perguntas de Reflexão

### 1. Por que as exceções do sistema foram implementadas como Unchecked Exceptions (`RuntimeException`) em vez de Checked Exceptions?
As exceções unchecked foram escolhidas porque representam violações de regras de negócio ou de contrato de API que impedem o prosseguimento da requisição. Em arquiteturas Spring Boot modernas, o uso de `RuntimeException` permite que os erros fluam limpos até a camada do `@RestControllerAdvice`, onde são capturados e convertidos em respostas HTTP estruturadas sem poluir as assinaturas dos métodos da camada de serviço e do controller com cláusulas `throws`.

### 2. Qual é a vantagem de mover a lógica de aluguel e validação do Controller para uma classe de serviço dedicada (`AluguelService`)?
A principal vantagem é a aderência ao Princípio da Responsabilidade Única (SRP). A camada de Controller deve se preocupar apenas com a infraestrutura Web (receber a requisição, mapear DTOs/parâmetros e retornar o status HTTP adequado). Transferir a regra de negócio e as validações de domínio para o `AluguelService` permite reutilizar a lógica de aluguel em outros pontos do sistema, facilita a escrita de testes unitários sem necessidade do contexto web e isola o domínio de alterações de infraestrutura.

### 3. Por que a Injeção de Dependência por Construtor é preferível em relação à injeção direta nos atributos (`@Autowired` em campo)?
A injeção por construtor garante a imutabilidade das dependências ao declarar os atributos como `final`, impedindo reatribuições acidentais durante a execução. Além disso, ela facilita o teste unitário isolado sem a necessidade de reflexão ou de inicializar o contexto do Spring, e impede a instanciação de objetos em estado inconsistente (com dependências nulas).

### 4. Como a aplicação do polimorfismo na classe `Conteudo` e na interface `Promocionavel` simplifica a adição de novos tipos de mídia no sistema?
O polimorfismo permite que o sistema trate subclasses como `Filme`, `Serie` e `Documentario` por meio da abstração `Conteudo`. Métodos como `calcularPrecoPromocional` executam o comportamento correto sem a necessidade de estruturas condicionais `if/else` ou `switch/case` complexas. Para adicionar um novo tipo de conteúdo (ex.: `Podcast`), basta herdar de `Conteudo` e implementar suas regras específicas, sem alterar a lógica de controllers e serviços existentes (Princípio do Aberto/Fechado - OCP).

### 5. Qual o papel das validações nos métodos setters e construtores do modelo na prevenção de bugs?
As validações diretas no modelo (defensa em profundidade) garantem que nenhum objeto da aplicação nasça ou assuma um estado inválido no banco de dados. Ao checar campos obrigatórios, valores numéricos negativos ou cadeias de caracteres em branco no momento da atribuição, o sistema falha rapidamente (*fail-fast*), impedindo que dados corrompidos propaguem para o repositório ou gerem comportamentos inesperados em cálculos futuros.

### 6. Como a padronização das respostas de erro com o `GlobalExceptionHandler` impacta a integração com sistemas clientes (Front-end/Mobile)?
A centralização do tratamento de exceções no `GlobalExceptionHandler` garante que todas as falhas da API sigam um contrato REST previsível e uniforme (retornando um JSON padronizado com o campo `"erro"` e o código HTTP apropriado). Isso evita o vazamento de stacktraces internas do Java para o cliente e simplifica a camada de integração no front-end, que pode tratar erros de forma genérica e amigável para o usuário final.

---

## 🛠️️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3** (Spring Data JPA, Spring Web)
- **Jakarta Persistence (JPA)** / Hibernate
- **Database:** Oracle / H2 Database
- **Maven**