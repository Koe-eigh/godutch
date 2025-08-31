import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Layout } from './components/Layout'
import HomePage from './pages/HomePage'
import GroupPage from './pages/GroupPage'
import EventsListPage from './pages/EventsListPage'
import NewEventPage from './pages/NewEventPage'
import EventDetailPage from './pages/EventDetailPage'
import SettlementPage from './pages/SettlementPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<HomePage />} />
          <Route path="groups/:groupId" element={<GroupPage />}>            
            <Route path="events" element={<EventsListPage />} />
            <Route path="events/new" element={<NewEventPage />} />
            <Route path="events/:eventId" element={<EventDetailPage />} />
            <Route path="settlement" element={<SettlementPage />} />
          </Route>
          <Route path="*" element={<p>ページが見つかりません</p>} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
