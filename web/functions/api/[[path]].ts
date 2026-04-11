/* eslint-env worker */
/* global PagesFunction */

type Env = {
  API_ORIGIN?: string
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

export const onRequest: PagesFunction<Env> = async ({ request, env, params }) => {
  if (!env.API_ORIGIN) {
    return new Response('Missing API_ORIGIN environment variable', { status: 500 })
  }

  const path = Array.isArray(params.path) ? params.path.join('/') : params.path || ''
  const incomingUrl = new URL(request.url)
  const targetUrl = new URL(`/${path}${incomingUrl.search}`, ensureTrailingSlash(env.API_ORIGIN))

  const headers = new Headers(request.headers)
  for (const header of HOP_BY_HOP_HEADERS) {
    headers.delete(header)
  }

  const init = {
    method: request.method,
    headers,
    redirect: 'manual',
  }

  if (request.method !== 'GET' && request.method !== 'HEAD') {
    init.body = request.body
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
}

function ensureTrailingSlash(value: string): string {
  return value.endsWith('/') ? value : `${value}/`
}
