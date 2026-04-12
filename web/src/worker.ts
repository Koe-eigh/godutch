type Env = {
  API_ORIGIN?: string
  ASSETS: {
    fetch: (request: Request | URL | string) => Promise<Response>
  }
}

const HOP_BY_HOP_HEADERS = new Set([
  'connection',
  'content-length',
  'host',
  'keep-alive',
  'proxy-authenticate',
  'proxy-authorization',
  'te',
  'trailer',
  'transfer-encoding',
  'upgrade',
])

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url)

    if (!url.pathname.startsWith('/api/')) {
      return env.ASSETS.fetch(request)
    }

    if (!env.API_ORIGIN) {
      return new Response('Missing API_ORIGIN environment variable', { status: 500 })
    }

    const upstreamPath = url.pathname.replace(/^\/api/, '') || '/'
    const targetUrl = new URL(`${upstreamPath}${url.search}`, ensureTrailingSlash(env.API_ORIGIN))
    const headers = new Headers(request.headers)

    for (const header of HOP_BY_HOP_HEADERS) {
      headers.delete(header)
    }

    const init: RequestInit & { duplex?: 'half' } = {
      method: request.method,
      headers,
      redirect: 'manual',
    }

    if (request.method !== 'GET' && request.method !== 'HEAD') {
      init.body = request.body
      init.duplex = 'half'
    }

    const response = await fetch(targetUrl, init)
    const responseHeaders = new Headers(response.headers)

    for (const header of HOP_BY_HOP_HEADERS) {
      responseHeaders.delete(header)
    }

    return new Response(response.body, {
      status: response.status,
      statusText: response.statusText,
      headers: responseHeaders,
    })
  },
}

function ensureTrailingSlash(value: string): string {
  return value.endsWith('/') ? value : `${value}/`
}
