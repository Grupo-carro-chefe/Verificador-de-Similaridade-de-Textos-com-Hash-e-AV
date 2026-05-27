# Verificador de Similaridade de Textos com Hash e AVL

Projeto Java para verificação de similaridade entre textos utilizando Tabela Hash e Árvore AVL.

---

## Estrutura do Projeto

```
verificador-similaridade/
├── Main.java                   <- PARTE 3: ponto de entrada do programa (pronto)
├── Resultado.java              <- PARTE 3: estrutura de um par comparado (pronto)
├── AVLTree.java                <- PARTE 3: árvore AVL implementada manualmente (pronto)
├── Documento.java              <- PARTE 1: leitura e normalização de texto (pronto)
├── ComparadorDeDocumentos.java <- PARTE 1: cálculo de similaridade cosseno (pronto)
├── HashTable.java              <- PARTE 2: tabela hash implementada manualmente (pronto)
├── stopwords_pt.txt            <- lista de stop words em português (pronto)
├── documentos/                 <- coloque aqui os arquivos .txt de teste
└── resultado.txt               <- gerado automaticamente ao executar o programa
```

---

## Guia para quem vai fazer a Parte 3 (AVL + Main)

### O que já está pronto e como usar

**`Documento`** — representa um arquivo `.txt` processado:
```java
// Carregar stop words (faça isso uma vez só)
HashTable<String, Boolean> stopWords = Documento.carregarStopWords("stopwords_pt.txt");

// Criar um documento a partir de um arquivo
Documento doc = new Documento(Path.of("documentos/doc1.txt"), stopWords);

doc.getNome();             // "doc1.txt"
doc.getTamanhoVocabulario(); // número de palavras únicas
doc.getTotalTokens();      // total de tokens válidos
doc.getFrequencias();      // HashTable<String, Integer> com palavra -> frequência
```

**`ComparadorDeDocumentos`** — calcula similaridade pelo cosseno:
```java
double sim = ComparadorDeDocumentos.cosseno(doc1, doc2);
// retorna valor entre 0.0 (nada em comum) e 1.0 (idênticos)
```

**`HashTable`** — usada internamente, mas útil para o relatório:
```java
HashTable<String, Integer> tabela = new HashTable<>(); // DJB2, capacidade 1009
HashTable<String, Integer> tabela2 = new HashTable<>(1009, HashTable.HASH_POLINOMIAL);

tabela.getDistribuicaoBuckets(); // int[] com qtd de elementos por bucket (para o gráfico do relatório)
tabela.getTotalColisoes();       // total de colisões
tabela.getNomeFuncaoHash();      // "hashDJB2" ou "hashPolinomial"
```

---

### Parte 3 — implementada (Resultado, AVLTree e Main)

> Status: **concluída**. As descrições abaixo documentam o que cada classe faz e a API disponível.

#### 1. `Resultado.java` — estrutura simples

Guarda o resultado de uma comparação entre dois documentos:

```java
public class Resultado {
    public String nomeDoc1;
    public String nomeDoc2;
    public double similaridade;

    public Resultado(String nomeDoc1, String nomeDoc2, double similaridade) { ... }
}
```

#### 2. `AVLTree.java` — árvore AVL com chave `double`

Requisitos do professor:
- Chave = similaridade (`double`)
- Valor em cada nó = **lista de `Resultado`** (para empates de similaridade)
- Implementar rotações simples (esquerda/direita) e duplas (esquerda-direita/direita-esquerda)
- O método `inserir()` deve **retornar ou acumular** a contagem de rotações realizadas

Estrutura sugerida do nó:
```java
private static class No {
    double chave;                // similaridade
    ArrayList<Resultado> pares;  // lista para tratar empates
    No esquerdo, direito;
    int altura;
}
```

Métodos mínimos necessários:
```java
public void inserir(double chave, Resultado resultado) // insere e conta rotações
public List<Resultado> buscarAcimaDe(double limiar)    // retorna pares com sim >= limiar
public List<Resultado> topK(int k)                     // retorna os K mais similares
public int getRotacoesSimples()                        // para o relatório
public int getRotacoesDuplas()                         // para o relatório
```

#### 3. `Main.java` — orquestra tudo

Fluxo esperado:
1. Ler e validar os argumentos da linha de comando
2. Carregar stop words do `stopwords_pt.txt`
3. Ler todos os `.txt` da pasta `documentos/` e criar objetos `Documento`
4. Comparar todos os pares e inserir os resultados na `AVLTree`
5. Executar o modo solicitado (`lista`, `topK` ou `busca`)
6. Imprimir no terminal **e** gravar em `resultado.txt`

Assinatura esperada pelo professor:
```
java Main <diretorio_documentos> <limiar> <modo> [argumentos_opcionais]
```

**Atenção — bug clássico de formatação:** use `Locale.US` no printf, senão em máquinas BR o `0.67` sai como `0,67` e quebra a saída. O professor usa **2 casas decimais**:
```java
System.out.printf(java.util.Locale.US, "%.2f", similaridade);
```

Formato de saída esperado pelo professor (modos `lista` e `topK`):
```
=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===
Total de documentos processados: 5
Total de pares comparados: 10
Função hash utilizada: hashDJB2
Métrica de similaridade: Cosseno

Pares com similaridade >= 0.75:
---------------------------------
doc1.txt <-> doc2.txt = 0.82
doc3.txt <-> doc4.txt = 0.79

Pares com menor similaridade:
---------------------------------
doc1.txt <-> doc5.txt = 0.12
```

O modo `busca` tem um formato próprio e enxuto:
```
=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===
Comparando: doc1.txt <-> doc4.txt
Similaridade calculada: 0.67
Métrica utilizada: Cosseno
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
