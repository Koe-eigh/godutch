import { useEffect, useState } from 'react'
import { Link, useParams, useSearchParams } from 'react-router-dom'
import { api } from '../api'
import type { PaymentEventsPage } from '../api'
import styles from '../styles/Page.module.css'

const EVENTS_PER_PAGE = 10

function parsePage(value: string | null): number {
  const parsed = Number.parseInt(value ?? '', 10)
  return Number.isNaN(parsed) ? 1 : Math.max(parsed, 1)
}

function parsePerPage(value: string | null): number {
  const parsed = Number.parseInt(value ?? '', 10)
  return Number.isNaN(parsed) ? EVENTS_PER_PAGE : Math.min(Math.max(parsed, 1), 100)
}

export default function EventsListPage() {
  const { groupId = '' } = useParams()
  const [searchParams, setSearchParams] = useSearchParams()
  const page = parsePage(searchParams.get('page'))
  const perPage = parsePerPage(searchParams.get('per_page'))
  const [pagination, setPagination] = useState<PaymentEventsPage | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (
      searchParams.get('page') === String(page) &&
      searchParams.get('per_page') === String(perPage)
    ) {
      return
    }
    const nextParams = new URLSearchParams(searchParams)
    nextParams.set('page', String(page))
    nextParams.set('per_page', String(perPage))
    setSearchParams(nextParams, { replace: true })
  }, [page, perPage, searchParams, setSearchParams])

  useEffect(() => {
    if (!groupId) return
    const controller = new AbortController()
    setLoading(true)
    setError('')
    setPagination(null)
    api
      .listEvents(groupId, page, perPage, controller.signal)
      .then(setPagination)
      .catch((e) => {
        if (e?.name !== 'AbortError') {
          setError(e?.message || '取得に失敗しました')
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setLoading(false)
        }
      })
    return () => controller.abort()
  }, [groupId, page, perPage])

  const events = pagination?.events ?? []
  const lastPage = pagination?.last_page ?? 1

  function moveToPage(nextPage: number) {
    const nextParams = new URLSearchParams(searchParams)
    nextParams.set('page', String(nextPage))
    nextParams.set('per_page', String(perPage))
    setSearchParams(nextParams)
  }

  return (
    <section className={styles.section}>
      <div className={styles.card}>
        <div className={styles.rowBetween}>
          <h3 className={styles.h3}>イベント一覧</h3>
          <Link to="new" className={styles.link}>
            + new
          </Link>
        </div>
        {loading && <p>読み込み中...</p>}
        {error && (
          <p role="alert" className={styles.error}>
            {error}
          </p>
        )}
        <ul className={styles.list}>
          {events.map((ev) => (
            <li key={ev.id} className={styles.listItem}>
              <Link to={`${ev.id}`} className={styles.link}>
                {ev.title}
              </Link>
            </li>
          ))}
          {events.length === 0 && !loading && !error && (
            <li className={styles.listItem}>
              {page === 1 ? 'イベントはまだありません' : 'このページにはイベントがありません'}
            </li>
          )}
        </ul>
        {(page > 1 || lastPage > 1) && (
          <nav className={styles.pagination} aria-label="イベント一覧のページ">
            <button
              type="button"
              className={styles.secondaryBtn}
              disabled={page === 1 || loading}
              onClick={() => moveToPage(Math.max(1, page - 1))}
            >
              前へ
            </button>
            <span aria-live="polite">{page} ページ</span>
            <button
              type="button"
              className={styles.secondaryBtn}
              disabled={!pagination || page >= lastPage || loading}
              onClick={() => moveToPage(page + 1)}
            >
              次へ
            </button>
          </nav>
        )}
      </div>
    </section>
  )
}
