import { useEffect, useMemo, useState } from "react"
import { Link } from "react-router-dom"
import { useAuth } from "@/context/auth/auth.context"
import { initialDocument, reduceDocument, uid, type Action, type Block, type BuilderDocument } from "@/pages/editor/lib/document"
import { PageContent } from "@/pages/editor/components/PageContent"
import { PropertyEditor } from "@/pages/editor/components/PropertyEditor"
import { definitions } from "@/utils/blocks"
import type { BlockProps } from "@/utils/blocks"
import "./ites.css"

type PageInfo = { slug: string; title: string }
type Draft = { revision: number | null; version_id: string | null; document: string | null }
const editableCategories = new Set(["hero", "about", "features", "portfolio", "blog", "download", "contact", "social", "cta", "testimonials", "team", "gallery", "faq", "timeline", "logos"])
const editableBlocks = [...definitions.values()].filter(definition => editableCategories.has(definition.category))

function normalize(raw: BuilderDocument): BuilderDocument {
  return { ...raw, pages: raw.pages.map(page => ({ ...page, blocks: page.blocks.map(block => ({ ...block, props: { ...definitions.get(block.definitionId)?.defaults, ...block.props } })) })) }
}

function safeDocument(raw: BuilderDocument): BuilderDocument {
  return { version: 1, name: raw.name.slice(0, 180), activePageId: raw.activePageId, pages: raw.pages.slice(0, 1).map(page => ({ id: page.id, name: page.name.slice(0, 180), slug: page.slug, blocks: page.blocks.slice(0, 40).map(block => ({ id: block.id, definitionId: block.definitionId, props: block.props as BlockProps })) })) }
}

