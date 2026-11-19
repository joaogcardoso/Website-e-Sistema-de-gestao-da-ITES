import { Link } from "react-router-dom"
import { useEffect, useState } from "react"
import { PageContent } from "@/pages/editor/components/PageContent"
import { definitions } from "@/utils/blocks"
import type { BuilderDocument } from "@/pages/editor/lib/document"
import { useAuth } from "@/context/auth/auth.context"
import "./ites.css"

export default function PortalPage() {
  const { accessToken } = useAuth()
  const [published, setPublished] = useState<BuilderDocument | null>(null)
  const [unavailable, setUnavailable] = useState(false)
  const [pagePermission, setPagePermission] = useState<{ token: string; allowed: boolean } | null>(null)
  useEffect(() => {
    fetch("/api/ites/pages/inicio")
      .then(response => response.ok ? response.json() : null)
      .then((result: { document: string } | null) => {
        if (!result) { setUnavailable(true); return }
        const raw = JSON.parse(result.document) as BuilderDocument
        setPublished({ ...raw, pages: raw.pages.map(page => ({ ...page, blocks: page.blocks.map(block => ({ ...block, props: { ...definitions.get(block.definitionId)?.defaults, ...block.props } })) })) })
      }).catch(() => { setPublished(null); setUnavailable(true) })
  }, [])
  useEffect(() => {
    if (!accessToken) return
    fetch("/api/ites/me/permissions", { headers: { Authorization: `Bearer ${accessToken}` } })
      .then(response => response.ok ? response.json() as Promise<string[]> : [])
      .then(permissions => setPagePermission({ token: accessToken, allowed: permissions.includes("builder.access") && permissions.includes("builder.pages.edit") }))
      .catch(() => setPagePermission({ token: accessToken, allowed: false }))
  }, [accessToken])
  const canEditPages = Boolean(accessToken && pagePermission?.token === accessToken && pagePermission.allowed)
  return <main className="ites-page">
    <header className="ites-header"><strong>ITES · IFRJ Niterói</strong><nav>{canEditPages && <Link to="/administracao/editor">Editar página inicial</Link>}<Link to="/entrar">Entrar</Link><Link to="/cadastro">Solicitar acesso</Link><Link to="/demandas">Minhas demandas</Link></nav></header>
    {published?.pages[0] ? <PageContent page={published.pages[0]} /> : <section className="ites-card" role="status"><h1>{unavailable ? "Portal temporariamente indisponível" : "Carregando portal institucional…"}</h1><p>{unavailable ? "A equipe responsável está verificando a publicação institucional." : ""}</p><Link to="/entrar">Acessar a plataforma</Link></section>}
  </main>
}
