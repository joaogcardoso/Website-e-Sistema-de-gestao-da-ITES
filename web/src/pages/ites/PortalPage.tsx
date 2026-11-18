import { Link } from "react-router-dom"
import { useEffect, useState } from "react"
import { PageContent } from "@/pages/editor/components/PageContent"
import { definitions } from "@/utils/blocks"
import type { BuilderDocument } from "@/pages/editor/lib/document"
import "./ites.css"

const sections = [
  { title: "Quem somos e como trabalhamos", slug: "quem-somos", text: "A ITES do IFRJ Campus Niterói apoia iniciativas de economia solidária com formação, articulação e acompanhamento." },
  { title: "Projetos e empreendimentos", slug: "projetos", text: "Conheça os grupos, coletivos e redes apoiados pela incubadora." },
  { title: "Notícias e agenda", slug: "noticias", text: "Acompanhe oficinas, encontros e atividades da ITES." },
  { title: "Publicações e biblioteca", slug: "publicacoes", text: "Consulte materiais de formação e produção de conhecimento." },
  { title: "Aplicativos e ferramentas", slug: "ferramentas", text: "Acesse recursos de apoio às iniciativas de economia solidária." },
  { title: "Redes sociais e contato", slug: "contato", text: "Entre em contato para construir parcerias e participar das atividades." },
]

export default function PortalPage() {
  const [published, setPublished] = useState<BuilderDocument | null>(null)
  useEffect(() => {
    fetch("/api/ites/pages/inicio")
      .then(response => response.ok ? response.json() : null)
      .then((result: { document: string } | null) => {
        if (!result) return
        const raw = JSON.parse(result.document) as BuilderDocument
        setPublished({ ...raw, pages: raw.pages.map(page => ({ ...page, blocks: page.blocks.map(block => ({ ...block, props: { ...definitions.get(block.definitionId)?.defaults, ...block.props } })) })) })
      }).catch(() => setPublished(null))
  }, [])
  return <main className="ites-page">
    <header className="ites-header"><strong>ITES · IFRJ Niterói</strong><nav><Link to="/entrar">Entrar</Link><Link to="/cadastro">Cadastrar</Link><Link to="/demandas">Minhas demandas</Link></nav></header>
    {published?.pages[0] ? <PageContent page={published.pages[0]} /> : <section className="ites-hero"><p>Incubadora Tecnológica de Economia Solidária</p><h1>Conexões para uma economia mais solidária</h1><p>Compartilhe sua necessidade com a ITES e acompanhe cada etapa do atendimento.</p><Link className="ites-button" to="/demandas">Enviar demanda</Link></section>}
    <div className="ites-grid">{sections.map(section => <section className="ites-card" key={section.title}><h2><Link to={`/institucional/${section.slug}`}>{section.title}</Link></h2><p>{section.text}</p></section>)}</div>
  </main>
}
