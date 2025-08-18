import type { Group, GroupInput, PaymentEvent, PaymentEventInput, Settlement } from './types'
import {
  createGroup as _createGroup,
  getGroup as _getGroup,
  listEvents as _listEvents,
  addEvent as _addEvent,
  getEvent as _getEvent,
  updateEvent as _updateEvent,
  deleteEvent as _deleteEvent,
  getSettlement as _getSettlement,
} from './endpoints'

export class ApiClient {
  createGroup(body: GroupInput): Promise<Group> {
    return _createGroup(body)
  }
  getGroup(groupId: string): Promise<Group> {
    return _getGroup(groupId)
  }
  listEvents(groupId: string): Promise<PaymentEvent[]> {
    return _listEvents(groupId)
  }
  addEvent(groupId: string, body: PaymentEventInput): Promise<PaymentEvent> {
    return _addEvent(groupId, body)
  }
  getEvent(groupId: string, eventId: string): Promise<PaymentEvent> {
    return _getEvent(groupId, eventId)
  }
  updateEvent(groupId: string, eventId: string, body: PaymentEventInput): Promise<PaymentEvent> {
    return _updateEvent(groupId, eventId, body)
  }
  deleteEvent(groupId: string, eventId: string): Promise<void> {
    return _deleteEvent(groupId, eventId)
  }
  getSettlement(groupId: string): Promise<Settlement[]> {
    return _getSettlement(groupId)
  }
}

export const api = new ApiClient()
export * from './types'
