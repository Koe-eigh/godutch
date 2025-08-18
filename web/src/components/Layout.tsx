import { Link, Outlet } from 'react-router-dom'
import styles from '../styles/Layout.module.css'

export function Layout() {
  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <div className={styles.brand}>GoDutch</div>
        <nav className={styles.nav}>
          <Link to="/" className={styles.navLink}>Home</Link>
        </nav>
      </header>
      <main className={styles.main}>
        <Outlet />
      </main>
      <footer className={styles.footer}>© GoDutch</footer>
    </div>
  )
}
