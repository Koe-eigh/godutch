import { useRef } from 'react'
import { Link, Outlet } from 'react-router-dom'
import styles from '../styles/Layout.module.css'

export function Layout() {
  const menuRef = useRef<HTMLDetailsElement>(null)

  const closeMenu = () => {
    menuRef.current?.removeAttribute('open')
  }

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <div className={styles.headerContent}>
          <div className={styles.brand}>GoDutch</div>
          <details ref={menuRef} className={styles.menu}>
            <summary className={styles.menuButton} aria-label="メニュー">
              <span className={styles.menuIcon} aria-hidden="true">
                <span />
                <span />
                <span />
              </span>
            </summary>
            <nav className={styles.menuPanel} aria-label="メインメニュー">
              <Link to="/" className={styles.menuLink} onClick={closeMenu}>
                新規グループ作成
              </Link>
            </nav>
          </details>
        </div>
      </header>
      <main className={styles.main}>
        <Outlet />
      </main>
      <footer className={styles.footer}>© GoDutch</footer>
    </div>
  )
}
