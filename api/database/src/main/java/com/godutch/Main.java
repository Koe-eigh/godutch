package com.godutch;

import com.godutch.database.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== GoDutch データベースモジュール ===");
        
        DatabaseManager dbManager = new DatabaseManager();
        
        // データベース設定を表示
        dbManager.printConfiguration();
        
        // データベース接続をテスト
        System.out.println("\n=== 接続テスト ===");
        if (dbManager.testConnection()) {
            System.out.println("✓ データベースに正常に接続できました");
            
            // マイグレーションを実行
            System.out.println("\n=== マイグレーション実行 ===");
            try {
                dbManager.migrate();
                System.out.println("✓ マイグレーションが正常に完了しました");
            } catch (Exception e) {
                System.err.println("✗ マイグレーションに失敗しました: " + e.getMessage());
            }
        } else {
            System.err.println("✗ データベースに接続できませんでした");
            System.out.println("\nMySQLサーバーが起動していることを確認してください。");
            System.out.println("また、以下のデータベースが存在することを確認してください: godutch");
            System.out.println("\nMySQLで以下のコマンドを実行してデータベースを作成してください:");
            System.out.println("CREATE DATABASE IF NOT EXISTS godutch CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
        }
    }
}