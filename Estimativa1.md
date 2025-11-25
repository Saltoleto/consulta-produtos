Abaixo está **a versão final da estimativa**, com o escopo consolidado, uso de **Redis como cache**, e **pontos de atenção sobre permissionamento**, conforme solicitado.
O texto já está adequado para ser colocado no **Jira**, **Confluence** ou **documento de refinamento**.

---

# ✅ **ESTIMATIVA FINAL – Feature: Consulta de Saldos Open Finance na Jornada de Extrato**

## 🎯 **Objetivo**

Disponibilizar os saldos de todas as contas Open Finance do operador na jornada de Extrato, utilizando:

* Redis como mecanismo de cache
* YO4 como origem do permissionamento
* ER4 como origem dos saldos
* Nenhum agrupamento (diferente da Jornada de Gestão Financeira PJ)

---

# 🧱 **Arquitetura da Solução (Resumo)**

### 1. A jornada de Extrato:

* Recupera ou cria o operador (em YO4)
* Verifica se o operador possui permissionamento atualizado
* Caso o permissionamento esteja válido:

  * consulta saldos no Redis
  * se o cache expirou → consulta ER4 para todas as contas OF
  * armazena lista completa no Redis com TTL
* Retorna ao frontend os saldos de todas as contas

### 2. Redis Cache

* Chave: `saldos:openfinance:<operadorId>`
* Valor: lista de contas + saldos
* TTL recomendado: 120–300s
* Estrutura única por operador (não por conta)

---

# 📘 **WBS – Work Breakdown Structure**

## **Etapa 1 — Análise (10h)**

* Revisão dos fluxos YO4 → contas permissionadas (4h)
* Definição de contrato do cache e formato JSON no Redis (3h)
* Análise de impacto de permissionamento na jornada de Extrato (3h)

---

## **Etapa 2 — Implementação Backend (27h)**

* Configuração do Redis (conexão, pooling, segurança) – 3h
* Serialização/deserialização da lista de saldos – 3h
* Implementação do cache por operador no Redis – 6h
* Implementação da reconstrução do cache (YO4 + ER4) – 6h
* Endpoint GET `/extrato/openfinance/saldos` – 5h
* Lógica de fallback em caso de falha da ER4 – 4h

---

## **Etapa 3 — Testes (17h)**

* Testes unitários: cache hit, miss, erro ER4, fallback – 5h
* Testes integrados utilizando Redis em container – 7h
* Testes E2E simulando expiração do cache – 5h

---

## **Etapa 4 — Infra, Observabilidade e Documentação (9h)**

* Provisionamento de Redis / ElastiCache (TLS + SG + ACLs) – 5h
* Métricas: cache hit/miss, chamadas ER4, latência – 2h
* Documentação da solução, diagrama, políticas de TTL – 2h

---

# 📊 **TOTAL DE HORAS**

### **10h + 27h + 17h + 9h = 63 horas**

---

# 🧮 **Estimativa PERT (final)**

| Cenário       | Horas |
| ------------- | ----- |
| Otimista      | 52h   |
| Mais provável | 63h   |
| Pessimista    | 82h   |

### **→ Estimativa final PERT: ~64 horas**

**Lead time recomendado: 4 a 5 dias úteis.**

---

# 🚨 **Pontos de Atenção da Feature**

A seguir, as três perguntas críticas que impactam a implementação.

---

## 🔶 **1. A jornada de Extrato deve criar o operador e iniciar permissionamento?**

### **Resposta / Impacto**

Sim, **extrato deve seguir a mesma regra da Gestão Financeira PJ**:

* Se o operador **não existir**, a jornada deve:

  1. Criar o operador (POST YO4)
  2. Iniciar o processo de permissionamento completo
  3. Aguardar retorno dos dados do operador

⚠ **Impacto:**
Durante o período de criação/permissionamento, o operador ainda não terá contas Open Finance disponíveis para consulta de saldos.

---

## 🔶 **2. O que deve ser mostrado enquanto o permissionamento está em andamento?**

### **Recomendação**

Durante permissionamento inicial (ou repermissionamento manual), exibir:

* **Mensagem informativa**:
  “Estamos verificando suas contas Open Finance. Tente novamente em alguns minutos.”

* **Lista vazia de contas Open Finance**, mas:

  * exibir *placeholder*
  * indicar *status de carregamento*

### Evitar:

* Exibir valores desatualizados
* Exibir contas irreais
* Exibir zero como saldo (causa interpretação errada)

⚠ **Impacto no código:**

* O backend deve retornar um status/flag indicando que `permissionamentoEmAndamento = true`.

---

## 🔶 **3. Extrato deve solicitar repermissionamento a cada 30 minutos (igual GF-PJ)?**

### **Resposta / Impacto**

Sim, **é recomendado manter o mesmo comportamento**, por motivos de:

* segurança regulatória
* atualização de contas revogadas ou adicionadas
* consistência entre jornadas

### Funcionamento:

* Front acessa jornada
* Backend verifica timestamp do último permissionamento
* Se **> 30 minutos** → dispara repermissionamento em YO4
* O retorno da jornada não deve aguardar o repermissionamento
* O resultado da consulta de saldos *usa o cache atual ou revalida*

⚠ **Impacto no backend:**

* Nova verificação de timestamp por operador
* Endpoint YO4/IG3 para disparar repermissionamento
* Nova flag de retorno para o frontend

---

# 🧩 **Conclusão Final da Estimativa**

A feature exige:

* Redis como cache
* Consulta de todas as contas OF com reconstrução programática
* Criação de operador se não existir
* Execução de permissionamento inicial e periódico
* Tratamento de estados intermediários (permissionamento em andamento)
* Testes robustos envolvendo fluxos de cache e ER4

### 🔥 **Esforço total: ~63 horas**

### 🚀 **Lead time: 4–5 dias úteis**

---

Se quiser, posso também gerar:

* **Documento completo para Jira / Confluence (versão final formatada)**
* **Diagrama C4 nível C2/C3**
* **PlantUML do fluxo de extrato com cache + permissionamento**
* **Modelo de contrato de API**

Só pedir!
