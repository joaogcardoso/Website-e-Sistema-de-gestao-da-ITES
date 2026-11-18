# Frontend

Cliente web do VH Studio. A aplicação usa React 19, TypeScript, Vite, React
Router, Tailwind CSS 4 e componentes Base UI/shadcn. Em desenvolvimento o Vite
serve a aplicação; no Compose, o build estático roda em Nginx, que encaminha
`/api/` ao backend Spring.

## Organização do código

- `src/router/`: rotas públicas, privadas, carregamento sob demanda e caminhos
  centralizados.
- `src/context/`: estado compartilhado de sessão e provedores da aplicação.
- `src/pages/`: telas de autenticação, conta, projetos, temas, editor,
  publicações e visualização de sites publicados.
- `src/services/`: chamadas HTTP e transformação de erros por domínio.
- `src/types/`: contratos TypeScript usados pela API e pela aplicação.
- `src/utils/blocks/`: catálogo de blocos, metadados editáveis, renderizadores e
  código compartilhado.
- `src/utils/pages/`: composições prontas a partir dos IDs do catálogo.
- `scripts/generate-catalog.mjs`: verifica metadados e regenera os arquivos
  tipados do catálogo e das fontes de exportação.

## Fluxos disponíveis

As rotas públicas cobrem login, cadastro com confirmação por e-mail,
recuperação de senha, confirmação de endereço e sites publicados em `/p/:key`.
As rotas privadas cobrem configurações da conta, lista de projetos, temas,
publicações, a caixa de entrada de revisões em `/publication/reviews`, detalhes
de uma publicação e edição/preview de projetos. A inbox depende de
`PUBLICATION_APPROVE`; o preview carrega somente o documento imutável do pedido
autorizado e não concede leitura geral de publicações.

O builder permite criar páginas hierárquicas, inserir e reordenar blocos,
editar propriedades no painel e diretamente no canvas, escolher uma navbar,
aplicar tema, desfazer/refazer alterações e visualizar o resultado. O documento
do projeto inclui suas páginas, blocos, tema e navegação. O editor valida e
normaliza documentos antes de carregá-los. Definições de bloco desconhecidas
são preservadas como conteúdo opaco e aparecem em placeholders não editáveis;
operações e salvamentos mantêm esses dados até que a pessoa os exclua de forma
explícita.

Projetos autenticados são carregados e salvos no backend. O número de revisão
evita que uma edição baseada em uma versão antiga sobrescreva silenciosamente
uma atualização concorrente. Rascunhos locais ajudam a recuperar alterações
que ainda não foram salvas no servidor. Undo/Redo é limitado à sessão atual do
editor, não é um histórico persistido no banco.

Rascunhos de projetos já salvos usam uma chave local por usuário e UUID do
projeto; o editor só os lê depois que o backend confirma o acesso ao projeto.
Rascunhos de projetos novos e snapshots de preview usam chaves separadas por
usuário e organização, e a troca de organização reinicia o estado do editor.
Chaves antigas sem escopo de tenant não são carregadas automaticamente, pois
não há como atribuí-las com segurança a uma organização.

O catálogo de blocos tem grupos e variantes definidos por arquivos `.tsx` e
`.json`. Páginas prontas armazenam a composição por IDs estáveis. O builder
exporta uma página React ou um projeto ZIP com os arquivos necessários; a
exportação não cria um backend nem conecta automaticamente formulários,
autenticação ou pagamentos a serviços externos.

Projetos, publicações, o seletor de projetos, temas, sessões, respostas de
formulário e eventos usam carregamento paginado. O hook
`src/hooks/use-infinite-page.ts` mantém o estado das páginas em telas que
compartilham esse fluxo; `react-infinite-scroll-component` liga a rolagem ao
carregamento seguinte. As páginas usam tamanhos pequenos para coleções com
conteúdo mais pesado e 20 itens para a lista compacta de temas.

## API e sessão

As URLs dos serviços são centralizadas em `src/services/helpers/api-paths.ts`.
O frontend usa o contrato paginado do backend (`items`, `page`, `size`,
`totalElements`, `totalPages`, `hasNext` e `hasPrevious`) e envia o access token
nas chamadas privadas. O fluxo do navegador usa cookies HttpOnly para refresh
e usa o access token de curta duração em memória. Não armazene tokens de sessão
em `localStorage`.

No desenvolvimento, o proxy do Vite encaminha `/api` ao servidor configurado
em `vite.config.ts`. No Compose, o Nginx mantém o mesmo prefixo de API na rede
privada Docker. A URL e os headers de encaminhamento são configurados no
servidor web; a porta publicada no host não muda o endereço interno entre
containers.

## Desenvolvimento

```sh
npm ci
npm run dev
```

Os comandos disponíveis em `package.json` incluem:

```sh
npm run catalog:generate
npm run catalog:check
npm run typecheck
npm run lint
npm test
npm run build
```

`dev` e `build` geram o catálogo antes de iniciar. Typecheck, lint e testes
conferem se os arquivos gerados acompanham os metadados. Consulte
[`src/utils/README.md`](src/utils/README.md),
[`src/utils/blocks/README.md`](src/utils/blocks/README.md) e
[`scripts/README.md`](scripts/README.md) antes de alterar blocos, páginas prontas
ou a geração do catálogo.
