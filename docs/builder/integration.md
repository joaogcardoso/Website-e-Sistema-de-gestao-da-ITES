# Integração com o editor de páginas

## Componentes reaproveitados

Serão portados do `web-builder`: operações de documento, registro de blocos, blocos selecionados do catálogo, área de edição (Canvas), catálogo, editor de propriedades, pré-visualização e controles de publicação.

## Blocos permitidos no portal da ITES

Destaque (hero), texto formatado, cartões, notícias, contato e chamada para ação.

## Regras de segurança

- O servidor valida o tipo de bloco, as propriedades, as URLs e o tamanho do conteúdo. Scripts e atributos de evento não são aceitos.
- Blocos desconhecidos aparecem como espaços inertes e impedem a publicação.
- Autenticação, formulários de demanda, telas de decisão e dados pessoais **nunca** são blocos editáveis.

## Versões e publicação

Cada página é identificada por um endereço fixo (slug). As versões de rascunho são guardadas em JSONB com número de revisão, e a página aponta para a versão publicada. Publicar a página e registrar a auditoria acontecem em uma única operação.

## Permissões

| Ação | Permissão necessária |
|---|---|
| Salvar rascunho | `builder.pages.edit` |
| Publicar | `builder.pages.publish` |
| Gerenciar modelos | `builder.templates.manage` |

O frontend mostra apenas as ações que o usuário pode fazer, mas quem garante a regra é o backend.

## Verificação

Testar: bloqueio de acesso sem permissão, conflito de edição, recarregar o rascunho, pré-visualizar, publicar, exibição pública e funcionamento das telas protegidas depois das edições.
