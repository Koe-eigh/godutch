import { useEffect, useState } from 'react'
import { api } from '../api'
import type { Group } from '../api'

export function useGroup(groupId?: string) {
  const [group, setGroup] = useState<Group | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string>('')

  useEffect(() => {
    if (!groupId) return
    const ctrl = new AbortController()
    setLoading(true)
    setError('')
    api
      .getGroup(groupId)
      .then(setGroup)
      .catch((e) => setError(e?.message || '読み込みに失敗しました'))
      .finally(() => setLoading(false))
    return () => ctrl.abort()
  }, [groupId])

  return { group, loading, error }
}
