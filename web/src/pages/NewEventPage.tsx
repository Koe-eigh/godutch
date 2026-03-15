import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api'
import type { PaymentEventInput } from '../api'
import { EventForm } from '../components/EventForm'
import { useGroup } from '../hooks/useGroup'
import styles from '../styles/Page.module.css'

export default function NewEventPage() {
  const { groupId = '' } = useParams()
  const navigate = useNavigate()
  const { group, error: groupError } = useGroup(groupId)

  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  async function onSubmit(body: PaymentEventInput) {
    setLoading(true)
    setError('')
    try {
      await api.addEvent(groupId, body)
      navigate(`/groups/${groupId}/events`)
    } catch (e: any) {
      setError(e?.message || '作成に失敗しました')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className={styles.section}>
      <EventForm
        cancelTo=".."
        error={error}
        group={group}
        groupError={groupError}
        onSubmit={onSubmit}
        submitting={loading}
        submitLabel="作成"
        submittingLabel="作成中..."
        title="新規イベント"
      />
    </section>
  )
}
