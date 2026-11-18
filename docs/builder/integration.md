# Editor visual de páginas institucionais

## Escopo funcional

O editor integrado permite abrir páginas institucionais, organizar blocos disponíveis, editar propriedades suportadas, visualizar a prévia, salvar rascunhos e publicar versões autorizadas. As operações utilizam a identidade, as permissões e a persistência da plataforma ITES.

## Blocos e validação

O catálogo é controlado pelo frontend e pelas definições de contrato utilizadas pela API. O servidor valida tipos de bloco, propriedades aceitas, URLs e tamanho do documento. Blocos desconhecidos não podem ser publicados. Documentos são dados JSON; nenhum código fornecido pelo usuário é executado.

## Ciclo de publicação

1. A equipe autorizada abre uma página institucional.
2. O editor carrega a versão atual e permite alterações apenas nos blocos suportados.
3. O rascunho é salvo com controle de revisão.
4. A prévia renderiza o documento validado sem alterar a versão pública.
5. Um usuário com a concessão de publicação ativa publica a versão.
6. A página pública consulta somente conteúdo publicado.
7. As alterações podem ser reabertas para edição e publicadas em nova revisão.

## Limites de edição e autorização

| Operação | Concessão |
|---|---|
| Salvar rascunho | `builder.pages.edit` |
| Publicar ou retirar do ar | `builder.pages.publish` |
| Gerenciar modelos | `builder.templates.manage` |

A autenticação, os formulários de demandas, a revisão de pedidos e os dados pessoais não são áreas editáveis. O frontend apresenta ações conforme as concessões efetivas, enquanto a API é responsável pela autorização final.

## Segurança

- Conteúdo não confiável é validado e renderizado pelo conjunto controlado de componentes.
- Scripts, propriedades arbitrárias e atributos executáveis não são aceitos.
- Endereços externos devem obedecer às regras de URL segura.
- Rascunhos não são expostos ao público.
- Publicação e auditoria persistem em conjunto.
