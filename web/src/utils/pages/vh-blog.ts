import type { PageTemplate } from "./types"
import { themePresets } from "@/pages/editor/lib/project-theme"

export const VhBlogPage: PageTemplate = {
  order: 26,
  name: "Blog",
  navbar: "navbar-vh-dock",
  navbarItems: [
    { id: "vh-blog-nav-home", title: "Início", description: "", href: "/", value: "home", features: [] },
    { id: "vh-blog-nav-list", title: "Artigos", description: "", href: "#artigos", value: "blog", features: [] },
    { id: "vh-blog-nav-contact", title: "Contato", description: "", href: "#contato", value: "contact", features: [] },
  ],
  blocks: ["vh-blog-index", "vh-contact"],
  theme: {
    preset: "VH Portfolio",
    mode: "dark",
    light: { ...themePresets["VH Portfolio"].light },
    dark: { ...themePresets["VH Portfolio"].dark },
    headingFont: "Roboto Variable",
    bodyFont: "Nunito Sans Variable",
    headingSize: 100,
    bodySize: 100,
    lineHeight: 1.5,
    letterSpacing: 0,
    radius: 8,
    shadow: 10,
  },
}
