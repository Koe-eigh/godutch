import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api'
import type { PaymentEvent, PaymentEventInput } from '../api'
import { EventForm } from '../components/EventForm'
import { useGroup } from '../hooks/useGroup'
import styles from '../styles/Page.module.css'

export default function EditEventPage() {
  const { groupId = '', eventId = '' } = useParams()
  const navigate = useNavigate()
  const { group, error: groupError } = useGroup(groupId)

  const [event, setEvent] = useState<PaymentEvent | null>(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  const initialValue = useMemo(
    () =>
      event
        ? {
            title: event.title,
            memo: event.memo,
            creditors: event.creditors,
            debtors: event.debtors,
          }
        : undefined,
    [event],
  )

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

  async function handleSubmit(body: PaymentEventInput) {
    setError('')
    setSubmitting(true)

    try {
      await api.updateEvent(groupId, eventId, body)
      navigate(`/groups/${groupId}/events/${eventId}`)
    } catch (e: any) {
      setError(e?.message || '更新に失敗しました')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) return <p>読み込み中...</p>
  if (error && !event) return <p role="alert" className={styles.error}>{error}</p>
  if (!event) return null

  return (
    <section className={styles.section}>
      <EventForm
        cancelTo={`/groups/${groupId}/events/${eventId}`}
        error={error}
        group={group}
        groupError={groupError}
        initialValue={initialValue}
        onSubmit={handleSubmit}
        submitting={submitting}
        submitLabel="更新"
        submittingLabel="更新中..."
        title="イベントを更新"
      />
    </section>
  )
}
