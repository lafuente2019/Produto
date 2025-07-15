# Monitor de Imagem

## Tabela de conteúdo

- [Cliente](#cliente)
- [Parceiro](#parceiro)
- [Fontes de dados](#fontes-de-dados)
- [Descrição](#descrição)
- [Tipos de arquivos disponibilizados](#tipos-de-arquivos-disponibilizados)
- [Categorias de Pesquisas DYT](#categorias-de-pesquisas-dyt)
  - [Categorias](#categorias)
- [AlphaBrand](#alphabrand)
  - [Frequência de realização do Questionário](#frequência-de-realização-do-questionário)
  - [Frequência de envio dos arquivos Proprietários](#frequência-de-envio-dos-arquivos-proprietários)
  - [Marcas abordadas](#marcas-abordadas)
  - [Indicadores Proprietários DYT](#indicadores-proprietários-dyt)
  - [Indicadores extraídos do Questionário](#indicadores-extraídos-do-questionário)
  - [Filtros extraídos do Questionário](#filtros-extraídos-do-questionário)
- [Arquitetura de Automação e Execução](#arquitetura-de-automação-e-execução)
  - [Lógica de execução](#lógica-de-execução)
  - [Nomenclatura dos recursos utilizados](#nomenclatura-dos-recursos-utilizados)
- [Reprocessamento manual](#reprocessamento-manual)
- [Dataproc Workflow Templates](#dataproc-workflow-templates)
  - [Workflow: questionario](#workflow-monitor-alpha-questionario)
  - [Workflow: proprietarios](#workflow-monitor-alpha-proprietarios)
- [Persistência](#persistência)
- [Indicadores do Questionário e Regras de Negócio](#indicadores-do-questionário-e-regras-de-negócio)
- [Indicadores Proprietários DYT](#indicadores-proprietários-dyt)
- [Indicadores de Respostas Espontâneas](#indicadores-de-respostas-espontâneas)
- [Filtros](#filtros)
  - [Filtros Demográficos](#filtros-demográficos)
  - [Assinantes por plataforma](#assinantes-por-plataforma)
- [Dashboard](#dashboard)
  - [Indicadores Contemplados](#indicadores-contemplados)
  - [Estrutura](#estrutura)
  - [Métricas](#métricas)
  - [Granularidade de visualização](#granularidade-de-visualização)
  - [Filtros do dashboard](#filtros-do-dashboard)
- [Links e referências](#links-e-referências)

| Modificado por | Data       |
| :------------- | :--------- |
| Alex Nunes     | 12/05/2025 |
| Carla Souza    | 14/07/2025 |

## Cliente

Equipe de Inteligência de Mercado - Grupo Fictício

Pontos focais:

- Mariana Lopes (mariana.lopes@empresa.com)
- Tiago Ferreira (tiago.ferreira@empresa.com)

## Parceiro

Equipe de Soluções Analíticas

Pontos focais:

- Lucas Prado (lucas.prado@parceiro.com)
- Helena Rocha (helena.rocha@parceiro.com)

## Fontes de dados

Arquivos disponibilizados via Sharepoint:

- Questionários em formato SPSS (.sav)
- Métricas proprietárias DYT em planilhas Excel

## Descrição

Pipeline para processamento automatizado de KPIs de imagem de marcas, com dados coletados em pesquisas com o público. O processamento ocorre na Google Cloud Platform, gerando tabelas BigQuery que alimentam dashboards Power BI para acompanhamento do cliente.

## Tipos de arquivos disponibilizados

- Dados brutos de questionários respondidos (.sav)
- Planilhas Excel com cálculos proprietários feitos pela DYT

## Categorias de Pesquisas DYT

### Categorias

- AlphaBrand
- Séries
- Notícias
- Esportes
- Kids
- Lifestyle
- Streaming
- Música

## AlphaBrand

### Frequência de realização do Questionário

- Jul/2024: inicial
- Mensal até Dez/2024
- Trimestral a partir de Jan/2025

### Frequência de envio dos arquivos Proprietários

- Trimestral para Power e Premium
- Semestral para BIP, Barreiras e Facilitadores

### Marcas abordadas

- Fictoflix
- Canal10
- PlayMax
- VidaTV
- MegaMusic
- NovaNews
- StreamUp
- CineGo

### Indicadores Proprietários DYT

- Power e Dimensões
- Premium
- Contribuição das Dimensões para o Power
- BIP
- Barreiras e Facilitadores
- Média de Mercado do NPS

### Indicadores extraídos do Questionário

- Afinidade
- Confiança
- Consideração
- Diferenciação
- Familiaridade
- Imagem
- Preferência
- NPS
- Preço Percebido
- Vale à Pena
- Atende às Necessidades
- Dita Tendências

### Filtros extraídos do Questionário

- Familiaridade
- NPS
- Sexo
- Faixa Etária
- Classe Social
- Região
- Assinantes por Plataforma

## Arquitetura de Automação e Execução

Executado no projeto `corp-analytics-prod` na GCP.

Fluxo unificado com ramificações para `questionario` e `proprietarios`, controlado via parâmetros.

### Lógica de execução

1. **Cloud Scheduler** diário às 8h (BRT) aciona **Cloud Function** com `TIPO_PROCESSAMENTO` (`QUESTIONARIO` ou `PROPRIETARIOS`).
2. Cloud Function:
   - Verifica novos arquivos no Sharepoint.
   - Baixa para bucket LND.
   - Gera lista de arquivos a serem processados.
   - Valida planilhas (abas obrigatórias para proprietários).
   - Dispara o Workflow do Dataproc.
3. Dataproc:
   - Processa dados e salva Parquet no bucket RAW.
   - Popula tabelas no BigQuery (RAW e PREP).

### Nomenclatura dos recursos utilizados

- **Scheduler:** `monitor-alpha-extract-sharepoint`
- **Cloud Function:** `monitor-alpha-extract-sharepoint-fn`
- **Dataproc Workflow:** `monitor-alpha-[questionario|proprietarios]`
- **Buckets:** `monitor-alpha-lnd`, `monitor-alpha-raw`, `monitor-alpha-processing`
- **Datasets BigQuery:** `raw_monitor_imagem_alpha`, `prep_monitor_imagem_alpha`

## Reprocessamento manual

Em alguns casos pode ser necessário realizar um reprocessamento manual dos dados em produção (PRD).

Para isso, siga os passos abaixo:

- Primeiro, acesse o bucket LND e vá até a pasta do arquivo que deseja reprocessar:
  - Para **proprietários**: `monitor-alpha-lnd-xyz123/alphabrand/proprietarios`
  - Para **questionário**: `monitor-alpha-lnd-xyz123/alphabrand/questionario`
- É necessário excluir o arquivo existente nessa pasta para que o processo identifique que precisa buscá-lo novamente no Sharepoint.  
  Caso não tenha permissão para excluir o arquivo, solicite a alguém que possua.

- Após excluir o arquivo, acione manualmente o Scheduler correspondente ao fluxo que deseja reprocessar:
  - Para **proprietários**: `monitor-alpha-extract-sharepoint-cloudfunction-proprietarios`
  - Para **questionário**: `monitor-alpha-extract-sharepoint-cloudfunction-questionario`

- Ao ser acionada, a Cloud Function irá verificar o bucket LND, não encontrará o arquivo e iniciará o download do Sharepoint, processando os dados via Dataproc e carregando nas tabelas finais do BigQuery.

- Após a execução, valide se os dados foram carregados corretamente nas tabelas finais no BigQuery.

## Dataproc Workflow Templates

### Workflow: `monitor-alpha-questionario`

Steps:

- `load-questionario`
- `transform-questionario-afinidade`
- `transform-questionario-confianca`
- `transform-questionario-consideracao`
- `transform-questionario-nps`
- `transform-questionario-preferencia`
- `transform-questionario-filtros`

### Workflow: `monitor-alpha-proprietarios`

Steps:

- `load-metricas-power`
- `load-metricas-premium`
- `load-metricas-bip`
- `load-metricas-barreiras`

## Persistência

- Bucket LND: arquivos brutos do Sharepoint.
- Bucket RAW: arquivos Parquet pós-processamento.
- BigQuery:
  - RAW: tabelas externas.
  - PREP: tabelas nativas por indicador.

## Indicadores do Questionário e Regras de Negócio

- `tb_afinidade`: percepção -3 a +3.
- `tb_confianca`: confiança de 1 a 7.
- `tb_consideracao`: primeira escolha até não consideraria.
- `tb_nps`: detratores, neutros, promotores.
- `tb_preferencia`: marca preferida.
- `tb_familiaridade`: conhecimento e consumo.
- `tb_dita_tendencias`: percepção de inovação.

## Indicadores Proprietários DYT

- `tb_power_dimensoes`
- `tb_premium`
- `tb_bip_relativo`
- `tb_barreiras_facilitadores`
- `tb_nps_mercado`

## Indicadores de Respostas Espontâneas

- Awareness espontâneo
- Buzz (o que ouviu falar)
- Razões de não consumo

## Filtros

### Filtros Demográficos

- Sexo, Idade, Classe, Região

### Assinantes por plataforma

- Streaming, TV paga, IPTV, não assinantes

## Dashboard

### Indicadores Contemplados

- KPIs do questionário e proprietários

### Estrutura

- Visualizações multi marca e multi período

### Métricas

- % respondentes
- Índice NPS = % promotores - % detratores

### Granularidade de visualização

- Por onda, mês, trimestre, semestre

### Filtros do dashboard

- Familiaridade
- NPS
- Assinatura por plataforma
- Sexo
- Idade
- Classe
- Região

## Links e referências

- [Dashboard Monitor de Imagem](https://monitorimagem.empresa.com/)

