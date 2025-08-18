import { http } from './http'
import type {
  Group,
  GroupInput,
  PaymentEvent,
  PaymentEventInput,
  Settlement,
} from './types'

// /groups
export async function createGroup(body: GroupInput): Promise<Group> {
  return http.post<Group>('/groups', body)
}

export async function getGroup(groupId: string): Promise<Group> {
  return http.get<Group>(`/groups/${encodeURIComponent(groupId)}`)
}

// /groups/{groupId}/events
export async function listEvents(groupId: string): Promise<PaymentEvent[]> {
  return http.get<PaymentEvent[]>(`/groups/${encodeURIComponent(groupId)}/events`)
}

export async function addEvent(
  groupId: string,
  body: PaymentEventInput,
): Promise<PaymentEvent> {
  return http.post<PaymentEvent>(`/groups/${encodeURIComponent(groupId)}/events`, body)
}

// /groups/{groupId}/events/{eventId}
export async function getEvent(groupId: string, eventId: string): Promise<PaymentEvent> {
  return http.get<PaymentEvent>(
    `/groups/${encodeURIComponent(groupId)}/events/${encodeURIComponent(eventId)}`,
  )
}

export async function updateEvent(
  groupId: string,
  eventId: string,
  body: PaymentEventInput,
): Promise<PaymentEvent> {
  return http.put<PaymentEvent>(
    `/groups/${encodeURIComponent(groupId)}/events/${encodeURIComponent(eventId)}`,
    body,
  )
}

export async function deleteEvent(groupId: string, eventId: string): Promise<void> {
  await http.delete<unknown>(
    `/groups/${encodeURIComponent(groupId)}/events/${encodeURIComponent(eventId)}`,
  )
}

// /groups/{groupId}/settlement
export async function getSettlement(groupId: string): Promise<Settlement[]> {
  return http.get<Settlement[]>(`/groups/${encodeURIComponent(groupId)}/settlement`)
}
