import { generatePath, useOutletContext, useParams } from 'react-router-dom'
import { useRef, useState } from 'react'
import type { GroupOutletContext } from './GroupPage'
import styles from '../styles/Page.module.css'

function formatAmount(value?: string) {
  if (!value) return '0'
  try {
    return BigInt(value).toLocaleString('ja-JP')
  } catch {
    return value
  }
}

export default function GroupDashboardPage() {
  const { groupId = '' } = useParams()
  const { group } = useOutletContext<GroupOutletContext>()
  const [copied, setCopied] = useState(false)
  const [manualCopy, setManualCopy] = useState(false)
  const manualCopyRef = useRef<HTMLInputElement | null>(null)
  const groupPath = generatePath('/groups/:groupId', { groupId })
  const pageUrl =
    typeof window !== 'undefined'
      ? new URL(groupPath, window.location.origin).toString()
      : groupPath

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(pageUrl)
      setCopied(true)
      setManualCopy(false)
      setTimeout(() => setCopied(false), 1500)
    } catch {
      setManualCopy(true)
      const input = manualCopyRef.current
      if (input) {
        requestAnimationFrame(() => {
          input.focus()
          input.select()
        })
      }
    }
  }

  return (
    <section className={styles.section}>
      <div className={styles.card}>
        <h2 className={styles.h2}>{group.name}</h2>
        {group.description && <p className={styles.desc}>{group.description}</p>}
        <div className={styles.totalPaid}>
          <span className={styles.totalPaidLabel}>グループ全体の支払総額</span>
          <span className={styles.totalPaidAmount}>{formatAmount(group.totalPaidAmount)}円</span>
        </div>
        <div className={styles.shareRow}>
          <span className={styles.shareUrl}>
            URL: <a href={pageUrl}>{pageUrl}</a>
          </span>
          <button type="button" className={styles.secondaryBtn} onClick={handleCopy}>
            コピー
          </button>
          {copied && <span role="status">コピーしました</span>}
        </div>
        {manualCopy && (
          <div className={styles.desc}>
            <p className={styles.help} role="status">
              コピーAPIが使用できません。下のURLは選択済みです。Cmd+C（または長押し）でコピーしてください。
            </p>
            <input
              ref={manualCopyRef}
              value={pageUrl}
              readOnly
              className={styles.input}
              onFocus={(e) => e.currentTarget.select()}
            />
          </div>
        )}
        <section>
          <h3 className={styles.h3}>メンバー</h3>
          <ul className={styles.list}>
            {group.members.map((member) => (
              <li key={member.id} className={styles.listItem}>
                <span className={styles.name}>{member.name}</span>
                <span className={styles.amount}>{formatAmount(member.totalUsedAmount)}円</span>
              </li>
            ))}
          </ul>
        </section>
      </div>
    </section>
  )
}
