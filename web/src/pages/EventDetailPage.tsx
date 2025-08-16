import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api'
import type { PaymentEvent } from '../api'
import { useGroup } from '../hooks/useGroup'
import pageStyles from '../styles/Page.module.css'

export default function EventDetailPage() {
  const { groupId = '', eventId = '' } = useParams()
  const navigate = useNavigate()
  const [event, setEvent] = useState<PaymentEvent | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const { group } = useGroup(groupId)

  const memberNameById = useMemo(() => {
    const map = new Map<string, string>()
    group?.members.forEach((m) => map.set(m.id, m.name))
    return map
  }, [group])

  useEffect(() => {
    if (!groupId || !eventId) return
    setLoading(true)
    setError('')
    api
      .getEvent(groupId, eventId)
      .then(setEvent)
      .catch((e) => setError(e?.message || '取得に失敗しました'))
      .finally(() => setLoading(false))
  }, [groupId, eventId])

  function formatAmount(v: string) {
    const num = Number(v)
    if (Number.isNaN(num)) return v
    return num.toLocaleString('ja-JP')
  }

  async function onDelete() {
    if (!confirm('このイベントを削除しますか？')) return
    try {
      await api.deleteEvent(groupId, eventId)
      navigate(`/groups/${groupId}/events`)
    } catch (e: any) {
      alert(e?.message || '削除に失敗しました')
    }
  }

  if (loading) return <p>読み込み中...</p>
  if (error) return <p role="alert" className={pageStyles.error}>{error}</p>
  if (!event) return null

  return (
    <section className={pageStyles.pageSection}>
      <div className={pageStyles.card}>
        <h3 className={pageStyles.title}>{event.title}</h3>
        {event.memo && <p className={pageStyles.memo}>{event.memo}</p>}

        <div className={pageStyles.infoGrid}>
          <div>
            <h4 className={pageStyles.blockTitle}>立替者</h4>
            <ul className={pageStyles.list}>
              {event.creditors.map((c) => (
                <li key={c.memberId} className={pageStyles.listItem}>
                  <span className={pageStyles.name}>
                    {memberNameById.get(c.memberId) || c.memberId}
                  </span>
                  <span className={pageStyles.amount}>{formatAmount(c.amount)}</span>
                </li>
              ))}
            </ul>
          </div>
          <div>
            <h4 className={pageStyles.blockTitle}>負担者</h4>
            <ul className={pageStyles.list}>
              {event.debtors.map((d) => (
                <li key={d.memberId} className={pageStyles.listItem}>
                  <span className={pageStyles.name}>
                    {memberNameById.get(d.memberId) || d.memberId}
                  </span>
                  <span className={pageStyles.amount}>{formatAmount(d.amount)}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>

        <div className={pageStyles.actions}>
          <button className={pageStyles.dangerButton} onClick={onDelete}>
            削除
          </button>
        </div>
      </div>
    </section>
  )
}
