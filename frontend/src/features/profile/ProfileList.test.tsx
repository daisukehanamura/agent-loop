import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { jsonResponse, renderWithProviders } from '@/test/renderWithProviders'
import { ProfileList } from './ProfileList'

const profile = {
  id: '11111111-1111-1111-1111-111111111111',
  displayName: 'hanamaru',
  headline: 'Backend Engineer',
  createdAt: '2026-09-13T00:00:00Z',
  updatedAt: '2026-09-13T00:00:00Z',
}

describe('ProfileList', () => {
  it('取得したプロフィールを一覧表示する', async () => {
    vi.mocked(fetch).mockResolvedValue(jsonResponse([profile]))

    renderWithProviders(<ProfileList />)

    expect(await screen.findByText('hanamaru')).toBeInTheDocument()
    expect(screen.getByText('Backend Engineer')).toBeInTheDocument()
  })

  it('空のときは案内を表示する', async () => {
    vi.mocked(fetch).mockResolvedValue(jsonResponse([]))

    renderWithProviders(<ProfileList />)

    expect(await screen.findByText('まだ登録がありません。')).toBeInTheDocument()
  })

  it('表示名が未入力なら登録ボタンが押せない', async () => {
    vi.mocked(fetch).mockResolvedValue(jsonResponse([]))

    renderWithProviders(<ProfileList />)

    expect(screen.getByRole('button', { name: '登録する' })).toBeDisabled()
  })

  it('フォーム送信で POST し、一覧を再取得する', async () => {
    vi.mocked(fetch)
      .mockResolvedValueOnce(jsonResponse([]))
      .mockResolvedValueOnce(jsonResponse(profile, 201))
      .mockResolvedValue(jsonResponse([profile]))

    renderWithProviders(<ProfileList />)
    await screen.findByText('まだ登録がありません。')

    await userEvent.type(screen.getByLabelText('表示名'), 'hanamaru')
    await userEvent.click(screen.getByRole('button', { name: '登録する' }))

    await waitFor(() => {
      expect(vi.mocked(fetch)).toHaveBeenCalledWith(
        '/api/profiles',
        expect.objectContaining({ method: 'POST' }),
      )
    })
    expect(await screen.findByText('hanamaru')).toBeInTheDocument()
  })

  it('取得失敗時にエラーを表示する', async () => {
    vi.mocked(fetch).mockResolvedValue(jsonResponse({ detail: 'boom' }, 500))

    renderWithProviders(<ProfileList />)

    expect(await screen.findByRole('alert')).toHaveTextContent('取得に失敗しました')
  })
})
