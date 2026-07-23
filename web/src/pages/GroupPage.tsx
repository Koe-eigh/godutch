import { Link, NavLink, Outlet, useLocation, useParams } from 'react-router-dom'
import type { Group } from '../api'
import { useGroup } from '../hooks/useGroup'
import styles from '../styles/Page.module.css'

export interface GroupOutletContext {
  group: Group
}

export default function GroupPage() {
  const { groupId = '' } = useParams()
  const location = useLocation()
  const { group, loading, error } = useGroup(groupId, location.key)

  if (loading && !group) return <p>読み込み中...</p>
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
    <>
      <div className={styles.groupNavContainer}>
        <nav className={styles.tabs} aria-label={`${group.name}のメニュー`}>
          <NavLink to="." end className={styles.tab}>
            ダッシュボード
          </NavLink>
          <NavLink to="events" className={styles.tab}>
            支払いイベント
          </NavLink>
          <NavLink to="settlement" className={styles.tab}>
            精算結果
          </NavLink>
        </nav>
      </div>
      <Outlet context={{ group } satisfies GroupOutletContext} />
    </>
  )
}
