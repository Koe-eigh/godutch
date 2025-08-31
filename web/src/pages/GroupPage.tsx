import { Link, Outlet, useParams, generatePath } from 'react-router-dom'
import { useState, useRef } from 'react'
import { useGroup } from '../hooks/useGroup'
import styles from '../styles/Page.module.css'

export default function GroupPage() {
  const { groupId = '' } = useParams()
  const { group, loading, error } = useGroup(groupId)
  const [copied, setCopied] = useState(false)
  const [manualCopy, setManualCopy] = useState(false)
  const manualCopyRef = useRef<HTMLInputElement | null>(null)
  const groupPath = generatePath('/groups/:groupId', { groupId })
  const pageUrl = typeof window !== 'undefined' ? new URL(groupPath, window.location.origin).toString() : groupPath

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

  if (loading) return <p>読み込み中...</p>
  if (error)
    return (
      <div>
        <p role="alert" className={styles.error}>
          {error}
        </p>
        <p>
          <Link to="/">トップへ戻る</Link>
        </p>
      </div>
    )
  if (!group) return null

  return (
    <section className={styles.section}>
      <div className={styles.card}>
        <h2 className={styles.h2}>{group.name}</h2>
        {group.description && <p className={styles.desc}>{group.description}</p>}
        <div
          className={styles.desc}
          style={{ display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap' }}
        >
          <span>
            URL: <a href={pageUrl}>{pageUrl}</a>
          </span>
          <button type="button" className={styles.secondaryBtn} onClick={handleCopy}>コピー</button>
          {copied && <span role="status">コピーしました</span>}
        </div>
        {manualCopy && (
          <div className={styles.desc}>
            <p className={styles.help} role="status">コピーAPIが使用できません。下のURLは選択済みです。Cmd+C（または長押し）でコピーしてください。</p>
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
            {group.members.map((m) => (
              <li key={m.id} className={styles.listItem}>{m.name}</li>
            ))}
          </ul>
        </section>
        <nav className={styles.tabs}>
          <Link to="events" className={styles.tab}>支払いイベント</Link>
          <Link to="settlement" className={styles.tab}>清算結果</Link>
        </nav>
      </div>
      <Outlet />
    </section>
  )
}
