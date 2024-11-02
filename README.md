# WordSwap - Back-end

## Pré-requisitos

- [Java JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) (versão 17 ou superior)
- IDE de sua escolha (por exemplo, [IntelliJ IDEA](https://www.jetbrains.com/idea/) ou [Eclipse](https://www.eclipse.org/))

## Clonando o Repositório

1. Clone este repositório para o seu ambiente local usando o comando:

   ```bash
   git clone https://github.com/vviccenzo/wordswap-backend
   ```

2. Acesse a pasta do projeto:

   ```bash
   cd wordswap-backend
   ```

## Importando o Projeto na IDE

### IntelliJ IDEA

1. Abra o IntelliJ IDEA.
2. Selecione "Open" e navegue até a pasta do projeto clonada.
3. Clique em "OK" para importar o projeto.
4. O IntelliJ irá reconhecer automaticamente o projeto Maven (se aplicável) e baixar as dependências necessárias.

### Eclipse

1. Abra o Eclipse.
2. Selecione "File" > "Import".
3. Escolha "Existing Maven Projects" se estiver usando Maven.
4. Navegue até a pasta do projeto clonada e clique em "Finish" para importar.

## Executando o Projeto

Após importar o projeto, você pode executá-lo de uma das seguintes maneiras:

- Se você estiver usando um IDE como o IntelliJ ou Eclipse, clique com o botão direito na classe principal (a que contém o método `main`) e selecione "Run".

- Se preferir executar via terminal, você pode usar o Maven para compilar e executar o projeto com os seguintes comandos:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

O servidor iniciará na porta padrão 8080. Acesse a API em [http://localhost:8080](http://localhost:8080).
