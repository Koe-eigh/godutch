import { useEffect, useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import type { Group, PaymentEventInput } from '../api'
import styles from '../styles/Page.module.css'

type EventFormProps = {
  cancelTo: string
  error: string
  group?: Group
  groupError?: string
  initialValue?: PaymentEventInput
  submitting: boolean
  submitLabel: string
  submittingLabel: string
  title: string
  // eslint-disable-next-line no-unused-vars
  onSubmit: (...args: [PaymentEventInput]) => Promise<void>
}

export function EventForm({
  cancelTo,
  error,
  group,
  groupError,
  initialValue,
  submitting,
  submitLabel,
  submittingLabel,
  title,
  onSubmit,
}: EventFormProps) {
  const [eventTitle, setEventTitle] = useState('')
  const [memo, setMemo] = useState('')
  const [creditors, setCreditors] = useState<string[]>([])
  const [debtors, setDebtors] = useState<string[]>([])
  const [validationError, setValidationError] = useState('')

  useEffect(() => {
    if (!group) return

    const creditorAmounts = new Map(initialValue?.creditors.map((item) => [item.memberId, item.amount]) ?? [])
    const debtorAmounts = new Map(initialValue?.debtors.map((item) => [item.memberId, item.amount]) ?? [])

    setEventTitle(initialValue?.title ?? '')
    setMemo(initialValue?.memo ?? '')
    setCreditors(group.members.map((member) => creditorAmounts.get(member.id) ?? ''))
    setDebtors(group.members.map((member) => debtorAmounts.get(member.id) ?? ''))
    setValidationError('')
  }, [group, initialValue])

  function onChangeCreditor(index: number, value: string) {
    setCreditors((prev) => {
      const next = prev.slice()
      next[index] = value
      return next
    })
  }

  function onChangeDebtor(index: number, value: string) {
    setDebtors((prev) => {
      const next = prev.slice()
      next[index] = value
      return next
    })
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setValidationError('')

    if (!group) return
    if (!eventTitle.trim()) {
      setValidationError('タイトルを入力してください')
      return
    }

    const creditorsArr = group.members
      .map((member, index) => ({ memberId: member.id, amount: (creditors[index] || '').trim() }))
      .filter((creditor) => creditor.amount)
    const debtorsArr = group.members
      .map((member, index) => ({ memberId: member.id, amount: (debtors[index] || '').trim() }))
      .filter((debtor) => debtor.amount)

    if (creditorsArr.length === 0 || debtorsArr.length === 0) {
      setValidationError('少なくとも1人の立替者と1人の負担額を入力してください')
      return
    }

    await onSubmit({
      title: eventTitle.trim(),
      memo: memo.trim() || undefined,
      creditors: creditorsArr,
      debtors: debtorsArr,
    })
  }

  return (
    <div className={styles.card}>
      <h3 className={styles.h3}>{title}</h3>
      {!group ? (
        groupError ? <p role="alert" className={styles.error}>{groupError}</p> : <p>グループ情報を読込中...</p>
      ) : (
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label className={styles.label}>タイトル</label>
            <input
              className={styles.input}
              value={eventTitle}
              onChange={(e) => setEventTitle(e.target.value)}
              placeholder="例: 昼食"
            />
            <p className={styles.help}>イベントの名称を入力してください。</p>
          </div>

          <div className={styles.field}>
            <label className={styles.label}>メモ (任意)</label>
            <input
              className={styles.input}
              value={memo}
              onChange={(e) => setMemo(e.target.value)}
              placeholder="備考"
            />
          </div>

          <fieldset className={styles.fieldset}>
            <legend className={styles.legend}>立替者 金額</legend>
            {group.members.map((member, index) => (
              <div key={member.id} className={styles.row}>
                <label className={styles.nameLabel}>{member.name}</label>
                <input
                  className={styles.input}
                  type="number"
                  inputMode="decimal"
                  placeholder="0"
                  value={creditors[index] || ''}
                  onChange={(e) => onChangeCreditor(index, e.target.value)}
                />
              </div>
            ))}
            <p className={styles.help}>実際に立替えた金額を入力してください（空欄可）。</p>
          </fieldset>

          <fieldset className={styles.fieldset}>
            <legend className={styles.legend}>各負担金額</legend>
            {group.members.map((member, index) => (
              <div key={member.id} className={styles.row}>
                <label className={styles.nameLabel}>{member.name}</label>
                <input
                  className={styles.input}
                  type="text"
                  inputMode="numeric"
                  pattern="[0-9]*"
                  placeholder="0"
                  value={debtors[index] || ''}
                  onChange={(e) => onChangeDebtor(index, e.target.value.replace(/[^0-9]/g, ''))}
                />
              </div>
            ))}
            <p className={styles.help}>各メンバーが負担すべき金額を入力してください（空欄可）。</p>
          </fieldset>

          {(validationError || error) && (
            <p role="alert" className={styles.error}>
              {validationError || error}
            </p>
          )}

          <div className={styles.actions}>
            <button type="submit" className={styles.primaryBtn} disabled={submitting}>
              {submitting ? submittingLabel : submitLabel}
            </button>
            <Link to={cancelTo} className={styles.secondaryBtn} role="button">
              キャンセル
            </Link>
          </div>
        </form>
      )}
    </div>
  )
}
