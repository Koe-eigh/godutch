import { useEffect, useState } from 'react'
import { api } from '../api'
import type { Group } from '../api'

export function useGroup(groupId?: string, refreshKey?: string) {
  const [group, setGroup] = useState<Group | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string>('')

  useEffect(() => {
    if (!groupId) return
    const ctrl = new AbortController()
    let active = true
    setLoading(true)
    setError('')
    api
      .getGroup(groupId, ctrl.signal)
      .then((nextGroup) => {
        if (active) setGroup(nextGroup)
      })
      .catch((e) => {
        if (!active || (e instanceof Error && e.name === 'AbortError')) return
        setError(e?.message || '読み込みに失敗しました')
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
      ctrl.abort()
    }
  }, [groupId, refreshKey])

  return { group, loading, error }
}
