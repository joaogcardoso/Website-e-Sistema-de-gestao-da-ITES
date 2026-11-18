import type { PageTemplate } from "./types"
import { themePresets } from "@/pages/editor/lib/project-theme"

export const PortfolioStudioPage: PageTemplate = {
  order: 28,
  name: "Portfólio imersivo",
  navbar: "navbar-vh-dock",
  blocks: ["vh-hero", "vh-about", "vh-experience", "vh-projects", "vh-contact"],
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
