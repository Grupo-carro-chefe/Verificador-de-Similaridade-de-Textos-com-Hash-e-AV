# Verificador de Similaridade de Textos com Hash e AVL

Projeto Java para verificação de similaridade entre textos utilizando Tabela Hash e Árvore AVL.

---

## Estrutura do Projeto

```
verificador-similaridade/
├── Main.java                  <- ponto de entrada do programa
├── Documento.java             <- representa um arquivo de texto processado
├── HashTable.java             <- tabela hash implementada manualmente
├── AVLTree.java               <- árvore AVL implementada manualmente
├── ComparadorDeDocumentos.java <- calcula a similaridade entre dois documentos
├── Resultado.java             <- armazena o resultado de uma comparação
├── documentos/                <- coloque aqui os arquivos .txt de teste
└── resultado.txt              <- gerado automaticamente ao executar o programa
```

---

## Como compilar e executar

Abra o terminal na pasta do projeto e rode:

```bash
# Compilar todos os arquivos Java
javac *.java

# Executar (modo lista)
java Main ./documentos 0.7 lista

# Executar (modo top K - exibe os 5 mais similares)
java Main ./documentos 0.8 topK 5

# Executar (modo busca - compara dois arquivos específicos)
java Main ./documentos 0.0 busca doc1.txt doc2.txt
```

---

## Como configurar o Git (para quem nunca usou)

### 1. Instale o Git
- Windows: baixe em https://git-scm.com/download/win e instale
- Linux/Mac: já vem instalado (ou rode `sudo apt install git`)

### 2. Configure seu nome e e-mail (só na primeira vez)
```bash
git config --global user.name "Seu Nome"
git config --global user.email "seu@email.com"
```

### 3. Clone o repositório (baixar o projeto)
```bash
git clone https://github.com/Grupo-carro-chefe/Verificador-de-Similaridade-de-Textos-com-Hash-e-AV.git
cd Verificador-de-Similaridade-de-Textos-com-Hash-e-AV
```

### 4. Fluxo básico de trabalho (use sempre que terminar algo)

```bash
# Ver o que mudou
git status

# Adicionar os arquivos que você alterou
git add NomeDoArquivo.java

# Ou adicionar tudo de uma vez
git add .

# Salvar as mudanças com uma mensagem
git commit -m "descrição do que você fez"

# Enviar para o GitHub
git push
```

### 5. Pegar as atualizações dos colegas
```bash
git pull
```

> **Dica:** sempre dê `git pull` antes de começar a trabalhar para não criar conflito com o que os colegas fizeram.

---

## Requisitos

- JDK 17 ou superior
- Nenhuma biblioteca externa (tudo implementado do zero)
