import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import { useAuth } from "@/context/auth/auth.context"
import { initialDocument, reduceDocument, uid, type BuilderDocument } from "@/pages/editor/lib/document"
import { PageContent } from "@/pages/editor/components/PageContent"
import { definitions, type BlockProps } from "@/utils/blocks"
import "./ites.css"

type PageInfo = { slug: string; title: string }
type Draft = { revision: number | null; version_id: string | null; document: string | null }
const allowed = ["hero-classic", "features-classic", "contact-channels"]
const fields = ["eyebrow", "title", "description", "button", "href"] as const

function normalize(raw: BuilderDocument): BuilderDocument {
  return { ...raw, pages: raw.pages.map(page => ({ ...page, blocks: page.blocks.map(block => ({ ...block, props: { ...definitions.get(block.definitionId)?.defaults, ...block.props } })) })) }
}
function safeDocument(raw: BuilderDocument): BuilderDocument {
  return { version: 1, name: raw.name, activePageId: raw.activePageId, pages: raw.pages.slice(0,1).map(page => ({ id: page.id, name: page.name, slug: page.slug, blocks: page.blocks.map(block => ({ id: block.id, definitionId: block.definitionId, props: Object.fromEntries(fields.filter(key => typeof block.props[key] === "string").map(key => [key, block.props[key]])) as BlockProps })) })) }
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
  const [selectedBlock, setSelectedBlock] = useState(allowed[0])

  useEffect(() => { fetch("/api/ites/pages").then(r => r.json() as Promise<PageInfo[]>).then(setPages).catch(() => setMessage("Não foi possível carregar as páginas.")) }, [])
  useEffect(() => {
    if (!accessToken) return
    fetch(`/api/ites/pages/${slug}/draft`, { headers: { Authorization: `Bearer ${accessToken}` } })
      .then(async r => { if (!r.ok) throw new Error(); return r.json() as Promise<Draft> })
      .then(draft => { setDocument(draft.document ? normalize(JSON.parse(draft.document) as BuilderDocument) : initialDocument()); setRevision(draft.revision ?? 0); setVersionId(draft.version_id); setMessage("") })
      .catch(() => setMessage("Você não tem permissão para editar esta página."))
  }, [accessToken, slug])

  function add(definitionId: string) { setDocument(current => reduceDocument(current, { type: "add", id: uid(), definitionId })) }
  function update(blockId: string, key: typeof fields[number], value: string) { setDocument(current => ({ ...current, pages: current.pages.map(page => ({ ...page, blocks: page.blocks.map(block => block.id === blockId ? { ...block, props: { ...block.props, [key]: value } } : block) })) })) }
  async function save() {
    const response = await fetch(`/api/ites/pages/${slug}/draft`, { method: "POST", headers: { Authorization: `Bearer ${accessToken}`, "Content-Type": "application/json" }, body: JSON.stringify({ revision, document: safeDocument(document) }) })
    if (!response.ok) { setMessage(response.status === 409 ? "Rascunho alterado em outra sessão. Recarregue a página." : "Não foi possível salvar."); return }
    const result = await response.json() as { versionId: string; revision: number }
    setRevision(result.revision); setVersionId(result.versionId); setMessage("Rascunho salvo.")
  }
  async function publish() {
    if (!versionId) { setMessage("Salve o rascunho antes de publicar."); return }
    const response = await fetch(`/api/ites/pages/${slug}/publish`, { method: "POST", headers: { Authorization: `Bearer ${accessToken}`, "Content-Type": "application/json" }, body: JSON.stringify({ versionId }) })
    setMessage(response.ok ? "Página publicada." : "Sem permissão para publicar ou versão inválida.")
  }

  async function unpublish() {
    const response = await fetch(`/api/ites/pages/${slug}/published`, { method: "DELETE", headers: { Authorization: `Bearer ${accessToken}` } })
    setMessage(response.ok ? "Página retirada do ar." : "Não foi possível retirar a página do ar.")
  }

  return <main className="ites-page"><header className="ites-header"><Link to="/">ITES · IFRJ Niterói</Link><nav><Link to="/demandas">Demandas</Link></nav></header><h1>Editor institucional</h1>
    <label>Página <select value={slug} onChange={event => setSlug(event.target.value)}>{pages.map(page => <option key={page.slug} value={page.slug}>{page.title}</option>)}</select></label>
    <div className="ites-toolbar"><select aria-label="Bloco para adicionar" value={selectedBlock} onChange={event => setSelectedBlock(event.target.value)}>{allowed.map(id => <option key={id} value={id}>{definitions.get(id)?.name ?? id}</option>)}</select><button onClick={() => add(selectedBlock)}>Adicionar bloco</button><button onClick={() => setPreview(value => !value)}>{preview ? "Editar" : "Prévia"}</button><button onClick={() => void save()}>Salvar rascunho</button><button onClick={() => void publish()}>Publicar</button><button onClick={() => void unpublish()}>Retirar do ar</button></div>
    {message && <p role="status">{message}</p>}
    {!preview && document.pages[0]?.blocks.map(block => <section className="ites-card" key={block.id}><h2>{definitions.get(block.definitionId)?.name}</h2>{fields.map(key => <label key={key}>{key}<input value={typeof block.props[key] === "string" ? block.props[key] as string : ""} onChange={event => update(block.id,key,event.target.value)} /></label>)}<button onClick={() => setDocument(current => reduceDocument(current,{ type: "remove", id: block.id }))}>Remover</button></section>)}
    {preview && document.pages[0] && <PageContent page={document.pages[0]} />}
  </main>
}
