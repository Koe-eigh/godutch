import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'
import type { GroupInput } from '../api'
import styles from '../styles/Page.module.css'

export default function HomePage() {
  const navigate = useNavigate()
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [memberInputs, setMemberInputs] = useState<string[]>([''])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string>('')
  const [groupIdInput, setGroupIdInput] = useState('')

  function updateMemberName(i: number, value: string) {
    setMemberInputs((prev) => prev.map((m, idx) => (idx === i ? value : m)))
  }
  function addMemberField() {
    setMemberInputs((prev) => [...prev, ''])
  }
  function removeMemberField(i: number) {
    setMemberInputs((prev) => prev.filter((_, idx) => idx !== i))
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    const members = memberInputs
      .map((n) => n.trim())
      .filter(Boolean)
      .map((n) => ({ name: n }))
    if (!name.trim()) {
      setError('グループ名を入力してください')
      return
    }
    if (members.length === 0) {
      setError('メンバーを1人以上追加してください')
      return
    }
    const body: GroupInput = { name: name.trim(), description: description.trim() || undefined, members }
    setLoading(true)
    try {
      const group = await api.createGroup(body)
      navigate(`/groups/${group.id}`)
    } catch (e: any) {
      setError(e?.message || '作成に失敗しました')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className={styles.section}>
      <h2 className={styles.h2}>新しいグループを作成</h2>
      <div className={styles.card}>
        <form onSubmit={onSubmit} className={styles.form}>
          <div className={styles.field}>
            <label className={styles.label}>グループ名</label>
            <input className={styles.input} value={name} onChange={(e) => setName(e.target.value)} placeholder="例: 旅行メンバー" />
          </div>
          <div className={styles.field}>
            <label className={styles.label}>説明 (任意)</label>
            <input className={styles.input} value={description} onChange={(e) => setDescription(e.target.value)} placeholder="メモ" />
          </div>
          <fieldset className={styles.fieldset}>
            <legend className={styles.legend}>メンバー</legend>
            {memberInputs.map((m, i) => (
              <div key={i} className={styles.row}>
                <input className={styles.input} value={m} onChange={(e) => updateMemberName(i, e.target.value)} placeholder={`メンバー${i + 1}の名前`} />
                <button type="button" className={styles.ghostBtn} onClick={() => removeMemberField(i)}>削除</button>
              </div>
            ))}
            <button type="button" className={styles.ghostBtn} onClick={addMemberField}>+ メンバーを追加</button>
          </fieldset>
          {error && <p role="alert" className={styles.error}>{error}</p>}
          <div className={styles.actions}>
            <button type="submit" className={styles.primaryBtn} disabled={loading}>{loading ? '作成中...' : 'グループを作成'}</button>
          </div>
        </form>
      </div>

      <hr className={styles.hr} />

      <h2 className={styles.h2}>既存のグループに移動</h2>
      <div className={styles.card}>
        <form onSubmit={(e) => { e.preventDefault(); if (groupIdInput.trim()) navigate(`/groups/${groupIdInput.trim()}`) }} className={styles.form}>
          <div className={styles.field}>
            <label className={styles.label}>グループID</label>
            <input className={styles.input} value={groupIdInput} onChange={(e) => setGroupIdInput(e.target.value)} placeholder="groupId" />
          </div>
          <div className={styles.actions}>
            <button className={styles.secondaryBtn} type="submit">移動</button>
          </div>
        </form>
      </div>
    </section>
  )
}
