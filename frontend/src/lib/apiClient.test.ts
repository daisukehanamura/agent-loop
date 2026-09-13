import { describe, expect, it, vi } from 'vitest'
import { jsonResponse } from '@/test/renderWithProviders'
import { ApiError, apiClient } from './apiClient'

describe('apiClient', () => {
  it('成功時に JSON をパースして返す', async () => {
    vi.mocked(fetch).mockResolvedValueOnce(jsonResponse({ id: '1' }))

    await expect(apiClient.get('/profiles')).resolves.toEqual({ id: '1' })
  })

  it('エラー時に ProblemDetail を持つ ApiError を投げる', async () => {
    vi.mocked(fetch).mockResolvedValueOnce(
      jsonResponse({ title: 'Validation Failed', detail: 'bad request' }, 400),
    )

    await expect(apiClient.post('/profiles', {})).rejects.toMatchObject({
      name: 'ApiError',
      status: 400,
      message: 'bad request',
    })
  })

  it('204 は undefined を返す', async () => {
    vi.mocked(fetch).mockResolvedValueOnce(new Response(null, { status: 204 }))

    await expect(apiClient.get('/profiles')).resolves.toBeUndefined()
  })

  it('ApiError は status と problem を保持する', () => {
    const error = new ApiError(404, { title: 'Not Found' })

    expect(error.status).toBe(404)
    expect(error.problem.title).toBe('Not Found')
  })
})
