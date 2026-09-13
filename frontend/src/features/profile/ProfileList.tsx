import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { createProfile, fetchProfiles, profileKeys } from './api'

export function ProfileList() {
  const queryClient = useQueryClient()
  const [displayName, setDisplayName] = useState('')
  const [headline, setHeadline] = useState('')

  const profiles = useQuery({
    queryKey: profileKeys.all,
    queryFn: fetchProfiles,
  })

  const create = useMutation({
    mutationFn: createProfile,
    onSuccess: async () => {
      setDisplayName('')
      setHeadline('')
      await queryClient.invalidateQueries({ queryKey: profileKeys.all })
    },
  })

  return (
    <section className="mx-auto max-w-2xl p-8">
      <h1 className="text-2xl font-bold tracking-tight">CareerDeck</h1>
      <p className="mt-1 text-sm text-ink-400">スキルと経歴を登録して公開する</p>

      <form
        className="mt-6 flex flex-col gap-3"
        onSubmit={(event) => {
          event.preventDefault()
          create.mutate({ displayName, headline })
        }}
      >
        <label className="flex flex-col gap-1 text-sm">
          <span className="font-medium">表示名</span>
          <input
            className="rounded border border-ink-400/40 px-3 py-2"
            value={displayName}
            onChange={(event) => setDisplayName(event.target.value)}
            required
            maxLength={100}
          />
        </label>

        <label className="flex flex-col gap-1 text-sm">
          <span className="font-medium">ヘッドライン</span>
          <input
            className="rounded border border-ink-400/40 px-3 py-2"
            value={headline}
            onChange={(event) => setHeadline(event.target.value)}
            maxLength={200}
          />
        </label>

        <button
          type="submit"
          className="self-start rounded bg-accent-500 px-4 py-2 text-sm font-medium text-white hover:bg-accent-600 disabled:opacity-50"
          disabled={create.isPending || displayName.trim() === ''}
        >
          {create.isPending ? '登録中…' : '登録する'}
        </button>

        {create.isError && (
          <p role="alert" className="text-sm text-red-600">
            登録に失敗しました: {create.error.message}
          </p>
        )}
      </form>

      <h2 className="mt-10 text-lg font-semibold">登録済みプロフィール</h2>
      {profiles.isPending && <p className="mt-2 text-sm text-ink-400">読み込み中…</p>}
      {profiles.isError && (
        <p role="alert" className="mt-2 text-sm text-red-600">
          取得に失敗しました: {profiles.error.message}
        </p>
      )}
      <ul className="mt-3 flex flex-col gap-2">
        {profiles.data?.map((profile) => (
          <li key={profile.id} className="rounded border border-ink-400/20 p-4">
            <p className="font-medium">{profile.displayName}</p>
            {profile.headline !== '' && <p className="text-sm text-ink-400">{profile.headline}</p>}
          </li>
        ))}
      </ul>
      {profiles.data?.length === 0 && (
        <p className="mt-3 text-sm text-ink-400">まだ登録がありません。</p>
      )}
    </section>
  )
}
