package com.godutch.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseManagerのテストクラス
 */
class DatabaseManagerTest {
    
    private DatabaseManager databaseManager;
    
    @BeforeEach
    void setUp() {
        databaseManager = new DatabaseManager();
    }
    
    @Test
    @DisplayName("DatabaseManagerのインスタンス化テスト")
    void testDatabaseManagerInstantiation() {
        assertNotNull(databaseManager, "DatabaseManagerが正常にインスタンス化されること");
    }
    
    @Test
    @DisplayName("設定の表示テスト")
    void testPrintConfiguration() {
        // 例外が発生しないことを確認
        assertDoesNotThrow(() -> databaseManager.printConfiguration(), 
                "設定の表示で例外が発生しないこと");
    }
    
    @Test
    @DisplayName("データベース接続取得テスト（接続不可ケースを疑似的に作る）")
    void testGetConnectionWithoutDatabase() {
        Properties p = new Properties();
        p.setProperty("db.url", "jdbc:mysql://127.0.0.1:13306/godutch"); // 未使用想定ポート
        p.setProperty("db.username", "root");
        p.setProperty("db.password", "password");
        p.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        DatabaseManager dm = new DatabaseManager(p);
        assertThrows(SQLException.class, dm::getConnection,
                "到達不能なポートを指定した場合、SQLExceptionが発生すること");
    }
    
    @Test
    @DisplayName("接続テスト（サーバー有無に依存しない）")
    void testConnectionTestWithoutServer() {
        // isValid 呼び出しで例外握りつぶし→ false か true を返す仕様なので例外は出ない
        assertDoesNotThrow(() -> databaseManager.testConnection(), 
                "接続テストで例外が発生しないこと");
    }
    
    // 注意: 以下のテストはMySQLサーバーが起動している場合のみ実行されます
    // 実際の統合テストでは、テスト用のデータベースを使用することを推奨します
    
    /*
    @Test
    @DisplayName("実際のデータベース接続テスト")
    @Disabled("MySQL サーバーが必要")
    void testRealDatabaseConnection() throws SQLException {
        assumeTrue(databaseManager.testConnection(), "MySQLサーバーが起動していること");
        
        try (Connection conn = databaseManager.getConnection()) {
            assertNotNull(conn, "データベース接続が取得できること");
            assertTrue(conn.isValid(5), "接続が有効であること");
        }
    }
    
    @Test
    @DisplayName("マイグレーション実行テスト")
    @Disabled("MySQL サーバーが必要")
    void testMigration() {
        assumeTrue(databaseManager.testConnection(), "MySQLサーバーが起動していること");
        
        assertDoesNotThrow(() -> databaseManager.migrate(), 
                "マイグレーションが正常に実行されること");
    }
    */
}
