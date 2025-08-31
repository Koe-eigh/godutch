// OpenAPI 3.1 (doc/apidoc/openapi.yaml) に基づく型定義（MVP 想定）

export interface Member {
  id: string
  name: string
}

export interface MemberInput {
  name: string
}

export interface Group {
  id: string
  name: string
  description?: string
  members: Member[]
}

export interface Creditor {
  memberId: string
  amount: string // 金額は文字列（小数/大きい数サポートのため）
}

export interface Debtor {
  memberId: string
  amount: string
}

export interface PaymentEvent {
  id: string
  groupId: string
  title: string
  memo?: string
  creditors: Creditor[]
  debtors: Debtor[]
}

export interface PaymentEventInput {
  title: string
  memo?: string
  creditors: Creditor[]
  debtors: Debtor[]
}

export interface GroupInput {
  name: string
  description?: string
  members: MemberInput[]
}

export interface Settlement {
  payerId: string
  payeeId: string
  amount: string
}

export interface ApiError extends Error {
  status: number
  details?: unknown
}
