# GoDutch Database Module

このモジュールは、GoDutchアプリケーションのデータベース管理を担当します。FlywayによるマイグレーションとMySQL JDBCコネクションを使用しています。

## 前提条件

- Java 17以上
- Maven 3.6以上
- MySQL 8.0以上

## セットアップ

### 1. MySQLサーバーの起動

MySQLサーバーが起動していることを確認してください。

### 2. データベースの作成

MySQLに接続し、以下のコマンドでデータベースを作成してください：

```sql
CREATE DATABASE IF NOT EXISTS godutch CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. データベース設定

`src/main/resources/database.properties`ファイルで、データベース接続設定を確認・変更してください：

```properties
db.url=jdbc:mysql://localhost:3306/godutch?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo
db.username=root
db.password=password
db.driver=com.mysql.cj.jdbc.Driver
```

## 使用方法

### プロジェクトのビルド

```bash
mvn clean compile
```

### テストの実行

```bash
mvn test
```

### アプリケーションの実行

```bash
mvn exec:java -Dexec.mainClass="com.godutch.Main"
```

### マイグレーションの実行

Mavenプラグインを使用してマイグレーションを実行：

```bash
mvn flyway:migrate
```

### マイグレーション情報の確認

```bash
mvn flyway:info
```

### マイグレーションの履歴確認

```bash
mvn flyway:history
```

## データベーススキーマ

初期マイグレーション（V1）では以下のテーブルが作成されます：

- `users` - ユーザー情報
- `groups_table` - グループ情報
- `group_members` - グループメンバー
- `expenses` - 支出情報
- `expense_splits` - 支出分割情報

## クラス構成

### DatabaseManager

データベース接続とマイグレーションを管理するメインクラス。

主要メソッド：
- `getConnection()` - データベース接続を取得
- `migrate()` - Flywayマイグレーションを実行
- `testConnection()` - 接続テスト
- `printConfiguration()` - 設定情報の表示

## トラブルシューティング

### 接続エラーが発生する場合

1. MySQLサーバーが起動していることを確認
2. データベース名、ユーザー名、パスワードが正しいことを確認
3. MySQLのポート番号（デフォルト：3306）が使用可能であることを確認

### マイグレーションエラーが発生する場合

1. データベースが存在することを確認
2. ユーザーに適切な権限があることを確認
3. 既存のマイグレーション履歴と矛盾がないことを確認

## 開発者向け情報

### 新しいマイグレーションの追加

1. `src/main/resources/db/migration/`ディレクトリに新しいSQLファイルを作成
2. ファイル名は`V{version}__{description}.sql`の形式に従う
3. バージョン番号は既存のものより大きい値を使用

例：`V2__Add_user_preferences_table.sql`

### テストの追加

JUnit 5を使用してテストを作成してください。テストクラスは`src/test/java/`ディレクトリに配置します。
