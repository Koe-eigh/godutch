import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api'
import type { PaymentEvent } from '../api'
import styles from '../styles/Page.module.css'

export default function EventsListPage() {
  const { groupId = '' } = useParams()
  const [events, setEvents] = useState<PaymentEvent[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!groupId) return
    setLoading(true)
    setError('')
    api
      .listEvents(groupId)
      .then(setEvents)
      .catch((e) => setError(e?.message || '取得に失敗しました'))
      .finally(() => setLoading(false))
  }, [groupId])

  return (
    <section className={styles.section}>
      <div className={styles.card}>
        <div className={styles.rowBetween}>
          <h3 className={styles.h3}>イベント一覧</h3>
          <Link to="new" className={styles.link}>+ new</Link>
        </div>
        {loading && <p>読み込み中...</p>}
        {error && <p role="alert" className={styles.error}>{error}</p>}
        <ul className={styles.list}>
          {events.map((ev) => (
            <li key={ev.id} className={styles.listItem}>
              <Link to={`${ev.id}`} className={styles.link}>{ev.title}</Link>
            </li>
          ))}
          {events.length === 0 && !loading && <li className={styles.listItem}>イベントはまだありません</li>}
        </ul>
      </div>
    </section>
  )
}
