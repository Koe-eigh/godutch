# GoDutch Application Module

このモジュールは、GoDutchアプリケーションのアプリケーション層を担当し、ドメイン層を使用したユースケースの実装を管理します。

## アーキテクチャ概要

このモジュールは以下のレイヤードアーキテクチャの**アプリケーション層**を実装しています：

```
┌─────────────────────────────────────┐
│          Presentation Layer         │  (外部：REST API、CLI等)
├─────────────────────────────────────┤
│         Application Layer           │  ← このモジュール
├─────────────────────────────────────┤
│           Domain Layer              │  (coreモジュール)
├─────────────────────────────────────┤
│        Infrastructure Layer         │  (databaseモジュール)
└─────────────────────────────────────┘
```

## 主要コンポーネント

### 1. Application Services (`service/`)
ユースケースを実装するサービスクラス群。ドメインオブジェクトを協調させてビジネスロジックを実行します。

- `GroupApplicationService` - グループ管理のユースケース

### 2. DTOs (`dto/`)
レイヤー間でデータを転送するためのデータ転送オブジェクト。

- `GroupDto` - グループ情報のDTO
- `MemberDto` - メンバー情報のDTO

### 3. Commands (`command/`)
アプリケーションサービスへの入力を表現するコマンドオブジェクト。

- `CreateGroupCommand` - グループ作成コマンド
- `AddMemberCommand` - メンバー追加コマンド

### 4. Queries (`query/`)
読み取り専用の操作を表現するクエリオブジェクト（将来拡張用）。

### 5. Use Cases (`usecase/`)
より複雑なユースケースのインターフェース定義（将来拡張用）。

## 設計原則

### 1. 依存関係の方向
- アプリケーション層 → ドメイン層（coreモジュール）
- アプリケーション層 → インフラストラクチャ層（databaseモジュール）

### 2. 責務の分離
- **Application Services**: ユースケースの調整
- **DTOs**: データ転送
- **Commands**: 操作の入力
- **Queries**: 読み取り操作の入力

### 3. バリデーション
- Bean Validation（Jakarta Validation）を使用した入力検証
- ドメインオブジェクトへの委譲による業務ルール検証

## 依存関係

### 内部依存関係
- `core` - ドメインオブジェクト（Group, Member等）
- `database` - リポジトリ実装

### 外部ライブラリ
- **JUnit 5** - ユニットテスト
- **Mockito** - モック作成
- **AssertJ** - 流暢なアサーション
- **Jakarta Validation** - 入力検証
- **Hibernate Validator** - Validation実装
- **SLF4J + Logback** - ロギング

## 使用方法

### プロジェクトのビルド
```bash
mvn clean compile
```

### テストの実行
```bash
mvn test
```

### 統合テストの実行
```bash
mvn integration-test
```

### コードカバレッジレポートの生成
```bash
mvn test jacoco:report
```

## 実装例

### グループ作成のユースケース

```java
// Command作成
CreateGroupCommand command = new CreateGroupCommand("新しいグループ");

// ApplicationService経由で実行
GroupApplicationService service = new GroupApplicationService(groupRepository);
GroupDto result = service.createGroup(command);

System.out.println("作成されたグループ: " + result.getName());
```

### メンバー追加のユースケース

```java
// Command作成
AddMemberCommand command = new AddMemberCommand("group-id", "新しいメンバー");

// ApplicationService経由で実行
GroupDto result = service.addMember(command);

System.out.println("メンバー数: " + result.getMembers().size());
```

## テスト戦略

### 1. ユニットテスト
- Application Servicesの各メソッドを個別にテスト
- リポジトリはモックを使用
- 正常系・異常系の両方をカバー

### 2. 統合テスト
- 実際のデータベースを使用したエンドツーエンドテスト
- トランザクション境界の検証

### 3. テストデータ
- テスト専用のファクトリクラスでテストデータを生成
- 各テストは独立して実行可能

## ログ設定

### ログレベル
- **DEBUG**: 開発時の詳細な情報
- **INFO**: 業務上重要な操作のログ
- **WARN**: 警告メッセージ
- **ERROR**: エラー情報

### ログファイル
- `logs/app.log` - アプリケーションログ
- 日次ローテーション、最大30日保持

## 拡張ガイド

### 新しいユースケースの追加
1. Commandクラスの作成（`command/`）
2. DTOクラスの作成（`dto/`）
3. Application Serviceメソッドの追加
4. ユニットテストの作成

### 新しいドメインオブジェクトの対応
1. DTOクラスの作成
2. 変換メソッドの実装
3. Application Serviceの拡張

## パフォーマンス考慮事項

- **N+1問題の回避**: リポジトリでの一括取得
- **キャッシュ戦略**: 頻繁にアクセスされるデータのキャッシュ
- **バッチ処理**: 大量データ処理時の分割処理

## セキュリティ考慮事項

- **入力検証**: すべての外部入力のValidation
- **認可**: ユーザー権限の確認（将来実装）
- **監査ログ**: 重要な操作のログ記録
