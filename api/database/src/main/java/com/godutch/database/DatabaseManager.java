package com.godutch.database;

import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

/**
 * データベース接続とマイグレーションを管理するクラス
 */
public class DatabaseManager {
    private static final String PROPERTIES_FILE = "/database.properties";
    private Properties properties;
    
    public DatabaseManager() {
        loadProperties();
    }

    /**
     * テスト/外部から任意の設定を直接注入するためのコンストラクタ。
     * 必須キー(db.url, db.username, db.password, db.driver) が無い場合は不足分をデフォルトで補完します。
     */
    public DatabaseManager(Properties properties) {
        this.properties = new Properties();
        setDefaultProperties(); // まずデフォルト
        if (properties != null) {
            for (String name : properties.stringPropertyNames()) {
                this.properties.setProperty(name, properties.getProperty(name));
            }
        }
    }
    
    /**
     * データベース設定をプロパティファイルから読み込む
     */
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                // デフォルト設定
                setDefaultProperties();
            }
        } catch (IOException e) {
            System.err.println("プロパティファイルの読み込みに失敗しました: " + e.getMessage());
            setDefaultProperties();
        }
    }
    
    /**
     * デフォルトのデータベース設定を設定
     */
    private void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/godutch");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "password");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }
    
    /**
     * データベース接続を取得
     * @return データベース接続
     * @throws SQLException データベース接続エラー
     */
    public Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        String driver = properties.getProperty("db.driver");
        
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBCドライバが見つかりません: " + driver, e);
        }
        
        return DriverManager.getConnection(url, username, password);
    }
    
    /**
     * Flywayを使用してデータベースマイグレーションを実行
     */
    public void migrate() {
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        
        Flyway flyway = Flyway.configure()
                .dataSource(url, username, password)
                .locations("classpath:db/migration")
                .load();
        
        try {
            flyway.migrate();
            System.out.println("データベースマイグレーションが正常に完了しました。");
        } catch (Exception e) {
            System.err.println("マイグレーションエラー: " + e.getMessage());
            throw new RuntimeException("マイグレーションに失敗しました", e);
        }
    }
    
    /**
     * データベース接続をテスト
     * @return 接続が成功した場合true
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection.isValid(5); // 5秒でタイムアウト
        } catch (SQLException e) {
            System.err.println("データベース接続テストに失敗しました: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 現在のデータベース設定を表示
     */
    public void printConfiguration() {
        System.out.println("=== データベース設定 ===");
        System.out.println("URL: " + properties.getProperty("db.url"));
        System.out.println("ユーザー名: " + properties.getProperty("db.username"));
        System.out.println("ドライバ: " + properties.getProperty("db.driver"));
    }
}
