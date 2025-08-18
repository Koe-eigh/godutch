import { useEffect, useMemo, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../api'
import type { Settlement } from '../api'
import { useGroup } from '../hooks/useGroup'
import styles from '../styles/Page.module.css'

export default function SettlementPage() {
  const { groupId = '' } = useParams()
  const { group } = useGroup(groupId)
  const [items, setItems] = useState<Settlement[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const memberNameById = useMemo(() => {
    const map = new Map<string, string>()
    group?.members.forEach((m) => map.set(m.id, m.name))
    return map
  }, [group])

  useEffect(() => {
    if (!groupId) return
    setLoading(true)
    setError('')
    api
      .getSettlement(groupId)
      .then(setItems)
      .catch((e) => setError(e?.message || '取得に失敗しました'))
      .finally(() => setLoading(false))
  }, [groupId])

  const fmt = (v: string) => {
    const n = Number(v)
    return Number.isNaN(n) ? v : n.toLocaleString('ja-JP')
  }

  return (
    <section className={styles.section}>
      <div className={styles.card}>
        <h3 className={styles.h3}>清算結果</h3>
        {loading && <p>計算中...</p>}
        {error && <p role="alert" className={styles.error}>{error}</p>}
        <ul className={styles.list}>
          {items.map((s, i) => (
            <li key={i} className={styles.listItem}>
              <span>
                {memberNameById.get(s.payerId) || s.payerId} → {memberNameById.get(s.payeeId) || s.payeeId}
              </span>
              <span>{fmt(s.amount)}</span>
            </li>
          ))}
          {items.length === 0 && !loading && <li className={styles.listItem}>清算はありません</li>}
        </ul>
      </div>
    </section>
  )
}
