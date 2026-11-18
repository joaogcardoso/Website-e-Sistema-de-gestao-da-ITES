import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import { useAuth } from "@/context/auth/auth.context"
import "./ites.css"

type User = { id: string; first_name: string; last_name: string; email: string }
type Role = { id: string; code: string; name: string }
export default function GrantsPage() {
  const { accessToken } = useAuth()
  const [users, setUsers] = useState<User[]>([])
  const [roles, setRoles] = useState<Role[]>([])
  const [selected, setSelected] = useState("")
  const [assigned, setAssigned] = useState<Role[]>([])
  const [message, setMessage] = useState("")
  useEffect(() => {
    if (!accessToken) return
    Promise.all([fetch("/api/ites/users", { headers: { Authorization: `Bearer ${accessToken}` } }), fetch("/api/ites/roles", { headers: { Authorization: `Bearer ${accessToken}` } })])
      .then(async ([u,r]) => { if (!u.ok || !r.ok) throw new Error(); setUsers(await u.json() as User[]); setRoles(await r.json() as Role[]) })
      .catch(() => setMessage("Você não tem permissão para gerenciar papéis."))
  }, [accessToken])
  useEffect(() => { if (!selected) return; fetch(`/api/ites/roles/users/${selected}`, { headers: { Authorization: `Bearer ${accessToken}` } }).then(r => r.json() as Promise<Role[]>).then(setAssigned).catch(() => setAssigned([])) }, [selected, accessToken])
  async function change(role: Role, grant: boolean) {
    const response = await fetch(`/api/ites/roles/users/${selected}/${role.id}`, { method: grant ? "POST" : "DELETE", headers: { Authorization: `Bearer ${accessToken}` } })
    if (!response.ok) { setMessage("Não foi possível alterar o papel."); return }
    setAssigned(current => grant ? [...current,role] : current.filter(item => item.id !== role.id))
    setMessage("Permissões atualizadas.")
  }
  return <main className="ites-page"><header className="ites-header"><Link to="/">ITES · IFRJ Niterói</Link><Link to="/administracao/demandas">Demandas</Link></header><h1>Permissões de usuários</h1>
    <label>Usuário <select value={selected} onChange={event => setSelected(event.target.value)}><option value="">Selecione</option>{users.map(user => <option key={user.id} value={user.id}>{user.first_name} {user.last_name} ({user.email})</option>)}</select></label>
    {selected && roles.map(role => <div className="ites-card" key={role.id}><strong>{role.name}</strong> <span>({role.code})</span><button onClick={() => void change(role,!assigned.some(item => item.id === role.id))}>{assigned.some(item => item.id === role.id) ? "Revogar" : "Conceder"}</button></div>)}
    {message && <p role="status">{message}</p>}
  </main>
}
