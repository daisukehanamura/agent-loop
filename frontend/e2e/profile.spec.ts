import { expect, test } from '@playwright/test'

test.describe('プロフィール登録', () => {
  test('登録したプロフィールが一覧に表示される', async ({ page }) => {
    const displayName = `e2e-${Date.now()}`

    await page.goto('/')
    await expect(page.getByRole('heading', { name: 'CareerDeck' })).toBeVisible()

    await page.getByLabel('表示名').fill(displayName)
    await page.getByLabel('ヘッドライン').fill('E2E テストユーザー')
    await page.getByRole('button', { name: '登録する' }).click()

    await expect(page.getByText(displayName)).toBeVisible()
  })
})
