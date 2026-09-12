# StreamFIAP

Sistema de gerenciamento de conteúdos para uma plataforma de streaming desenvolvido em Java, utilizando Programação Orientada a Objetos (POO) e Jakarta Persistence (JPA).

## Descrição

O projeto permite o gerenciamento de diferentes tipos de conteúdo disponíveis na plataforma, aplicando regras específicas para cálculo de aluguel, promoções e validação de dados.

## Funcionalidades

- Cadastro de filmes e documentários
- Cálculo de preço de aluguel
- Aplicação de promoções
- Validação de dados de entrada
- Persistência de dados com JPA

---

## Estrutura do Projeto

### Filme

A classe `Filme` representa filmes disponíveis na plataforma.

#### Características

- Herda da classe `Conteudo`
- Implementa a interface `Promocionavel`
- Possui indicador de estreia
- Calcula automaticamente o preço do aluguel
- Permite aplicação de descontos promocionais

#### Regras de Negócio

- Preço base: R$ 9,90
- Taxa adicional para estreia: R$ 5,00
- Desconto promocional: 20%

#### Exemplo de Uso

```java
Filme filme = new Filme(
    "Titulo",
    "Categoria",
    120,
    14,
    true,
    true
);

double preco = filme.calcularPrecoAluguel();
double precoPromocional = filme.aplicarPromocao(preco);
```

### Documentário

A classe `Documentario` representa documentários disponíveis na plataforma.

#### Características

- Herda da classe `Conteudo`
- Possui tema obrigatório
- Não possui custo de aluguel
- Realiza validação de dados

#### Validações

O sistema lança uma exceção quando:

- O tema é nulo
- O tema está vazio
- O tema contém apenas espaços em branco

#### Exemplo de Uso

```java
Documentario documentario = new Documentario(
    "Titulo",
    "Categoria",
    50,
    10,
    true,
    "Tema"
);

double preco = documentario.calcularPrecoAluguel();
```

### Interface Promocionavel

Define o contrato para aplicação de promoções nos conteúdos da plataforma.

```java
public interface Promocionavel {
    double aplicarPromocao(double preco);
}
```

#### Implementação

A implementação aplica 20% de desconto sobre o valor informado.

```java
preco * 0.80
```

---

## Tecnologias Utilizadas

- Java
- Jakarta Persistence (JPA)
- Maven
- Programação Orientada a Objetos (POO)

---

## Conceitos Aplicados

- Herança
- Polimorfismo
- Interfaces
- Encapsulamento
- Tratamento de exceções
- Validação de dados
- Persistência de dados

---

## Autor

**Gabriel Jorge Coutinho**  
**RM: 565441**

---

## Licença

Projeto desenvolvido para fins acadêmicos.
