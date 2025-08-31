import type { ApiError } from './types'

const API_BASE = '/api'

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'

export type RequestOptions = {
  headers?: Record<string, string>
  query?: Record<string, string | number | boolean | null | undefined>
  body?: unknown
  signal?: AbortSignal
}

function buildQuery(query?: RequestOptions['query']): string {
  if (!query) return ''
  const params = new URLSearchParams()
  for (const [k, v] of Object.entries(query)) {
    if (v === undefined || v === null) continue
    params.append(k, String(v))
  }
  const s = params.toString()
  return s ? `?${s}` : ''
}

async function request<T>(path: string, method: HttpMethod, opts: RequestOptions = {}): Promise<T> {
  const url = `${API_BASE}${path}${buildQuery(opts.query)}`
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...opts.headers,
  }

  const init: RequestInit = {
    method,
    headers,
    signal: opts.signal,
  }

  if (opts.body !== undefined) {
    init.body = typeof opts.body === 'string' ? opts.body : JSON.stringify(opts.body)
  }

  const res = await fetch(url, init)
  const text = await res.text()
  const isJson = res.headers.get('content-type')?.includes('application/json')
  const data = text ? (isJson ? JSON.parse(text) : (text as unknown)) : (null as unknown)

  if (!res.ok) {
    const err = new Error(
      typeof data === 'object' && data && 'message' in (data as any)
        ? String((data as any).message)
        : res.statusText
    ) as ApiError
    err.status = res.status
    err.details = data
    throw err
  }

  return data as T
}

export const http = {
  get: <T>(path: string, opts?: RequestOptions) => request<T>(path, 'GET', opts),
  post: <T>(path: string, body?: unknown, opts?: RequestOptions) =>
    request<T>(path, 'POST', { ...opts, body }),
  put: <T>(path: string, body?: unknown, opts?: RequestOptions) =>
    request<T>(path, 'PUT', { ...opts, body }),
  delete: <T>(path: string, opts?: RequestOptions) => request<T>(path, 'DELETE', opts),
}

export type { ApiError }
