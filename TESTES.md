# Guia de Testes — Verificador de Similaridade

## 1. Pré-requisitos

### Verificar se o Java está instalado
Abra o terminal (CMD, PowerShell ou WSL) e rode:
```
java -version
```
Deve aparecer `java version "17"` ou superior. Se não aparecer, baixe o JDK em:
https://adoptium.net (clique em "Latest LTS release")

---

## 2. Baixar o projeto

```bash
git clone https://github.com/Grupo-carro-chefe/Verificador-de-Similaridade-de-Textos-com-Hash-e-AV.git
cd Verificador-de-Similaridade-de-Textos-com-Hash-e-AV
```

---

## 3. Compilar

Na pasta do projeto, rode:
```bash
javac *.java
```
**Resultado esperado:** nenhuma mensagem de erro. Vários arquivos `.class` serão criados.

---

## 4. Testes

### Teste 1 — Lista com limiar zero (mostra todos os pares)
```bash
java Main ./documentos 0.0 lista
```
**Saída esperada:**
```
=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===
Total de documentos processados: 5
Total de pares comparados: 10
Função hash utilizada: hashDJB2
Métrica de similaridade: Cosseno

Pares com similaridade >= 0.0000:
---------------------------------
doc1.txt <-> doc2.txt = 0.5559
doc3.txt <-> doc4.txt = 0.4082
doc1.txt <-> doc5.txt = 0.1091
doc2.txt <-> doc4.txt = 0.0400
doc2.txt <-> doc5.txt = 0.0377
doc1.txt <-> doc3.txt = 0.0000
doc1.txt <-> doc4.txt = 0.0000
doc2.txt <-> doc3.txt = 0.0000
doc3.txt <-> doc5.txt = 0.0000
doc4.txt <-> doc5.txt = 0.0000

Rotações simples realizadas na AVL: 0
Rotações duplas realizadas na AVL: 2
Resultado salvo em resultado.txt
```
**O que valida:** leitura de todos os documentos, cálculo de similaridade, AVL e saída completa.

---

### Teste 2 — Lista com limiar alto (filtra pares)
```bash
java Main ./documentos 0.4 lista
```
**Saída esperada (apenas os pares acima de 0.40):**
```
=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===
Total de documentos processados: 5
Total de pares comparados: 10
Função hash utilizada: hashDJB2
Métrica de similaridade: Cosseno

Pares com similaridade >= 0.4000:
---------------------------------
doc1.txt <-> doc2.txt = 0.5559
doc3.txt <-> doc4.txt = 0.4082

Rotações simples realizadas na AVL: 0
Rotações duplas realizadas na AVL: 2
Resultado salvo em resultado.txt
```
**O que valida:** filtro por limiar na AVL funcionando.

---

### Teste 3 — Top K pares mais similares
```bash
java Main ./documentos 0.0 topK 3
```
**Saída esperada (os 3 mais similares):**
```
=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===
Total de documentos processados: 5
Total de pares comparados: 10
Função hash utilizada: hashDJB2
Métrica de similaridade: Cosseno

Top 3 pares mais similares:
---------------------------------
doc1.txt <-> doc2.txt = 0.5559
doc3.txt <-> doc4.txt = 0.4082
doc1.txt <-> doc5.txt = 0.1091

Rotações simples realizadas na AVL: 0
Rotações duplas realizadas na AVL: 2
Resultado salvo em resultado.txt
```
**O que valida:** percurso descendente da AVL e corte por K.

---

### Teste 4 — Busca direta entre dois arquivos
```bash
java Main ./documentos 0.0 busca doc1.txt doc2.txt
```
**Saída esperada:**
```
=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===
Total de documentos processados: 2
Total de pares comparados: 1
Função hash utilizada: hashDJB2
Métrica de similaridade: Cosseno

Comparação direta: doc1.txt <-> doc2.txt
---------------------------------
doc1.txt <-> doc2.txt = 0.5559

Rotações simples realizadas na AVL: 0
Rotações duplas realizadas na AVL: 0
Resultado salvo em resultado.txt
```
**O que valida:** modo busca com apenas dois documentos.

---

### Teste 5 — Verificar o arquivo resultado.txt
Após qualquer execução acima, cheque se o arquivo foi gerado:
```bash
# Linux/Mac/WSL:
cat resultado.txt

# Windows CMD:
type resultado.txt
```
O conteúdo deve ser idêntico ao que apareceu no terminal.

---

### Teste 6 — Erro: argumento inválido
```bash
java Main ./documentos abc lista
```
**Saída esperada (mensagem de erro no terminal):**
```
Erro: limiar deve ser um numero decimal (ex: 0.75)
```
**O que valida:** tratamento de entradas inválidas.

---

### Teste 7 — Erro: pasta inexistente
```bash
java Main ./pasta_que_nao_existe 0.5 lista
```
**Saída esperada:**
```
Erro: diretorio nao encontrado: ./pasta_que_nao_existe
```

---

## 5. Checklist de validação

Marque cada item após confirmar:

- [ ] Compila sem erros com `javac *.java`
- [ ] Teste 1 passa com saída idêntica
- [ ] Teste 2 mostra apenas os 2 pares acima de 0.40
- [ ] Teste 3 mostra exatamente os 3 primeiros
- [ ] Teste 4 mostra apenas 1 par e 0 rotações duplas
- [ ] Teste 5 — arquivo `resultado.txt` foi gerado e bate com o terminal
- [ ] Teste 6 exibe mensagem de erro sem travar
- [ ] Teste 7 exibe mensagem de erro sem travar
