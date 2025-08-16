import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api'
import type { PaymentEventInput } from '../api'
import { useGroup } from '../hooks/useGroup'
import styles from '../styles/Page.module.css'

export default function NewEventPage() {
  const { groupId = '' } = useParams()
  const navigate = useNavigate()
  const { group } = useGroup(groupId)

  const [title, setTitle] = useState('')
  const [memo, setMemo] = useState('')
  // 各メンバーごとに独立した値を保持するため、indexベースの配列で管理
  const [creditors, setCreditors] = useState<string[]>([])
  const [debtors, setDebtors] = useState<string[]>([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  // グループ取得後に入力配列を初期化（メンバー数に合わせる）
  useEffect(() => {
    if (!group) return
    const len = group.members.length
    setCreditors(Array(len).fill(''))
    setDebtors(Array(len).fill(''))
  }, [group])

  // 立替者の金額変更（indexで更新）
  function onChangeCreditor(i: number, value: string) {
    setCreditors((prev) => {
      const next = prev.slice()
      next[i] = value
      return next
    })
  }

  // 負担者の金額変更（indexで更新）
  function onChangeDebtor(i: number, value: string) {
    setDebtors((prev) => {
      const next = prev.slice()
      next[i] = value
      return next
    })
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    if (!title.trim()) {
      setError('タイトルを入力してください')
      return
    }
    const members = group?.members ?? []
    const creditorsArr = members
      .map((m, i) => ({ memberId: m.id, amount: (creditors[i] || '').trim() }))
      .filter((c) => c.amount)
    const debtorsArr = members
      .map((m, i) => ({ memberId: m.id, amount: (debtors[i] || '').trim() }))
      .filter((d) => d.amount)

    if (creditorsArr.length === 0 || debtorsArr.length === 0) {
      setError('少なくとも1人の立替者と1人の負担者を入力してください')
      return
    }

    const body: PaymentEventInput = {
      title: title.trim(),
      memo: memo.trim() || undefined,
      creditors: creditorsArr,
      debtors: debtorsArr,
    }

    setLoading(true)
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
      <div className={styles.card}>
        <h3 className={styles.h3}>新規イベント</h3>
        {!group ? (
          <p>グループ情報を読込中...</p>
        ) : (
          <form onSubmit={onSubmit} className={styles.form}>
            <div className={styles.field}>
              <label className={styles.label}>タイトル</label>
              <input className={styles.input} value={title} onChange={(e) => setTitle(e.target.value)} placeholder="例: 昼食" />
              <p className={styles.help}>イベントの名称を入力してください。</p>
            </div>
            <div className={styles.field}>
              <label className={styles.label}>メモ (任意)</label>
              <input className={styles.input} value={memo} onChange={(e) => setMemo(e.target.value)} placeholder="備考" />
            </div>

            <fieldset className={styles.fieldset}>
              <legend className={styles.legend}>立替者 金額</legend>
              {group.members.map((m, i) => (
                <div key={`${m.id}-${i}`} className={styles.row}>
                  <label className={styles.nameLabel}>{m.name}</label>
                  <input
                    className={styles.input}
                    type="number"
                    inputMode="decimal"
                    placeholder="0"
                    value={creditors[i] || ''}
                    onChange={(e) => onChangeCreditor(i, e.target.value)}
                  />
                </div>
              ))}
              <p className={styles.help}>実際に立替えた金額を入力してください（空欄可）。</p>
            </fieldset>

            <fieldset className={styles.fieldset}>
              <legend className={styles.legend}>負担者 金額</legend>
              {group.members.map((m, i) => (
                <div key={`${m.id}-${i}`} className={styles.row}>
                  <label className={styles.nameLabel}>{m.name}</label>
                  <input
                    className={styles.input}
                    type="text"
                    inputMode="numeric"
                    pattern="[0-9]*"
                    placeholder="0"
                    value={debtors[i] || ''}
                    onChange={(e) => onChangeDebtor(i, e.target.value.replace(/[^0-9]/g, ''))}
                  />
                </div>
              ))}
              <p className={styles.help}>各メンバーが負担すべき金額を入力してください（空欄可）。</p>
            </fieldset>

            {error && (
              <p role="alert" className={styles.error}>
                {error}
              </p>
            )}

            <div className={styles.actions}>
              <button type="submit" className={styles.primaryBtn} disabled={loading}>
                {loading ? '作成中...' : '作成'}
              </button>
              <Link to=".." className={styles.secondaryBtn} role="button">キャンセル</Link>
            </div>
          </form>
        )}
      </div>
    </section>
  )
}
