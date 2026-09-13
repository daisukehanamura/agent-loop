import { apiClient } from '@/lib/apiClient'
import type { CreateProfileInput, Profile } from './types'

export const profileKeys = {
  all: ['profiles'] as const,
  detail: (id: string) => ['profiles', id] as const,
}

export const fetchProfiles = () => apiClient.get<Profile[]>('/profiles')

export const createProfile = (input: CreateProfileInput) =>
  apiClient.post<Profile>('/profiles', input)
