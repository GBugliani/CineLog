# CineLog

Aplicacao web para buscar filmes, avaliar e organizar um catalogo pessoal. O CineLog usa a API do OMDb para consultar informacoes dos filmes e permite que cada usuario salve sua nota e comentario.

## Funcionalidades

- Cadastro e login de usuarios
- Busca de filmes por titulo
- Exibicao de poster, ano, genero, duracao, direcao, elenco e sinopse
- Avaliacao de 1 a 5 estrelas/notas
- Comentarios pessoais
- Catalogo individual com total de filmes e media das notas
- Remocao de filmes salvos

## Tecnologias

- Java 21
- Spring Boot 4
- Spring MVC e Thymeleaf
- Spring Security
- Spring Data JPA
- Banco H2 em memoria
- API OMDb
- Maven

## Pre-requisitos

- JDK 21 ou superior
- Uma chave gratuita da API OMDb: https://www.omdbapi.com/apikey.aspx

## Configuracao

Defina a chave da OMDb na variavel de ambiente `OMDB_API_KEY`.

No PowerShell:

```powershell
$env:OMDB_API_KEY = "sua-chave-da-omdb"
```

No Linux ou macOS:

```bash
export OMDB_API_KEY="sua-chave-da-omdb"
```

A aplicacao le essa variavel em `src/main/resources/application.properties`. Nao coloque chaves reais no repositorio.

## Como executar

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Acesse http://localhost:8080, crie uma conta e comece a montar seu catalogo.

## Testes

```bash
./mvnw test
```

No Windows PowerShell, use `./mvnw.cmd test`.

## Estrutura principal

```text
src/main/java/com/example/CineLog/
├── config/       configuracao de seguranca
├── controller/   rotas da aplicacao
├── dto/          objetos de transferencia da OMDb
├── model/        entidades Usuario e FilmeSalvo
├── repository/   repositorios JPA
└── service/      integracao com a API OMDb

src/main/resources/
├── templates/    paginas Thymeleaf
└── static/       estilos CSS
```

## Observacoes

O banco H2 esta configurado em memoria para desenvolvimento. Os dados sao perdidos quando a aplicacao e reiniciada. Para um ambiente de producao, configure um banco persistente e altere as propriedades de datasource.

## Licenca

Este projeto ainda nao possui uma licenca definida.