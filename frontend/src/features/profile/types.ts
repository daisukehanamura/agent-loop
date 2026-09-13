export type Profile = {
  id: string
  displayName: string
  headline: string
  createdAt: string
  updatedAt: string
}

export type CreateProfileInput = {
  displayName: string
  headline: string
}