export default function InstitutionalEditorPage() {
  const { accessToken } = useAuth()
  const [pages, setPages] = useState<PageInfo[]>([])
  const [slug, setSlug] = useState("inicio")
  const [document, setDocument] = useState<BuilderDocument>(() => initialDocument())
  const [revision, setRevision] = useState(0)
  const [versionId, setVersionId] = useState<string | null>(null)
  const [message, setMessage] = useState("")
  const [preview, setPreview] = useState(false)
  const [selectedBlockId, setSelectedBlockId] = useState<string | null>(null)
  const [selectedDefinition, setSelectedDefinition] = useState(editableBlocks[0]?.id ?? "hero-classic")
  const activePage = document.pages[0]
  const selectedBlock = useMemo(() => activePage?.blocks.find(block => block.id === selectedBlockId) ?? null, [activePage, selectedBlockId])

  useEffect(() => { fetch("/api/ites/pages").then(response => response.json() as Promise<PageInfo[]>).then(setPages).catch(() => setMessage("Não foi possível carregar as páginas.")) }, [])
  useEffect(() => {
    if (!accessToken) return
    let active = true
    fetch(`/api/ites/pages/${slug}/draft`, { headers: { Authorization: `Bearer ${accessToken}` } })
      .then(async response => { if (!response.ok) throw new Error(); return response.json() as Promise<Draft> })
      .then(draft => {
        if (!active) return
        const loaded = draft.document ? normalize(JSON.parse(draft.document) as BuilderDocument) : initialDocument()
        setDocument(loaded); setRevision(draft.revision ?? 0); setVersionId(draft.version_id); setSelectedBlockId(loaded.pages[0]?.blocks[0]?.id ?? null); setMessage("")
      })
      .catch(() => { if (active) setMessage("Você não tem permissão para editar esta página.") })
    return () => { active = false }
  }, [accessToken, slug])

  function dispatch(action: Action) { setDocument(current => reduceDocument(current, action)) }
  function addBlock() {
    const id = uid()
    dispatch({ type: "add", id, definitionId: selectedDefinition })
    setSelectedBlockId(id)
  }
  async function save() {
    const response = await fetch(`/api/ites/pages/${slug}/draft`, { method: "POST", headers: { Authorization: `Bearer ${accessToken}`, "Content-Type": "application/json" }, body: JSON.stringify({ revision, document: safeDocument(document) }) })
    if (!response.ok) { setMessage(response.status === 409 ? "Rascunho alterado em outra sessão. Recarregue a página." : "Não foi possível salvar o rascunho."); return }
    const result = await response.json() as { versionId: string; revision: number }
    setRevision(result.revision); setVersionId(result.versionId); setMessage("Rascunho salvo.")
  }
  async function publish() {
    if (!versionId) { setMessage("Salve o rascunho antes de publicar."); return }
    const response = await fetch(`/api/ites/pages/${slug}/publish`, { method: "POST", headers: { Authorization: `Bearer ${accessToken}`, "Content-Type": "application/json" }, body: JSON.stringify({ versionId }) })
    setMessage(response.ok ? "Versão publicada no portal." : "Sem permissão para publicar ou versão inválida.")
  }
  async function restorePublished() {
    const response = await fetch(`/api/ites/pages/${slug}/draft/restore-published`, { method: "POST", headers: { Authorization: `Bearer ${accessToken}` } })
    if (!response.ok) { setMessage("Não foi possível recuperar a versão publicada."); return }
    const draft = await response.json() as { versionId: string; revision: number }
    const result = await fetch(`/api/ites/pages/${slug}/draft`, { headers: { Authorization: `Bearer ${accessToken}` } })
    if (result.ok) {
      const restored = await result.json() as Draft
      if (restored.document) setDocument(normalize(JSON.parse(restored.document) as BuilderDocument))
    }
    setRevision(draft.revision); setVersionId(draft.versionId); setMessage("A versão publicada foi restaurada como rascunho.")
  }

  return <main className="ites-page">
    <header className="ites-header"><Link to="/">ITES · IFRJ Niterói</Link><nav><Link to="/administracao/demandas">Demandas</Link><Link to="/administracao/indicadores">Indicadores</Link></nav></header>
    <p className="ites-eyebrow">Conteúdo institucional</p><h1>Editor de páginas</h1>
    <p>As alterações ficam em rascunho até serem publicadas. A página pública sempre usa a última versão publicada.</p>
    <label>Página institucional<select value={slug} onChange={event => setSlug(event.target.value)}>{pages.map(page => <option key={page.slug} value={page.slug}>{page.title}</option>)}</select></label>
    <div className="ites-toolbar"><label>Bloco<select aria-label="Bloco para adicionar" value={selectedDefinition} onChange={event => setSelectedDefinition(event.target.value)}>{editableBlocks.map(definition => <option key={definition.id} value={definition.id}>{definition.name}</option>)}</select></label><button onClick={addBlock}>Adicionar seção</button><button onClick={() => setPreview(value => !value)}>{preview ? "Voltar à edição" : "Prévia"}</button><button onClick={() => void save()}>Salvar rascunho</button><button onClick={() => void publish()}>Publicar</button><button onClick={() => void restorePublished()}>Restaurar publicada</button></div>
    {message && <p role="status">{message}</p>}
    {preview ? activePage && <PageContent page={activePage} /> : <div className="ites-editor-layout">
      <section className="ites-card"><h2>Estrutura da página</h2>{activePage?.blocks.map((block,index) => <article className={`ites-block-row ${selectedBlockId === block.id ? "is-selected" : ""}`} key={block.id}><button className="ites-block-select" onClick={() => setSelectedBlockId(block.id)}>{index + 1}. {definitions.get(block.definitionId)?.name ?? block.definitionId}</button><div><button disabled={index === 0} aria-label="Mover seção para cima" onClick={() => dispatch({ type: "move", id: block.id, index: index - 1 })}>↑</button><button disabled={index === activePage.blocks.length - 1} aria-label="Mover seção para baixo" onClick={() => dispatch({ type: "move", id: block.id, index: index + 1 })}>↓</button><button aria-label="Remover seção" onClick={() => { dispatch({ type: "remove", id: block.id }); setSelectedBlockId(null) }}>Remover</button></div></article>)}{!activePage?.blocks.length && <p>Adicione uma seção para começar a montar esta página.</p>}</section>
      <section className="ites-card"><h2>Propriedades</h2>{selectedBlock ? <PropertyEditor block={selectedBlock as Block} dispatch={dispatch} pages={document.pages} /> : <p>Selecione uma seção para editar textos, imagens, links e opções visuais.</p>}</section>
    </div>}
  </main>
}
